package com.learning.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.learning.demo.entity.TransactionEntity;

@Repository
@EnableJpaRepositories
public interface TransactionRepo extends CrudRepository<TransactionEntity, Long> {

	List<TransactionEntity> findByTradeID(Long tradeID);

	TransactionEntity findTopByTradeIDOrderByVersionDesc(Long tradeID);

}
