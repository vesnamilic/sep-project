package sep.project.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class PaymentDTO {
	
	@NotNull
	@Email
	private String email;
	
	@NotNull
	@Positive
	private Double paymentAmount;
	
	@NotNull
	private String paymentCurrency;
	
	@NotNull
	private String successUrl;
	
	@NotNull
	private String errorUrl;
	
	@NotNull
	private String failedUrl;
			
	public PaymentDTO(){
		
	}
	
	public PaymentDTO(String email, Double paymentAmount, String paymentCurrency, @NotNull String successUrl, String errorUrl, @NotNull String failedUrl) {
		this.email = email;
		this.paymentAmount = paymentAmount;
		this.paymentCurrency = paymentCurrency;
		this.successUrl = successUrl;
		this.errorUrl = errorUrl;
		this.failedUrl = failedUrl;
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
