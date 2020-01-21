package sep.project.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import sep.project.utils.CryptoConverter;

@Entity
public class Client {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private String email;
	
    @Column
    @Convert(converter = CryptoConverter.class)
    private String clientId;
    
    @Column
    @Convert(converter = CryptoConverter.class)
    private String clientSecret;
    
    @Column
    private String billingPlan;
	
	public Client(){
		
	}
	
	public Client(Long id, String clientId, String email, String clientSecret){
		this.id = id;
		this.email = email;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getBillingPlan() {
		return billingPlan;
	}

	public void setBillingPlan(String billingPlan) {
		this.billingPlan = billingPlan;
	}

}
