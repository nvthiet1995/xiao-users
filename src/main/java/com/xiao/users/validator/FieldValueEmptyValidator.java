package com.xiao.users.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;


import java.util.Arrays;
import java.util.List;

public class FieldValueEmptyValidator implements ConstraintValidator<FieldValueEmpty, Object> {
    private List<String> fieldList;
    @Override
    public void initialize(FieldValueEmpty constraintAnnotation) {
        System.out.println("enter here");
        System.out.println(Arrays.toString(constraintAnnotation.fields()));
        fieldList = List.of(constraintAnnotation.fields());
    }

    @Override
    public boolean isValid(Object value,
                           ConstraintValidatorContext context) {
        for (String fieldName : fieldList){
            Object valueField = new BeanWrapperImpl(value).getPropertyValue(fieldName);
            if(valueField != null && !valueField.toString().isEmpty()){
                return true;
            }
        }
        return false;
    }

}
