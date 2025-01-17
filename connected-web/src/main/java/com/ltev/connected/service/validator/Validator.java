package com.ltev.connected.service.validator;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.ToString;
import org.springframework.validation.FieldError;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Validates value correctness of annotated fields
 */
@ToString
public class Validator<T> {

    private static final Set<Class<? extends Annotation>> SUPPORTED_ANNOTATIONS_SET =
            Set.of(NotNull.class, NotBlank.class, Size.class);

    private static final Map<Class<? extends Annotation>, Method> ANNOTATION_METHOD_MAP;

    static {
        // method annotation map
        Map<Method, Class<? extends Annotation>> methodAnnotationMap = new HashMap<>();
        Arrays.stream(Validator.class.getDeclaredMethods())
                .forEach(m -> {
                    var annotations = m.getDeclaredAnnotations();
                    if (annotations.length == 1) {
                        methodAnnotationMap.put(m, annotations[0].annotationType());
                    }
                });

        // annotation method map
        ANNOTATION_METHOD_MAP = new HashMap<>();
        methodAnnotationMap.entrySet()
                        .forEach(es -> ANNOTATION_METHOD_MAP.put(es.getValue(), es.getKey()));
    }

    private T object;
    private Map<String, List<FieldError>> fieldErrorsMap = new HashMap<>();

    public Validator(T object) {
        this.object = object;
    }

    public void validate() {
        Field[] objectFields = object.getClass().getDeclaredFields();
        for (Field field : objectFields) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            Arrays.stream(annotations)
                    .filter(annotation -> SUPPORTED_ANNOTATIONS_SET.contains(annotation.annotationType()))
                    .forEach(annotation -> {
                        try {
                            Method method = ANNOTATION_METHOD_MAP.get(annotation.annotationType());
                            method.invoke(this, field, annotation);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    public boolean hasErrors() {
        return numErrors() > 0;
    }

    public int numErrors() {
        return fieldErrorsMap.values().stream().mapToInt(List::size).sum();
    }

    public boolean hasErrors(String field) {
        return numErrors(field) > 0;
    }

    public int numErrors(String field) {
        return getFieldErrors(field).size();
    }

    public List<FieldError> getFieldErrors(String field) {
        return fieldErrorsMap.getOrDefault(field, Collections.EMPTY_LIST);
    }

    // == PRIVATE HELPER METHODS ==

    private void validate(Field field) {
        try {
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                Method method = ANNOTATION_METHOD_MAP.get(annotation.annotationType());
                method.invoke(this, field, annotation);
            }

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void addFieldError(Field field, Object fieldValue, String message) throws IllegalAccessException {
        FieldError fieldError = new FieldError(object.getClass().toString(), field.getName(), fieldValue,
                false, null, null, message);

        fieldErrorsMap.putIfAbsent(field.getName(), new ArrayList<>(1));
        fieldErrorsMap.get(field.getName()).add(fieldError);
    }

    @Size
    private void validateSize(Field field, Annotation annotation) throws IllegalAccessException {
        Size fieldAnnotation = (Size) annotation;

        field.setAccessible(true);
        Object fieldValue = field.get(object);
        field.setAccessible(false);

        if (fieldValue == null) {
            addFieldError(field, fieldValue, fieldAnnotation.message());
        } else {
            int valueSize = ((String) fieldValue).length();
            if (valueSize < fieldAnnotation.min() || valueSize > fieldAnnotation.max()) {
                addFieldError(field, fieldValue, fieldAnnotation.message());
            }
        }

//        Errors errors = new SimpleErrors(object);
//        errors.rejectValue(field.getName(), "errorCode1", null, fieldAnnotation.message());
//        errors.rejectValue(field.getName(), "errorCode2", null, fieldAnnotation.message());
//        System.out.println(errors);
//
//        BindingResult br = new MapBindingResult(new HashMap<>(), "object name");
//        br.addError(errors.getAllErrors().get(0));
//
//        System.out.println("----");
//        System.out.println(errors.getFieldError(field.getName()));
    }

    @NotNull
    private void validateNotNull(Field field, Annotation annotation) throws IllegalAccessException {
        NotNull fieldAnnotation = (NotNull) annotation;

        field.setAccessible(true);
        Object fieldValue = field.get(object);
        field.setAccessible(false);

        if (fieldValue == null) {
            addFieldError(field, fieldValue, fieldAnnotation.message());
        }
    }

    @NotBlank
    private void validateNotBlank(Field field, Annotation annotation) throws IllegalAccessException {
        NotBlank fieldAnnotation = (NotBlank) annotation;

        field.setAccessible(true);
        Object fieldValue = field.get(object);
        field.setAccessible(false);

        if (fieldValue == null || ((String) fieldValue).trim().isEmpty()) {
            addFieldError(field, fieldValue, fieldAnnotation.message());
        }
    }
}