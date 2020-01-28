package sep.project.dto;

public class OrderResponseDTO {

	private String uuid;
	
	private String redirectUrl;

	public OrderResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	
}
