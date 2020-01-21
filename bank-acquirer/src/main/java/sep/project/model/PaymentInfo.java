package sep.project.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class PaymentInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long paymentID;

	@Column(nullable = false, length = 256)
	private String paymentToken;

	@OneToOne
	private Transaction transaction;

	public PaymentInfo() {

	}

	public PaymentInfo(String url, Transaction t) {
		this.paymentToken = url;
		this.transaction = t;
	}

	public Long getPaymentID() {
		return paymentID;
	}

	public void setPaymentID(Long paymentID) {
		this.paymentID = paymentID;
	}

	public String getPaymentToken() {
		return paymentToken;
	}

	public void setPaymentToken(String paymentURL) {
		this.paymentToken = paymentURL;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

}
