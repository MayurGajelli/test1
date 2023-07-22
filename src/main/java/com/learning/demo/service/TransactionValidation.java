package com.learning.demo.service;

import com.learning.demo.entity.TransactionEntity;
import com.learning.demo.exception.CustomException;

public interface TransactionValidation {

	public String validatePayload(TransactionEntity entity) throws CustomException;
}
