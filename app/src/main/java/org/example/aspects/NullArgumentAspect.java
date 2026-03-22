package org.example.aspects;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.exceptions.IncorrectArgumentGivenException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NullArgumentAspect {
    
    public Object checkNullArgument(ProceedingJoinPoint jp) throws Throwable {
        Signature signature = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;
        Method method = methodSignature.getMethod();
        String methodName = method.getName();
        Parameter[] parameters = method.getParameters();
        Object[] args = jp.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                throw new IncorrectArgumentGivenException(methodName, parameters[i].isNamePresent() ? parameters[i].getName() : "arg " + i, "не может быть null");
            }
        }

        return jp.proceed();
    }
}
