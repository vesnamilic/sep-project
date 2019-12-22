package sep.project.DTOs;

public class PayResponseDTO {

	private String location;
	private String message;
	
	public PayResponseDTO() {
		location="";
		message="";
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
