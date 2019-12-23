package sep.project.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 * Klasa koja predstavlja prodavca
 * @author Vesna Milic
 *
 */
@Entity
@SequenceGenerator(name="seqMer", initialValue=100, allocationSize=50)
public class Merchant {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String email;
	
	private String userToken;

	public Merchant() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Merchant(Long id, String email, String userToken) {
		super();
		this.id = id;
		this.email = email;
		this.userToken = userToken;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
	

}
