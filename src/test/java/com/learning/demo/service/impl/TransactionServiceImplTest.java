package com.learning.demo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.learning.demo.entity.Position;
import com.learning.demo.entity.TransactionEntity;
import com.learning.demo.exception.CustomException;
import com.learning.demo.repo.PositionRepo;
import com.learning.demo.repo.TransactionRepo;
import com.learning.demo.service.TransactionValidation;
import com.learning.demo.utils.Constants;

@RunWith(SpringRunner.class)
public class TransactionServiceImplTest {

	@TestConfiguration
	static class TransactionServiceImplTestConfig {
		@Bean
		public TransactionServiceImpl serviceImpl() {
			return new TransactionServiceImpl();
		}
	}

	@Autowired
	TransactionServiceImpl transactionServiceImpl;

	@MockBean
	TransactionRepo transactionRepo;

	@MockBean
	PositionRepo positionRepo;

	@MockBean
	TransactionValidation transactionValidation;

	private TransactionEntity entity = new TransactionEntity();
	private List<TransactionEntity> list = new ArrayList<>();
	private TransactionEntity entity1 = new TransactionEntity();
	private TransactionEntity entity2 = new TransactionEntity();

	private Position position = new Position();

	@Before
	public void setUp() throws Exception {
		entity.setTradeID(1L);
		entity.setSecurityCode("REL");
		entity.setQuantity(40L);
		entity.setTransactionType("Buy");
		entity.setVersion(1);
		entity.setAction(Constants.INSERT);

		entity1.setTradeID(1L);
		entity1.setSecurityCode("REL");
		entity1.setQuantity(40L);
		entity1.setTransactionType("Sell");
		entity1.setVersion(2);
		entity1.setAction(Constants.UPDATE);

		entity2.setTradeID(1L);
		entity2.setSecurityCode("REL");
		entity2.setQuantity(40L);
		entity2.setTransactionType("Buy");
		entity2.setVersion(3);
		entity2.setAction(Constants.CANCEL);

		position.setSecurityCode("REL");
		position.setPosition(10L);

	}

	@Test
	public void testInsertRequest() throws CustomException {
		when(transactionRepo.findByTradeID(1L)).thenReturn(list);
		Optional<Position> optional = Optional.of(position);
		when(positionRepo.findById(Mockito.anyString())).thenReturn(optional);
		when(transactionRepo.save(entity)).thenReturn(entity);
		when(positionRepo.save(position)).thenReturn(position);
		TransactionEntity result = transactionServiceImpl.insertRequest(entity);
		assertThat(result).isNotNull();
	}

	@Test
	public void testInsertRequestValidationError() throws CustomException {
		entity.setTransactionType("ABC");
		when(transactionRepo.findByTradeID(1L)).thenReturn(list);
		Optional<Position> optional = Optional.of(position);
		when(positionRepo.findById(Mockito.anyString())).thenReturn(optional);
		when(transactionRepo.save(entity)).thenReturn(entity);
		when(positionRepo.save(position)).thenReturn(position);
		TransactionEntity result = transactionServiceImpl.insertRequest(entity);
		assertThat(result).isNotNull();
	}

	@Test(expected = CustomException.class)
	public void testInsertRequestException() throws CustomException {
		when(transactionRepo.findByTradeID(1L)).thenReturn(list);
		Optional<Position> optional = Optional.of(position);
		when(positionRepo.findById(Mockito.anyString())).thenReturn(optional);
		when(transactionRepo.save(entity)).thenThrow(RuntimeException.class);
		when(positionRepo.save(position)).thenReturn(position);
		transactionServiceImpl.insertRequest(entity);
	}

	@Test
	public void testInsertRequestWithoutPosition() throws CustomException {
		when(transactionRepo.findByTradeID(1L)).thenReturn(list);
		when(transactionRepo.save(entity)).thenReturn(entity);
		when(positionRepo.save(position)).thenReturn(position);
		TransactionEntity result = transactionServiceImpl.insertRequest(entity);
		assertThat(result).isNotNull();
	}

	@Test
	public void testInsertRequesWithUpdate() throws CustomException {
		list.add(entity1);
		when(transactionRepo.findByTradeID(1L)).thenReturn(list);
		when(transactionRepo.save(entity)).thenReturn(entity);
		when(positionRepo.save(position)).thenReturn(position);
		TransactionEntity result = transactionServiceImpl.insertRequest(entity);
		assertThat(result).isNotNull();
	}

	@Test
	public void testInsertRequesDuplicateRequest() throws CustomException {
		list.add(entity);
		when(transactionRepo.findByTradeID(1L)).thenReturn(list);
		when(transactionRepo.save(entity)).thenReturn(entity);
		when(positionRepo.save(position)).thenReturn(position);
		TransactionEntity result = transactionServiceImpl.insertRequest(entity);
		assertThat(result).isNull();
	}

	@Test(expected = CustomException.class)
	public void testUpdateRequestCustomException() throws CustomException {
		list.add(entity);
		when(transactionRepo.findByTradeID(1L)).thenReturn(list);
		when(transactionRepo.save(entity1)).thenThrow(RuntimeException.class);
		when(positionRepo.save(position)).thenReturn(position);
		transactionServiceImpl.updateRequest(entity1);
	}

	@Test
	public void testUpdateRequest() throws CustomException {
		list.add(entity);
		when(transactionRepo.findByTradeID(1L)).thenReturn(list);
		when(transactionRepo.save(entity1)).thenReturn(entity1);
		when(positionRepo.save(position)).thenReturn(position);
		TransactionEntity result = transactionServiceImpl.updateRequest(entity1);
		assertThat(result).isNotNull();
	}

	@Test
	public void testUpdateRequestWithoutInsert() throws CustomException {
		when(transactionRepo.findByTradeID(1L)).thenReturn(list);
		when(transactionRepo.save(entity1)).thenReturn(entity1);
		when(positionRepo.save(position)).thenReturn(position);
		TransactionEntity result = transactionServiceImpl.updateRequest(entity1);
		assertThat(result).isNotNull();
	}

	@Test
	public void testUpdateRequestDuplicateRequest() throws CustomException {
		list.add(entity1);
		when(transactionRepo.findByTradeID(1L)).thenReturn(list);
		when(transactionRepo.save(entity1)).thenReturn(entity1);
		when(positionRepo.save(position)).thenReturn(position);
		TransactionEntity result = transactionServiceImpl.updateRequest(entity1);
		assertThat(result).isNull();
	}

	@Test
	public void testCancelRequest() throws CustomException {
		list.add(entity);
		list.add(entity1);
		when(transactionRepo.findByTradeID(1L)).thenReturn(list);
		when(transactionRepo.save(entity2)).thenReturn(entity2);
		when(positionRepo.save(position)).thenReturn(position);
		TransactionEntity result = transactionServiceImpl.cancelRequest(entity2);
		assertThat(result).isNotNull();
	}

	@Test
	public void testCancelRequestWithInsert() throws CustomException {
		list.add(entity);
		when(transactionRepo.findByTradeID(1L)).thenReturn(list);
		when(transactionRepo.save(entity2)).thenReturn(entity2);
		when(positionRepo.save(position)).thenReturn(position);
		TransactionEntity result = transactionServiceImpl.cancelRequest(entity2);
		assertThat(result).isNotNull();
	}

	@Test
	public void testCancelRequestDuplicateRequest() throws CustomException {
		list.add(entity2);
		when(transactionRepo.findByTradeID(1L)).thenReturn(list);
		when(transactionRepo.save(entity2)).thenReturn(entity2);
		when(positionRepo.save(position)).thenReturn(position);
		TransactionEntity result = transactionServiceImpl.cancelRequest(entity2);
		assertThat(result).isNull();
	}

	@Test
	public void testSaveTransaction() throws CustomException {
		when(transactionRepo.save(entity)).thenReturn(entity);
		TransactionEntity result = transactionServiceImpl.saveTransaction(entity);
		assertThat(result).isNotNull();
	}

	@Test
	public void testSavePosition() throws CustomException {
		when(positionRepo.save(position)).thenReturn(position);
		Position result = transactionServiceImpl.savePosition(position);
		assertThat(result).isNotNull();
	}

}
