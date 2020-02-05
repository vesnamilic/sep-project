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

import sep.project.dto.SubscriptionDTO;

@Entity
public class Subscription {

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
	private SubscriptionStatus status;
	
	@Column
	private Double paymentAmount;

	@Column
	private String paymentCurrency;
	
	@Column
	@Enumerated(EnumType.STRING)
	private SubscriptionFrequency frequency;
	
	@Column
	@Enumerated(EnumType.STRING)
	private SubscriptionType type;
	
	@Column
	private Integer cyclesNumber;
	
	@Column
	private String successUrl;
	
	@Column
	private String errorUrl;
	
	@Column
	private String failedUrl;
	
	@Column
	private String billingPlanId;
	
	@Column
	private String billingAgreementId;
	
	public Subscription() {
		
	}
	
	public Subscription(SubscriptionDTO subscriptionDTO, Client client) {
		this.client = client;
		this.date = new Date();
		this.status = SubscriptionStatus.INITIATED;
		this.paymentAmount = subscriptionDTO.getPaymentAmount();
		this.paymentCurrency = subscriptionDTO.getPaymentCurrency();
		this.frequency = subscriptionDTO.getFrequency();
		this.type = subscriptionDTO.getType();
		this.cyclesNumber = subscriptionDTO.getCyclesNumber();
		this.successUrl = subscriptionDTO.getSuccessUrl();
		this.errorUrl = subscriptionDTO.getErrorUrl();
		this.failedUrl = subscriptionDTO.getFailedUrl();
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

	public SubscriptionFrequency getFrequency() {
		return frequency;
	}

	public void setFrequency(SubscriptionFrequency frequency) {
		this.frequency = frequency;
	}

	public SubscriptionType getType() {
		return type;
	}

	public void setType(SubscriptionType type) {
		this.type = type;
	}

	public Integer getCyclesNumber() {
		return cyclesNumber;
	}

	public void setCyclesNumber(Integer cyclesNumber) {
		this.cyclesNumber = cyclesNumber;
	}

	public SubscriptionStatus getStatus() {
		return status;
	}

	public void setStatus(SubscriptionStatus status) {
		this.status = status;
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

	public String getBillingPlanId() {
		return billingPlanId;
	}

	public void setBillingPlanId(String billingPlanId) {
		this.billingPlanId = billingPlanId;
	}

	public String getBillingAgreementId() {
		return billingAgreementId;
	}

	public void setBillingAgreementId(String billingAgreementId) {
		this.billingAgreementId = billingAgreementId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}
