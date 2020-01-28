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
public class UserSubscription {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "uuid")
	private String uuid;
	
	@Column(name = "orderStatus")
	@Enumerated(EnumType.STRING)
	private OrderStatus subscriptionStatus;
	
	@ManyToOne
	private RegisteredUser buyer;
	
	@ManyToOne
	private Magazine magazine;

	public UserSubscription() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserSubscription(String uuid, Date expirationDate, OrderStatus subscriptionStatus, String successUrl, String errorUrl, String failedUrl, RegisteredUser buyer) {
		super();
		this.uuid = uuid;
		this.subscriptionStatus = subscriptionStatus;
		this.buyer = buyer;
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

	public RegisteredUser getBuyer() {
		return buyer;
	}

	public void setBuyer(RegisteredUser buyer) {
		this.buyer = buyer;
	}

	public Magazine getMagazine() {
		return magazine;
	}

	public void setMagazine(Magazine magazine) {
		this.magazine = magazine;
	}

	public OrderStatus getSubscriptionStatus() {
		return subscriptionStatus;
	}

	public void setSubscriptionStatus(OrderStatus subscriptionStatus) {
		this.subscriptionStatus = subscriptionStatus;
	}
	
}
