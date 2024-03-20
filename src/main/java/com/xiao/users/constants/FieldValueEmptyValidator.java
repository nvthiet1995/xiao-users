package com.xiao.users.constants;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class FieldValueEmptyValidator implements ConstraintValidator<FieldValueEmpty, Object>{
    private List<String> fieldList;
    @Override
    public void initialize(FieldValueEmpty constraintAnnotation) {
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
