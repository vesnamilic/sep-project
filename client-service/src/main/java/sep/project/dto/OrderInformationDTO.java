package sep.project.dto;

public class OrderInformationDTO {

	private String email;

	private double paymentAmount;

	private String paymentCurrency;

	public OrderInformationDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public double getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getPaymentCurrency() {
		return paymentCurrency;
	}

	public void setPaymentCurrency(String paymentCurrency) {
		this.paymentCurrency = paymentCurrency;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


}
