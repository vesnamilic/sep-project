package sep.project.security;

import java.util.Date;


public class UserTokenState {

	private String token;
	private String type = "Bearer";
	private String username;
	
	public UserTokenState() {
		super();
		this.token = null;
		this.username = null;
	}
	
	public UserTokenState(String token,String email) {
		super();
		this.token = token;
		this.username = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


}
