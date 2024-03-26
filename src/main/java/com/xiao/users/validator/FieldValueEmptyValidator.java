package com.xiao.users.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bouncycastle.util.Strings;
import org.springframework.beans.BeanWrapperImpl;


import java.util.List;
import java.util.Objects;

public class FieldValueEmptyValidator implements ConstraintValidator<CheckAllValueEmpty, Object> {
    private List<String> fieldList;
    @Override
    public void initialize(CheckAllValueEmpty constraintAnnotation) {
        fieldList = List.of(constraintAnnotation.fields());
    }

    @Override
    public boolean isValid(Object value,
                           ConstraintValidatorContext context) {
        for (String fieldName : fieldList){
            Object valueField = new BeanWrapperImpl(value).getPropertyValue(fieldName);
            if(!Objects.isNull(valueField) && !String.valueOf(valueField).isEmpty()){
                return true;
            }
        }
        return false;
    }

}
