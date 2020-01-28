package sep.project.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class BillingAgreementDTO {
	
	@NotNull
	@Email
	private String email;
	
	@NotNull
	private Long billingPlanId;
	
	@NotNull
	private String successUrl;
	
	@NotNull
	private String errorUrl;
	
	@NotNull
	private String failedUrl;
	
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
	
}
