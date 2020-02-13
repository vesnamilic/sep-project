package sep.project.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import sep.project.model.UserOrder;

public class OrderInformationDTO {

	@NotNull
	@Email
	private String email;
	
	@NotNull
	private Long orderId;

	@NotNull
	@Positive
	private Double paymentAmount;

	@NotNull
	private String paymentCurrency;
	
	@NotNull
	private String successUrl;
	
	@NotNull
	private String errorUrl;
	
	@NotNull
	private String failedUrl;


	public OrderInformationDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public OrderInformationDTO(UserOrder userOrder) {
		this.email = userOrder.getSeller().getEmail();
		this.paymentAmount = userOrder.getPaymentAmount();
		this.paymentCurrency = userOrder.getPaymentCurrency();
		this.orderId = userOrder.getOrderId();
		this.successUrl = userOrder.getSuccessUrl();
		this.errorUrl = userOrder.getErrorUrl();
		this.failedUrl = userOrder.getFailedUrl();
	}

	public Double getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(Double paymentAmount) {
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


}
