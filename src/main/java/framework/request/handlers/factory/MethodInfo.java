package framework.request.handlers.factory;

import framework.context.QuickLinkContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public record MethodInfo(QuickLinkContext context, Annotation annotation, String controllerMapping, Method callback, Object controller){}
