package sep.project.dto;

import sep.project.model.BillingPlanFrequency;

public class BillingPlanDTO {
	
	private String email;
	
	private Double paymentAmount;
	
	private String paymentCurrency;
	
	private BillingPlanFrequency frequency;
			
	public BillingPlanDTO(){
		
	}
	
	public BillingPlanDTO(String email, Double paymentAmount, String paymentCurrency, BillingPlanFrequency frequency){
		this.email = email;
		this.paymentAmount = paymentAmount;
		this.paymentCurrency = paymentCurrency;
		this.frequency = frequency;
	}
	
	public BillingPlanDTO(String email, Double paymentAmount){
		this.email = email;
		this.paymentAmount = paymentAmount;
		this.paymentCurrency = "USD";
		this.frequency = BillingPlanFrequency.MONTH;
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
	
}
