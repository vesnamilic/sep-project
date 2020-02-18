package sep.project.dto;

public class RegistrationDTO {
	private String email;
    private String name;
    private String confirmationLink;
    private String returnLink;
    
    public RegistrationDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConfirmationLink() {
		return confirmationLink;
	}

	public void setConfirmationLink(String confirmationLink) {
		this.confirmationLink = confirmationLink;
	}

	public String getReturnLink() {
		return returnLink;
	}

	public void setReturnLink(String returnLink) {
		this.returnLink = returnLink;
	}
    
}
