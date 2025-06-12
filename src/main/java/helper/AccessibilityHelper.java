package helper;

import component_scan.exceptions.AccessException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AccessibilityHelper {
    private AccessibilityHelper() {}

    public static Method setMethodAccessible(Method method) {
        try {
            method.setAccessible(true);
            return method;
        } catch (Exception e) {
            throw AccessException.timedMethodPrivate(method, e);
        }
    }

    public static <T> Constructor<T> setConstructorAccessible(Constructor<T> constructor) {
        try {
            constructor.setAccessible(true);
            return constructor;
        } catch (Exception e) {
            throw AccessException.ofConstructor(constructor, e);
        }
    }
}
