package sep.project.DTOs;

import javax.validation.constraints.NotNull;

public class BankResponseDTO {

	@NotNull
	String paymentID;

	@NotNull
	String paymentURL;

	@NotNull
	String transactionId;

	public BankResponseDTO() {

	}

	public String getPaymentID() {
		return paymentID;
	}

	public void setPaymentID(String paymentID) {
		this.paymentID = paymentID;
	}

	public String getPaymentURL() {
		return paymentURL;
	}

	public void setPaymentURL(String paymentURL) {
		this.paymentURL = paymentURL;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

}
