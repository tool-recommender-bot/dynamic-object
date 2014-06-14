package com.github.rschmitt.dynamicobject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

class Reflection {
    static <T extends DynamicObject<T>> Collection<Method> requiredFields(Class<T> type) {
        Collection<Method> fields = fieldGetters(type);
        return fields.stream().filter(Reflection::isRequired).collect(Collectors.toSet());
    }

    static <T extends DynamicObject<T>> Collection<Method> fieldGetters(Class<T> type) {
        Collection<Method> ret = new LinkedHashSet<>();
        for (Method method : type.getDeclaredMethods())
            if (method.getParameterCount() == 0 && !method.isDefault() && !isMetadataGetter(method))
                ret.add(method);
        return ret;
    }

    static boolean isMetadataGetter(Method getter) {
        if (getter.getParameterCount() != 0)
            return false;
        return hasAnnotation(getter, Meta.class);
    }

    static boolean isRequired(Method getter) {
        return hasAnnotation(getter, Required.class);
    }

    private static boolean hasAnnotation(Method method, Class ann) {
        List<Annotation> annotations = Arrays.asList(method.getAnnotations());
        for (Annotation annotation : annotations)
            if (annotation.annotationType().equals(ann))
                return true;
        return false;
    }

    static boolean isMetadataBuilder(Method method) {
        if (method.getParameterCount() != 1)
            return false;
        for (Annotation[] annotations : method.getParameterAnnotations())
            for (Annotation annotation : annotations)
                if (annotation.annotationType().equals(Meta.class))
                    return true;
        return false;
    }
}