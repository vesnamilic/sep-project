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
	private Long id;

	@Column(nullable = false, length = 256)
	private String paymentId;

	@OneToOne
	private Transaction transaction;

	public PaymentInfo() {

	}

	public PaymentInfo(String url, Transaction t) {
		this.paymentId = url;
		this.transaction = t;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentURL) {
		this.paymentId = paymentURL;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
