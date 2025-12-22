package com.logtest.masker;

import com.logtest.masker.annotations.Masked;
import com.logtest.masker.annotations.MaskedProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.time.temporal.Temporal;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class Masker {

    private static final String IS_MASKED_FIELD = "isMasked";

    private final CollectionMasker collectionMasker;
    private final List<MaskProcessor> maskProcessors;
    private Map<MaskPatternType, MaskProcessor> maskProcessorMap;

    @Autowired
    public Masker(CollectionMasker collectionMasker, List<MaskProcessor> maskProcessors) {
        this.collectionMasker = collectionMasker;
        this.collectionMasker.setMaskFunction(this::processRecursively); //ToDo - нужно ли
        this.maskProcessors = maskProcessors;
        this.maskProcessorMap = maskProcessors.stream().collect(Collectors.toMap(MaskProcessor::getType, Function.identity()));
    }

    public <T> T mask(T dto) {
        return processRecursively(dto, new IdentityHashMap<>());
    }

    private <T> T processRecursively(T dto, Map<Object, Object> processed) {

        if (dto == null) {
            return null;
        } else if (processed.containsKey(dto)) {
            return (T) processed.get(dto);
        } else if (!dto.getClass().isAnnotationPresent(Masked.class)) {
            return dto;
        } else {
            return createDtoMaskedInstance(dto, processed)
                .orElseGet(() -> {
                    log.error("Error during masking: {}", dto.getClass().getSimpleName());
                    return dto;
                });
        }
    }

    private <T> Optional<T> createDtoMaskedInstance(T source, Map<Object, Object> processed) {
        Optional<T> result;
        try {
            result = Optional.of(((Class<T>) source.getClass()).getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            log.error("Failed to create instance of dto: {}", e.getMessage());
            result = Optional.empty();
        }
        return result
            .map(maskedInstance -> {
                processed.put(source, maskedInstance);
                copyAndMaskFields(source, maskedInstance, processed);
                setMaskedFlag(maskedInstance);
                return maskedInstance;
            });
    }

    private void copyAndMaskFields(Object source, Object target, Map<Object, Object> processed) {
        ReflectionUtils.doWithFields(
            source.getClass(),
            field -> {
                ReflectionUtils.makeAccessible(field);
                Object value = ReflectionUtils.getField(field, source);
                Object maskedValue = processFieldValue(field, value, processed);
                ReflectionUtils.setField(field, target, maskedValue);
            },
            field -> !field.isSynthetic()
        );
    }

    private Object processFieldValue(Field field, Object value, Map<Object, Object> processed) {

        if (value == null) {
            return null;
        } else if (value instanceof Temporal) {
            return processTemporalValue(field, value);
        } else if (value instanceof String) {
            return processStringValue(field, (String) value);
        } else if (value instanceof List) {
            return collectionMasker.processList((List<?>) value, field, processed);
        } else if (value instanceof Set) {
            return collectionMasker.processSet((Set<?>) value, field, processed);
        } else if (value instanceof Map) {
            return collectionMasker.processMap((Map<?, ?>) value, field, processed);
        } else if (value.getClass().isArray()) {
            return collectionMasker.processArray(value, processed);
        } else if (value.getClass().isAnnotationPresent(Masked.class)) {
            return processRecursively(value, processed);
        } else {
            return value;
        }
    }

    private Object processTemporalValue(Field field, Object value) {

        return processFieldByMaskProcessor(field, value);
    }

    private /*String*/Object processStringValue(Field field, String value) {
        //1 вариант
/*        return (String) Optional.ofNullable(field.getAnnotation(MaskedProperty.class))
                .map(MaskedProperty::type)
                .map(type -> maskStringProcessorMap.get(type))
                .map(maskProcessor -> maskProcessor.process(value))
                .orElse(value);
        */
        //2 вариант

        return processFieldByMaskProcessor(field, value);

        // return Optional.ofNullable(field.getAnnotation(MaskedProperty.class))
        //     .map(annotation -> switch (annotation.type()) {
        //         case CUSTOM -> value.replaceAll(annotation.pattern(), annotation.replacement());
        //         case TEXT_FIELD -> MaskUtils.maskedTextField(value);
        //         case NAME -> MaskUtils.maskedName(value);
        //         case EMAIL -> MaskUtils.maskedEmail(value);
        //         case PHONE -> MaskUtils.maskedPhoneNumber(value);
        //         case FULL_ADDRESS -> MaskUtils.maskFullAddress(value);
        //         case CONFIDENTIAL_NUMBER -> MaskUtils.maskedConfidentialNumber(value);
        //         case PIN -> MaskUtils.maskedPIN(value);
        //         case PAN -> MaskUtils.maskedPAN(value);
        //         case BALANCE -> MaskUtils.maskedBalance(value);
        //         case PASSPORT_SERIES -> MaskUtils.maskedPassportSeries(value);
        //         case PASSPORT_NUMBER -> MaskUtils.maskedPassportNumber(value);
        //         case PASSPORT -> MaskUtils.maskedPassport(value);
        //         case ISSUER_CODE -> MaskUtils.maskedIssuerCode(value);
        //         case ISSUER_NAME -> MaskUtils.maskedIssuerName(value);
        //         case OTHER_DUL_SERIES -> MaskUtils.maskedOtherDulSeries(value);
        //         case OTHER_DUL_NUMBER -> MaskUtils.maskedOtherDulNumber(value);
        //         case INN_FL_OR_UL -> MaskUtils.maskInnUlOrFL(value);
        //         case KPP -> MaskUtils.maskKpp(value);
        //         case OGRN_UL_OR_IP -> MaskUtils.maskOgrnUlOrOgrnIp(value);
        //         case OKPO -> MaskUtils.maskOkpo(value);
        //         case JWT_TYK_API_KEY_IP_ADDRESS -> MaskUtils.maskedJwtTykApiKeyIpAddress(value);
        //         case SNILS -> MaskUtils.maskSnils(value);
        //         default -> value;
        //     })
        //     .orElse(value);
    }

    private Object processFieldByMaskProcessor(Field field, Object value) { //Или сделать generic
        if (field.getAnnotation(MaskedProperty.class) != null) {
            MaskPatternType maskPatternType = field.getAnnotation(MaskedProperty.class).type();
            MaskProcessor maskProcessor = maskProcessorMap.get(maskPatternType);
            if (Objects.isNull(maskProcessor)) {
                // ToDo - свалится ли весь процесс маскирования, если не найден процессор для анотации.
                throw new IllegalArgumentException(String.format("Не найден процессор для типа маскирования %s", maskPatternType));
            }
            return maskProcessor.process(value);
        } else {
            return value;
        }
    }

    private void setMaskedFlag(Object dto) {
        Field field = ReflectionUtils.findField(dto.getClass(), IS_MASKED_FIELD);
        if (field == null) return;

        Class<?> fieldType = field.getType();
        if (fieldType != boolean.class && fieldType != Boolean.class) return;

        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, dto, true);
    }
}