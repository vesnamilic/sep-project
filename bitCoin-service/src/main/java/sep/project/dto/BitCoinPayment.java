package sep.project.dto;

public class BitCoinPayment {

	private String email;

	private double paymentAmount;

	private String paymentCurrency;

	private String title;

	private String description;

	public BitCoinPayment() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BitCoinPayment(String email, double paymentAmount, String paymentCurrency, String title,
			String description) {
		super();
		this.email = email;
		this.paymentAmount = paymentAmount;
		this.paymentCurrency = paymentCurrency;
		this.title = title;
		this.description = description;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}