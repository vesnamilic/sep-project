package sep.project.dto;

import javax.validation.constraints.NotNull;

public class SubmitSubscriptionDTO {
	
	@NotNull
	private String paymentMethod;
	
	@NotNull
	private Long subscriptionPlanId;
	
	SubmitSubscriptionDTO(){
		
	}
	
	SubmitSubscriptionDTO(String paymentMethod, Long subscriptionPlanId) {
		this.paymentMethod = paymentMethod;
		this.subscriptionPlanId = subscriptionPlanId;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Long getSubscriptionPlanId() {
		return subscriptionPlanId;
	}

	public void setSubscriptionPlanId(Long subscriptionPlanId) {
		this.subscriptionPlanId = subscriptionPlanId;
	}

}
