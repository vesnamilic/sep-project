package sep.project.dto;

public class ConfirmPaymentDTO {
	
	private String email;
	private String paymentId;
	private String payerId;
	
	public ConfirmPaymentDTO(){
		
	}
	
	public ConfirmPaymentDTO(String email, String paymentId, String payerId){
		this.email = email;
		this.paymentId = paymentId;
		this.payerId = payerId;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getPayerId() {
		return payerId;
	}

	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	

}
