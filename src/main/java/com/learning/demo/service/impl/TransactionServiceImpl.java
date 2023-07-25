package com.learning.demo.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.learning.demo.entity.Position;
import com.learning.demo.entity.TransactionEntity;
import com.learning.demo.exception.CustomException;
import com.learning.demo.repo.PositionRepo;
import com.learning.demo.repo.TransactionRepo;
import com.learning.demo.service.TransactionService;
import com.learning.demo.service.TransactionValidation;
import com.learning.demo.utils.Constants;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	TransactionRepo transactionRepo;

	@Autowired
	PositionRepo positionRepo;

	@Autowired
	TransactionValidation transactionValidation;

	private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

	@Override
	public TransactionEntity insertRequest(TransactionEntity entity) throws CustomException {
		TransactionEntity result = null;
		try {
			transactionValidation.validatePayload(entity);
			List<TransactionEntity> list = getTransactions(entity.getTradeID());
			TransactionEntity insertEntity = null;
			if (!CollectionUtils.isEmpty(list)) {
				insertEntity = getRequestEntityFromList(list, Constants.INSERT);
			}
			if (Objects.isNull(insertEntity)) {
				entity.setAction(Constants.INSERT);
				entity.setVersion(1);
				result = saveTransaction(entity);
				calculatePositionForInsertRequest(entity, list);
			} else {
				String errorMessage = "Duplicate Request - Insert Request already processed for tradeID: "+ entity.getTradeID();
				logger.error(errorMessage);
				throw new CustomException(errorMessage, null);
			}
		} catch (CustomException ex) {
			throw ex;
		} catch (Exception ex) {
			new CustomException(Constants.GENERIC_EXCEPTION_MSG, ex);
		}
		return result;
	}

	@Override
	public TransactionEntity updateRequest(TransactionEntity entity) throws CustomException {
		TransactionEntity result = null;
		try {
			transactionValidation.validatePayload(entity);
			List<TransactionEntity> list = getTransactions(entity.getTradeID());
			TransactionEntity updateEntity = getRequestEntityFromList(list, Constants.UPDATE);
			TransactionEntity cancelEntity = getRequestEntityFromList(list, Constants.CANCEL);
			if (Objects.nonNull(updateEntity)) {
				String errorMessage = "Duplicate Request - Update Request already processed for tradeID: "+ entity.getTradeID();
				logger.error(errorMessage);
				throw new CustomException(errorMessage, null);
			} else if (Objects.nonNull(cancelEntity)) {
				String errorMessage = "Invalid Update Request - Cancel Request already processed for tradeID: "+ entity.getTradeID();
				logger.error(errorMessage);
				throw new CustomException(errorMessage, null);
			} else {
				entity.setAction(Constants.UPDATE);
				TransactionEntity tran = transactionRepo.findTopByTradeIDOrderByVersionDesc(entity.getTradeID());
				logger.info("Max version: " + tran);
				if (Objects.nonNull(tran)) {
					entity.setVersion(tran.getVersion() + 1);
				} else {
					logger.info("Setting default version as 2");
					entity.setVersion(2);
				}
				result = saveTransaction(entity);
				calculatePositionForUpdateRequest(entity, list);
			}
		} catch (CustomException ex) {
			throw ex;
		} catch (Exception ex) {
			new CustomException(Constants.GENERIC_EXCEPTION_MSG, ex);
		}
		return result;
	}

	@Override
	public TransactionEntity cancelRequest(TransactionEntity entity) throws CustomException {
		TransactionEntity result = null;
		try {
			transactionValidation.validatePayload(entity);
			List<TransactionEntity> list = getTransactions(entity.getTradeID());
			TransactionEntity cancelEntity = getRequestEntityFromList(list, Constants.CANCEL);
			if (Objects.nonNull(cancelEntity)) {
				String errorMessage = "Duplicate Request - Cancel Request already processed for tradeID: "+ entity.getTradeID();
				logger.error(errorMessage);
				throw new CustomException(errorMessage, null);
			} else {
				TransactionEntity insertEntity = getRequestEntityFromList(list, Constants.INSERT);
				TransactionEntity updateEntity = getRequestEntityFromList(list, Constants.UPDATE);
				if (Objects.nonNull(updateEntity)) {
					savePosition(setPositionForReverseTransaction(updateEntity));
					result = formCancelRequest(entity);
				} else if (Objects.nonNull(insertEntity)) {
					savePosition(setPositionForReverseTransaction(insertEntity));
					result = formCancelRequest(entity);
				} else {
					String errorMessage = "Invalid Cancel Request - Insert/Update Request is not processed for tradeID: "+ entity.getTradeID();
					logger.error(errorMessage);
					throw new CustomException(errorMessage, null);
				}
			}
		} catch (CustomException ex) {
			throw ex;
		} catch (Exception ex) {
			new CustomException(Constants.GENERIC_EXCEPTION_MSG, ex);
		}
		return result;
	}

	private void calculatePositionForInsertRequest(TransactionEntity entity, List<TransactionEntity> list)
			throws CustomException {
		try {
			TransactionEntity updateEntity = null;
			if (!CollectionUtils.isEmpty(list)) {
				updateEntity = getRequestEntityFromList(list, Constants.UPDATE);
			}
			if (Objects.isNull(updateEntity)) {
				Long quantity = determineQuantity(entity);
				Position position = fetchPosition(entity);
				if (Objects.nonNull(position)) {
					position.setPosition(position.getPosition() + quantity);
				} else {
					position = new Position();
					position.setSecurityCode(entity.getSecurityCode());
					position.setPosition(quantity);
				}
				savePosition(position);
			} else {
				logger.info("Update Request already processed for tradeID: {}", entity.getTradeID());
			}
		} catch (CustomException ex) {
			throw ex;
		} catch (Exception ex) {
			new CustomException(Constants.GENERIC_EXCEPTION_MSG, ex);
		}
	}

	@Transactional
	private void calculatePositionForUpdateRequest(TransactionEntity entity, List<TransactionEntity> list)
			throws CustomException {
		try {
			TransactionEntity insertEntity = getRequestEntityFromList(list, Constants.INSERT);
			if (Objects.nonNull(insertEntity)) {
				savePosition(setPositionForReverseTransaction(insertEntity));
				savePosition(setPositionForNormalTransaction(entity));
			} else {
				savePosition(setPositionForNormalTransaction(entity));
			}
		} catch (CustomException ex) {
			throw ex;
		} catch (Exception ex) {
			new CustomException(Constants.GENERIC_EXCEPTION_MSG, ex);
		}
	}

	private TransactionEntity formCancelRequest(TransactionEntity entity) throws CustomException {
		try {
			entity.setAction(Constants.CANCEL);
			TransactionEntity tran = transactionRepo.findTopByTradeIDOrderByVersionDesc(entity.getTradeID());
			logger.info("Max version: {}", tran);
			if (null != tran) {
				entity.setVersion(tran.getVersion() + 1);
			} else {
				logger.info("Setting default version as 2");
				entity.setVersion(2);
			}
		} catch (Exception ex) {
			new CustomException(Constants.GENERIC_EXCEPTION_MSG, ex);
		}
		return saveTransaction(entity);
	}

	private Position fetchPosition(TransactionEntity entity) throws CustomException {
		Position position = null;
		try {
			Optional<Position> result = positionRepo.findById(entity.getSecurityCode());
			if (result.isPresent()) {
				position = result.get();
			}
		} catch (Exception ex) {
			new CustomException(Constants.GENERIC_EXCEPTION_MSG, ex);
		}
		return position;
	}

	private Long determineQuantity(TransactionEntity entity) throws CustomException {
		Long quantity = 0L;
		try {
			if (Constants.SELL.equalsIgnoreCase(entity.getTransactionType())) {
				quantity = Math.negateExact(Math.abs(entity.getQuantity()));
			} else if (Constants.BUY.equalsIgnoreCase(entity.getTransactionType())) {
				quantity = entity.getQuantity();
			}
		} catch (Exception ex) {
			new CustomException(Constants.GENERIC_EXCEPTION_MSG, ex);
		}
		return quantity;
	}

	@Transactional
	public TransactionEntity saveTransaction(TransactionEntity entity) throws CustomException {
		TransactionEntity result = null;
		try {
			result = transactionRepo.save(entity);
			logger.info("Saved transaction: {}", result);
		} catch (Exception ex) {
			String errorMesage = "Exception occured during saving Transaction";
			logger.error(errorMesage, ex.getMessage());
			throw new CustomException(errorMesage, ex);
		}
		return result;
	}

	@Transactional
	public Position savePosition(Position position) throws CustomException {
		Position result = null;
		try {
			result = positionRepo.save(position);
			logger.info("Saved Position: {}", result);
		} catch (Exception ex) {
			String errorMesage = "Exception occured during saving Position";
			logger.error(errorMesage, ex.getMessage());
			throw new CustomException(errorMesage, ex);
		}
		return result;
	}

	private List<TransactionEntity> getTransactions(Long tradeID) throws CustomException {
		List<TransactionEntity> result = null;
		try {
			result = transactionRepo.findByTradeID(tradeID);
		} catch (Exception ex) {
			new CustomException(Constants.GENERIC_EXCEPTION_MSG, ex);
		}
		return result;
	}

	private TransactionEntity getRequestEntityFromList(List<TransactionEntity> list, String requestType)
			throws CustomException {
		TransactionEntity entity = null;
		try {
			entity = list.stream().filter(t -> requestType.equalsIgnoreCase(t.getAction())).findFirst().orElse(null);
		} catch (Exception ex) {
			new CustomException(Constants.GENERIC_EXCEPTION_MSG, ex);
		}
		return entity;
	}

	private Position setPositionForNormalTransaction(TransactionEntity entity) throws CustomException {
		Position position = fetchPosition(entity);
		try {
			Long quantity = determineQuantity(entity);
			if (null != position) {
				position.setPosition(position.getPosition() + quantity);
			} else {
				position = new Position();
				position.setSecurityCode(entity.getSecurityCode());
				position.setPosition(quantity);
			}
		} catch (CustomException ex) {
			throw ex;
		} catch (Exception ex) {
			new CustomException(Constants.GENERIC_EXCEPTION_MSG, ex);
		}
		return position;
	}

	private Position setPositionForReverseTransaction(TransactionEntity entity) throws CustomException {
		Position position = null;
		try {
			position = fetchPosition(entity);
			Long quantity = determineQuantity(entity);
			if (null != position) {
				position.setPosition(position.getPosition() - quantity);
			} else {
				position = new Position();
				position.setSecurityCode(entity.getSecurityCode());
				position.setPosition(quantity);
			}
		} catch (CustomException ex) {
			throw ex;
		} catch (Exception ex) {
			new CustomException(Constants.GENERIC_EXCEPTION_MSG, ex);
		}
		return position;
	}
}
