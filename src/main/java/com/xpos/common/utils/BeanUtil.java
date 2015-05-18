package com.xpos.common.utils;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class BeanUtil {
	
	/**
	 * Bean validation
	 * @param bean
	 * @return null if every field is ok, otherwise return the first failed validation message
	 */
	public static String validate(Object bean){
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(bean);
		if(constraintViolations.size() > 0){
			return constraintViolations.iterator().next().getMessage();
		}else{
			return null;
		}
	}
	
}
