package com.learning.demo.service.impl;

import org.springframework.stereotype.Service;

import com.learning.demo.entity.TransactionEntity;
import com.learning.demo.exception.CustomException;
import com.learning.demo.service.TransactionValidation;
import com.learning.demo.utils.Constants;

@Service
public class TransactionValidationImpl implements TransactionValidation {

	@Override
	public String validatePayload(TransactionEntity entity) throws CustomException {
		if (!Constants.BUY.equals(entity.getTransactionType()) && !Constants.SELL.equals(entity.getTransactionType())) {
			String errorMessage = "TransactionType value can either be Buy or Sell";
			throw new CustomException(errorMessage, null);
		}
		return "Success";
	}

}
