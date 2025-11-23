package com.logtest.masker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MaskingUtils {

    public static <T> T mask(T dto) {

        if (dto == null) return null;

        Class<?> dtoClass = dto.getClass();

        if (!dtoClass.isAnnotationPresent(Masked.class)) return dto;

        try {
            T maskedDto = (T) dtoClass.getDeclaredConstructor().newInstance();
            copyFields(dto, maskedDto, dtoClass);
            applyMasking(maskedDto, dtoClass);
            return maskedDto;

        } catch (Exception e) {
            System.err.println("Error during masking: " + e.getMessage());
            return dto;
        }
    }

    private static <T> void copyFields(T source, T target, Class<?> clazz) throws IllegalAccessException {
        for (Field field : getAllFields(clazz)) {
            field.setAccessible(true);
            Object value = field.get(source);
            field.set(target, value);
        }
    }

    private static <T> void applyMasking(T dto, Class<?> dtoClass) throws IllegalAccessException {
        for (Field field : getAllFields(dtoClass)) {
            MaskedProperty annotation = field.getAnnotation(MaskedProperty.class);

            if (annotation != null && field.getType().equals(String.class)) {
                field.setAccessible(true);
                String value = (String) field.get(dto);

                if (value != null) {
                    String maskedValue = value.replaceAll(annotation.pattern(), annotation.replacement());
                    field.set(dto, maskedValue);
                }
            }
        }
    }

    private static List<Field> getAllFields(Class<?> dtoClass) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = dtoClass;

        while (currentClass != null && currentClass != Object.class) {
            Collections.addAll(fields, currentClass.getDeclaredFields());
            currentClass = currentClass.getSuperclass();
        }

        return fields;
    }
}