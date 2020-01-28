package sep.project.dto;

import sep.project.model.BillingPlanFrequency;
import sep.project.model.BillingType;


public class BillingPlanDTO {
	
	private Long id;
	
	private String billingPlanId;
	
	private Double paymentAmount;
	
	private String paymentCurrency;
	
	private BillingPlanFrequency frequency;
	
	private BillingType type;
	
	public BillingPlanDTO(){
		
	}

	public BillingPlanDTO(Double paymentAmount, String paymentCurrency, BillingPlanFrequency frequency, BillingType type, String billingPlanId) {
		this.paymentAmount = paymentAmount;
		this.paymentCurrency = paymentCurrency;
		this.frequency = frequency;
		this.type = type;
		this.billingPlanId = billingPlanId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBillingPlanId() {
		return billingPlanId;
	}

	public void setBillingPlanId(String billingPlanId) {
		this.billingPlanId = billingPlanId;
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

	public BillingPlanFrequency getFrequency() {
		return frequency;
	}

	public void setFrequency(BillingPlanFrequency frequency) {
		this.frequency = frequency;
	}

	public BillingType getType() {
		return type;
	}

	public void setType(BillingType type) {
		this.type = type;
	}
	
}
