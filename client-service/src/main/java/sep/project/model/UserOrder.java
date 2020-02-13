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
public class UserOrder {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="orderId")
	private Long orderId;
	
	@Column(name = "uuid")
	private String uuid;
	
	@Column(name = "expirationDate")
	private Date expirationDate;
	
	@Column(name = "orderStatus")
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;
	
	@Column(name = "paymentAmount")
	private double paymentAmount;
	
	@Column(name = "paymentCurrency")
	private String paymentCurrency;
	
	@Column(name = "successUrl")
	private String successUrl;
	
	@Column(name = "errorUrl")
	private String errorUrl;
	
	@Column(name = "failedUrl")
	private String failedUrl;
	
	@Column(name = "paymentMethod")
	private String paymentMethod;
	
	@ManyToOne
	private Seller seller;

	public UserOrder() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserOrder(String uuid, Date expirationDate, OrderStatus orderStatus, double paymentAmount,
			String paymentCurrency, String successUrl, String errorUrl, String failedUrl,Long orderId, Seller seller) {
		super();
		this.uuid = uuid;
		this.expirationDate = expirationDate;
		this.orderStatus = orderStatus;
		this.paymentAmount = paymentAmount;
		this.paymentCurrency = paymentCurrency;
		this.successUrl = successUrl;
		this.errorUrl = errorUrl;
		this.failedUrl = failedUrl;
		this.seller = seller;
		this.orderId = orderId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
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

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
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

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

}
