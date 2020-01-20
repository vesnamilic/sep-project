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
public class Request {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private Long acquirerOrderID; // id transkacije kreirane u banci prodavca koja se referencira na ovaj zahtev
									// na pcc
	
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date acquirerTimestamp;

	private Long issuerOrderID; // id transkacije kreirane u banci prodavca koja se referencira na ovaj zahtev
								// na pcc

	@Temporal(TemporalType.TIMESTAMP)
	private Date issuerTimestamp;

	@Column
	private Long merchantOrderId;

	@ManyToOne
	private Bank sellerBank;

	@ManyToOne
	private Bank customerBank;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;

	@Enumerated(EnumType.STRING)
	private Status status;

	private String returnURL;

	public Request() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getAcquirerOrderID() {
		return acquirerOrderID;
	}

	public void setAcquirerOrderID(Long acquirerOrderID) {
		this.acquirerOrderID = acquirerOrderID;
	}

	public Date getAcquirerTimestamp() {
		return acquirerTimestamp;
	}

	public void setAcquirerTimestamp(Date acquirerTimestamp) {
		this.acquirerTimestamp = acquirerTimestamp;
	}

	public Long getIssuerOrderID() {
		return issuerOrderID;
	}

	public void setIssuerOrderID(Long issuerOrderID) {
		this.issuerOrderID = issuerOrderID;
	}

	public Date getIssuerTimestamp() {
		return issuerTimestamp;
	}

	public void setIssuerTimestamp(Date issuerTimestamp) {
		this.issuerTimestamp = issuerTimestamp;
	}

	public Long getMerchantOrderId() {
		return merchantOrderId;
	}

	public void setMerchantOrderId(Long merchantOrderId) {
		this.merchantOrderId = merchantOrderId;
	}

	public Bank getSellerBank() {
		return sellerBank;
	}

	public void setSellerBank(Bank sellerBank) {
		this.sellerBank = sellerBank;
	}

	public Bank getCustomerBank() {
		return customerBank;
	}

	public void setCustomerBank(Bank customerBank) {
		this.customerBank = customerBank;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getReturnURL() {
		return returnURL;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

}
