package sep.project.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import sep.project.enums.Status;

@Entity
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private CardOwner seller;

	@ManyToOne 
	private CardOwner buyer;

	@Column(length = 256)
	private String paymentId;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp; 

	@Column
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(length = 400)
	//@Convert(converter = CryptoConverter.class)
	private String buyerPan;

	@Column(length = 400)
	//@Convert(converter = CryptoConverter.class)
	private String sellerPan;

	@Column
	private Float amount;

	@Column	
	private Long merchantOrderId;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date merchantTimestamp;

	@Column
	private Long issuerOrderId;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date issuerTimestamp;

	public Transaction() {
		
	}
	
	public Transaction(Long id, CardOwner seller, CardOwner buyer, String paymentURL, Date timestamp, Status status,
			String buyerPan, String sellerPan, Float amount,
			Long merchantOrderId, Date merchantTimestamp, Long issuerOrderId, Date issuerTimestamp) {
		super();
		this.id = id;
		this.seller = seller;
		this.buyer = buyer;
		this.paymentId = paymentURL;
		this.timestamp = timestamp;
		this.status = status;
		this.buyerPan = buyerPan;
		this.sellerPan = sellerPan;
		this.amount = amount;
		this.merchantOrderId = merchantOrderId;
		this.merchantTimestamp = merchantTimestamp;
		this.issuerOrderId = issuerOrderId;
		this.issuerTimestamp = issuerTimestamp;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CardOwner getSeller() {
		return seller;
	}

	public void setSeller(CardOwner seller) {
		this.seller = seller;
	}

	public CardOwner getBuyer() {
		return buyer;
	}

	public void setBuyer(CardOwner reciever) {
		this.buyer = reciever;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentURL) {
		this.paymentId = paymentURL;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getBuyerPan() {
		return buyerPan;
	}

	public void setBuyerPan(String buyerPan) {
		this.buyerPan = buyerPan;
	}

	public String getSellerPan() {
		return sellerPan;
	}

	public void setSellerPan(String sellerPan) {
		this.sellerPan = sellerPan;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public Long getMerchantOrderId() {
		return merchantOrderId;
	}

	public void setMerchantOrderId(Long merchantOrderId) {
		this.merchantOrderId = merchantOrderId;
	}

	public Date getMerchantTimestamp() {
		return merchantTimestamp;
	}

	public void setMerchantTimestamp(Date merchantTimestamp) {
		this.merchantTimestamp = merchantTimestamp;
	}

	public Long getIssuerOrderId() {
		return issuerOrderId;
	}

	public void setIssuerOrderId(Long issuerOrderId) {
		this.issuerOrderId = issuerOrderId;
	}

	public Date getIssuerTimestamp() {
		return issuerTimestamp;
	}

	public void setIssuerTimestamp(Date issuerTimestamp) {
		this.issuerTimestamp = issuerTimestamp;
	}

}
