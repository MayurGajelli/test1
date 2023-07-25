package com.learning.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learning.demo.entity.TransactionEntity;
import com.learning.demo.exception.CustomException;
import com.learning.demo.service.TransactionService;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin
public class TransactionController {

	@Autowired
	TransactionService transactionService;

	private static final Logger Logger = LoggerFactory.getLogger(TransactionController.class);

	@PostMapping(value = "/transaction")
	public ResponseEntity<Object> createTransaction(@RequestBody TransactionEntity entity) throws CustomException {
		TransactionEntity response = null;
		try {
			Logger.info("Insert Request Started...");
			response = transactionService.insertRequest(entity);
			Logger.info("Insert Request Completed...");
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getErrorMessage(), HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/transaction/update")
	public ResponseEntity<Object> updateTransaction(@RequestBody TransactionEntity entity) throws CustomException {
		TransactionEntity response = null;
		try {
			Logger.info("Update Request Started...");
			response = transactionService.updateRequest(entity);
			Logger.info("Update Request Completed...");
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getErrorMessage(), HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/transaction/cancel")
	public ResponseEntity<Object> cancelTransaction(@RequestBody TransactionEntity entity) throws CustomException {
		TransactionEntity response = null;
		try {
			Logger.info("Cancel Request Started...");
			response = transactionService.cancelRequest(entity);
			Logger.info("Cancel Request Completed...");
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getErrorMessage(), HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
