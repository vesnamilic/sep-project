package sep.project.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class SubscriptionInformationDTO {

	@NotNull
	@Email
	private String email;
	
	private String successUrl;
	
	
	private String errorUrl;
	
	
	private String failedUrl;

	public SubscriptionInformationDTO() {
	
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

}
