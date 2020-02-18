package sep.project.dto;

public class RegistrationResponseDTO {

	private String responseUrl;
	
	public RegistrationResponseDTO() {
		
	}

	public RegistrationResponseDTO(String responseUrl) {
		super();
		this.responseUrl = responseUrl;
	}

	public String getResponseUrl() {
		return responseUrl;
	}

	public void setResponseUrl(String responseUrl) {
		this.responseUrl = responseUrl;
	}
	
}
