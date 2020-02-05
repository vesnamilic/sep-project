package sep.project.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.joda.time.DateTime;

import sep.project.dto.SubscriptionInformationDTO;

@Entity
public class Subscription {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private String uuid;
	
	@Column
	private Date expirationDate;
	
	@Column
	@Enumerated(EnumType.STRING)
	private SubscriptionStatus subscriptionStatus;
	
	@Column
	private double paymentAmount;
	
	@Column
	private String paymentCurrency;
	
	@Column
	private String successUrl;
	
	@Column
	private String errorUrl;
	
	@Column
	private String failedUrl;
	
	@ManyToOne
	private Seller seller;
	
	public Subscription() {
		
	}
	
	public Subscription(SubscriptionInformationDTO subscriptionDTO, Seller seller) {
		Date date = new Date();
		DateTime originalDateTime = new DateTime(date);
		DateTime expirationDateTime = originalDateTime.plusMinutes(15);
		
		this.uuid = UUID.randomUUID().toString();
		this.expirationDate = expirationDateTime.toDate();
		this.subscriptionStatus = SubscriptionStatus.CREATED;
		this.successUrl = subscriptionDTO.getSuccessUrl();
		this.errorUrl = subscriptionDTO.getErrorUrl();
		this.failedUrl = subscriptionDTO.getFailedUrl();
		this.paymentAmount = subscriptionDTO.getPaymentAmount();
		this.paymentCurrency = subscriptionDTO.getPaymentCurrency();
		this.seller = seller;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}

	public String getErrorUrl() {
		return errorUrl;
	}

	public void setErrorUrl(String errorUrl) {
		this.errorUrl = errorUrl;
	}

	public String getFailedUrl() {
		return failedUrl;
	}

	public void setFailedUrl(String failedUrl) {
		this.failedUrl = failedUrl;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public SubscriptionStatus getSubscriptionStatus() {
		return subscriptionStatus;
	}

	public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
		this.subscriptionStatus = subscriptionStatus;
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
	
}
