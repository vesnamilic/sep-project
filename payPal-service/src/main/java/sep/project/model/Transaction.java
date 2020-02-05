package sep.project.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import sep.project.dto.PaymentDTO;

@Entity
public class Transaction {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	private Client client;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	@Column
	@Enumerated(EnumType.STRING)
	private TransactionStatus status;

	@Column
	private Double paymentAmount;
	
	@Column
	private String paymentCurrency;
	
	@Column(name = "successUrl")
	private String successUrl;
	
	@Column(name = "errorUrl")
	private String errorUrl;
	
	@Column(name = "failedUrl")
	private String failedUrl;
	
	@Column
	private String paymentId;

	public Transaction() {

	}

	public Transaction(PaymentDTO payment, Client client) {
		this.client = client;
		this.date = new Date();
		this.status = TransactionStatus.INITIATED;
		this.paymentAmount = payment.getPaymentAmount();
		this.paymentCurrency = payment.getPaymentCurrency();
		this.successUrl = payment.getSuccessUrl();
		this.failedUrl = payment.getFailedUrl();
		this.errorUrl = payment.getErrorUrl();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public Double getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(Double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getPaymentCurrency() {
		return paymentCurrency;
	}

	public void setPaymentCurrency(String paymentCurrency) {
		this.paymentCurrency = paymentCurrency;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}

	public String getErrorUrl() {
		return errorUrl;
	}

	public void setErrorUrl(String errorUrl) {
		this.errorUrl = errorUrl;
	}

	public String getFailedUrl() {
		return failedUrl;
	}

	public void setFailedUrl(String failedUrl) {
		this.failedUrl = failedUrl;
	}
	
}
