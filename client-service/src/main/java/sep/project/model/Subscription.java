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
	private String successUrl;
	
	@Column
	private String errorUrl;
	
	@Column
	private String failedUrl;
	
	@ManyToOne
	private Seller seller;
	
	public Subscription() {
		
	}

	public Subscription(String uuid, Date expirationDate, SubscriptionStatus subscriptionStatus, String successUrl,
			String errorUrl, String failedUrl, Seller seller) {
		this.uuid = uuid;
		this.expirationDate = expirationDate;
		this.subscriptionStatus = subscriptionStatus;
		this.successUrl = successUrl;
		this.errorUrl = errorUrl;
		this.failedUrl = failedUrl;
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
	
}
