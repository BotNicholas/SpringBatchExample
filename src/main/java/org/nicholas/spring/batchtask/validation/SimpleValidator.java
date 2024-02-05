package org.nicholas.spring.batchtask.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;

@Component
public class SimpleValidator {
    ValidatorFactory factory;
    Validator validator;

    public SimpleValidator(){
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public Boolean validate(Object object, Class<?> objClass){
        return validator.validate(objClass.cast(object)).isEmpty();
    }
}
