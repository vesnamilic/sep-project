package sep.project.DTOs;

public class KPResponseDTO {

	String paymentURL;
	
	String paymentID;
	
	public KPResponseDTO() {
		
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
