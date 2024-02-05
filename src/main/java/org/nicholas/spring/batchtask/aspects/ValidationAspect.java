package org.nicholas.spring.batchtask.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.nicholas.spring.batchtask.validation.SimpleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ValidationAspect {
    @Pointcut("execution(public * process(*))")
    public void processorPointcut(){}

    @Autowired
    private SimpleValidator validator;

    @Around("processorPointcut()")
    public Object aroundProcessorAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object arg = joinPoint.getArgs()[0];

        Object processed = null;
        if (validator.validate(arg, arg.getClass())) {
            processed = joinPoint.proceed();
        }
        return processed;
    }
}
