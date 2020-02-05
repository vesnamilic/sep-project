package sep.project.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import sep.project.model.Subscription;
import sep.project.model.SubscriptionFrequency;
import sep.project.model.SubscriptionPlan;
import sep.project.model.SubscriptionType;

public class SubscriptionDTO {
		
	@NotNull
	@Email
	private String email;

	@NotNull
	@Positive
	private Double paymentAmount;

	@NotNull
	private String paymentCurrency;
	
	@NotNull
	private SubscriptionFrequency frequency;
	
	@NotNull
	private SubscriptionType type;
	
	@NotNull
	@Positive
	private Integer cyclesNumber;
	
	@NotNull
	private String successUrl;
	
	@NotNull
	private String errorUrl;
	
	@NotNull
	private String failedUrl;
	
	public SubscriptionDTO() {
		
	}
	
	public SubscriptionDTO(SubscriptionPlan subscriptionPlan, Subscription subscription) {
		this.email = subscription.getSeller().getEmail();
		this.paymentAmount = subscription.getPaymentAmount();
		this.paymentCurrency = subscription.getPaymentCurrency();
		this.frequency = subscriptionPlan.getFrequency();
		this.type = subscriptionPlan.getType();
		this.cyclesNumber = subscriptionPlan.getCyclesNumber();
		this.successUrl = subscription.getSuccessUrl();
		this.errorUrl = subscription.getErrorUrl();
		this.failedUrl = subscription.getFailedUrl();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
