package com.learning.demo.service;

import com.learning.demo.entity.TransactionEntity;
import com.learning.demo.exception.CustomException;

public interface TransactionService {

	public TransactionEntity insertRequest(TransactionEntity entity) throws CustomException;

	public TransactionEntity updateRequest(TransactionEntity entity) throws CustomException;

	public TransactionEntity cancelRequest(TransactionEntity entity) throws CustomException;

}
