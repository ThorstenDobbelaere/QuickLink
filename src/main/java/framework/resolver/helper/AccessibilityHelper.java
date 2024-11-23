package framework.resolver.helper;

import framework.exceptions.AccessException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AccessibilityHelper {
    public static Method trySetMethodAccessible(Method method) {
        try {
            method.setAccessible(true);
            return method;
        } catch (Exception e) {
            throw AccessException.ofMethod(method, e);
        }
    }

    public static Constructor<?> trySetConstructorAccessible(Constructor<?> constructor) {
        try {
            constructor.setAccessible(true);
            return constructor;
        } catch (Exception e) {
            throw AccessException.ofConstructor(constructor, e);
        }
    }
}
