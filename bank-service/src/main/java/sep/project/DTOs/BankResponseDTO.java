package sep.project.DTOs;

public class BankResponseDTO {

	String paymentURL;
	
	String paymentID;
	
	public BankResponseDTO() {
		
	}

	public String getPaymentURL() {
		return paymentURL;
	}

	public void setPaymentURL(String paymentURL) {
		this.paymentURL = paymentURL;
	}

	public String getPaymentID() {
		return paymentID;
	}

	public void setPaymentID(String paymentID) {
		this.paymentID = paymentID;
	}
	
}
