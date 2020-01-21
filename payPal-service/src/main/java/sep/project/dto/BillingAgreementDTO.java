package sep.project.dto;

public class BillingAgreementDTO {
	
	private String email;
	
	public BillingAgreementDTO() {
		
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
	
}
