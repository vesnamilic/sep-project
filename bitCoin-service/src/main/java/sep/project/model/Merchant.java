package sep.project.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import sep.project.encryption.CryptoConverter;

/**
 * Klasa koja predstavlja prodavca
 * @author Vesna Milic
 *
 */
@Entity
@SequenceGenerator(name="seqMer", initialValue=100, allocationSize=50)
public class Merchant {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqMer")
	private Long id;
	
	@Column
	private String email;
	
	@Column
	@Convert(converter = CryptoConverter.class)
	private String token;

	public Merchant() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Merchant(Long id, String email, String token) {
		super();
		this.id = id;
		this.email = email;
		this.token = token;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
	

}
