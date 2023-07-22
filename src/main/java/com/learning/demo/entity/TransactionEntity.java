package com.learning.demo.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TransactionEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "TRANSACTION_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long transactionID;

	@Column(name = "TRADE_ID")
	private Long tradeID;

	@Column(name = "VERSION")
	private Integer version;

	@Column(name = "SECURITY_CODE")
	private String securityCode;

	@Column(name = "QUANTITY")
	private Long quantity;

	@Column(name = "ACTION")
	private String action;

	@Column(name = "TRANSACTION_TYPE")
	private String transactionType;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(Long transactionID) {
		this.transactionID = transactionID;
	}

	public Long getTradeID() {
		return tradeID;
	}

	public void setTradeID(Long tradeID) {
		this.tradeID = tradeID;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	@Override
	public String toString() {
		return "TransactionEntity [transactionID=" + transactionID + ", tradeID=" + tradeID + ", version=" + version
				+ ", securityCode=" + securityCode + ", quantity=" + quantity + ", action=" + action
				+ ", transactionType=" + transactionType + "]";
	}

}
