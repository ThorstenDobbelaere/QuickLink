package framework.setup;

import framework.annotations.interception.Timed;
import framework.exceptions.internal.InternalException;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class TimedInterceptor {
    public static <T> T instantiateAnnotationInterceptedComponent(Class<?> type, Constructor<T> constructor, Object[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(type);
        proxyFactory.setFilter(method -> Arrays.stream(method.getDeclaredAnnotations())
                .anyMatch(annotation -> annotation.annotationType().equals(Timed.class))
        );

        T instance = constructor.newInstance(args);
        MethodHandler handler = new AnnotatedHandler(instance);

        try{
            Object timedInstance = proxyFactory.create(constructor.getParameterTypes(), args, handler);
            System.out.println("Creating proxy for " + type.getName());
            return (T)timedInstance;
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new InternalException(e.getMessage());
        }
    }

    private record AnnotatedHandler(Object original) implements MethodHandler {
        private static final Logger TIMED_LOGGER = LoggerFactory.getLogger(Timed.class);

        @Override
        public Object invoke(Object o, Method method, Method method1, Object[] objects) throws Throwable {
            if (!method.isAnnotationPresent(Timed.class)) {
                return method.invoke(original);
            }
            TIMED_LOGGER.info("Timed Method {} invoked", method.getName());
            return method.invoke(original, objects);
        }
    }
}
