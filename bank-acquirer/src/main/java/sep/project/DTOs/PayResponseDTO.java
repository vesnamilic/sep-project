package sep.project.DTOs;

public class PayResponseDTO {

	private String url;
	private String message;
	
	public PayResponseDTO() {
		url="";
		message="";
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
