package sep.project.dto;

public class BillingAgreementDTO {
	
	private String email;
	
	private Long billingPlanId;
	
	public BillingAgreementDTO() {
		
	}
	
	public BillingAgreementDTO(String email, Long billingPlanId) {
		this.email = email;
		this.billingPlanId = billingPlanId;
	}

	public BillingAgreementDTO(String email) {	
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getBillingPlanId() {
		return billingPlanId;
	}

	public void setBillingPlanId(Long billingPlanId) {
		this.billingPlanId = billingPlanId;
	}
	
}
