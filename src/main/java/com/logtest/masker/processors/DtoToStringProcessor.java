package com.logtest.masker.processors;

import com.logtest.masker.annotations.Masked;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class DtoToStringProcessor {

    public static String maskDates(String toStringResult) {
        Pattern pattern = Pattern.compile("(-0001|0000|0001)(-\\d{2}-\\d{2})");
        return pattern.matcher(toStringResult).replaceAll("****$2");
    }

    //ToDo делать maskDates напрямую в каждом поле
    public static String convertDtoToStringAndMaskDates(Object dto) {
        return processRecursively(dto, new IdentityHashMap<>());
    }

    private static String processRecursively(Object dto, Map<Object, Object> processed) {
        if (dto == null) {
            return "null";
        } else if (processed.containsKey(dto)) {
            return "cyclic reference";
        }

        processed.put(dto, dto);

        Class<?> clazz = dto.getClass();
        StringBuilder sb = new StringBuilder();

        sb.append(clazz.getSimpleName()).append("(");

        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);

            if (field.isSynthetic()) continue;

            sb.append(field.getName()).append("=");

            try {
                sb.append(processFieldValue(field.get(dto), processed));
            } catch (IllegalAccessException e) {
                log.warn("Cannot access field for toString: {}", e.getMessage());
                sb.append("...");
            }

            if (i < fields.length - 1) {
                sb.append(", ");
            }
        }

        sb.append(")");
        processed.remove(dto);
        return sb.toString();
    }

    private static String processFieldValue(Object value, Map<Object, Object> processed) {
        if (value == null) {
            return "null";
        } else if (value.getClass().isAnnotationPresent(Masked.class)) {
            return processRecursively(value, processed);
        } else if (value instanceof Map) {
            return mapToString((Map<?, ?>) value, processed);
        } else if (value instanceof List) {
            return listToString((List<?>) value, processed);
        } else if (value instanceof Set) {
            return setToString((Set<?>) value, processed);
        } else if (value.getClass().isArray()) {
            return arrayToString(value, processed);
        } else {
            return String.valueOf(value);
        }
    }

    private static String listToString(List<?> list, Map<Object, Object> visited) {

        String elements = list.stream()
            .map(item -> elementToString(item, visited))
            .collect(Collectors.joining(", "));

        return "[" + elements + "]";
    }

    private static String setToString(Set<?> set, Map<Object, Object> visited) {

        String elements = set.stream()
            .map(item -> elementToString(item, visited))
            .collect(Collectors.joining(", "));

        return "[" + elements + "]";
    }

    private static String arrayToString(Object array, Map<Object, Object> visited) {

        String elements = IntStream.range(0, Array.getLength(array))
            .mapToObj(i -> elementToString(Array.get(array, i), visited))
            .collect(Collectors.joining(", "));

        return "[" + elements + "]";
    }

    private static String elementToString(Object element, Map<Object, Object> visited) {
        if (element == null) {
            return "null";
        } else if (element.getClass().isAnnotationPresent(Masked.class)) {
            return processRecursively(element, visited);
        } else {
            return String.valueOf(element);
        }
    }

    private static String mapToString(Map<?, ?> map, Map<Object, Object> visited) {

        String entries = map.entrySet().stream()
            .map(entry -> {
                StringBuilder sb = new StringBuilder();
                sb.append(entry.getKey()).append("=");
                Object value = entry.getValue();

                if (value != null && value.getClass().isAnnotationPresent(Masked.class)) {
                    sb.append(processRecursively(value, visited));
                } else {
                    sb.append(value);
                }

                return sb.toString();
            })
            .collect(Collectors.joining(", "));

        return "{" + entries + "}";
    }
}