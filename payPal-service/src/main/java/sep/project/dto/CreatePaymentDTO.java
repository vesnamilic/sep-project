package sep.project.dto;

public class CreatePaymentDTO {
	
	private String email;
	
	private Double paymentAmount;
	
	private String paymentCurrency;
		
	public CreatePaymentDTO(){
		
	}
	
	public CreatePaymentDTO(String email, Double paymentAmount, String paymentCurrency){
		this.email = email;
		this.paymentAmount = paymentAmount;
		this.paymentCurrency = paymentCurrency;
	}
	
	public CreatePaymentDTO(String email, Double paymentAmount){
		this.email = email;
		this.paymentAmount = paymentAmount;
		this.paymentCurrency = "USD";
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
	
}
