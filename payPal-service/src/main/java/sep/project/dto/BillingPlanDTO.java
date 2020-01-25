package sep.project.dto;

import sep.project.model.BillingPlanFrequency;
import sep.project.model.BillingType;

public class BillingPlanDTO {
	
	private String email;
	
	private Double paymentAmount;
	
	private String paymentCurrency;
	
	private BillingPlanFrequency frequency;
	
	private BillingType type;
			
	public BillingPlanDTO(){
		
	}
	
	public BillingPlanDTO(String email, Double paymentAmount, String paymentCurrency, BillingPlanFrequency frequency, BillingType type){
		this.email = email;
		this.paymentAmount = paymentAmount;
		this.paymentCurrency = paymentCurrency;
		this.frequency = frequency;
		this.type = type;
	}
	
	public BillingPlanDTO(String email, Double paymentAmount){
		this.email = email;
		this.paymentAmount = paymentAmount;
		this.paymentCurrency = "USD";
		this.frequency = BillingPlanFrequency.MONTH;
		this.type = BillingType.INFINITE;
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
