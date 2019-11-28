package sep.project.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import sep.projects.dto.SellerDTO;

@Entity
public class Seller {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
    @Column
    private String name;
    
    @Column
    private boolean deleted;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;
    
    @ManyToMany
    private Set<PaymentMethod> paymentMethods = new HashSet<>();
    
    public Seller() {
    	
    }
    
    public Seller(SellerDTO dto) {
    	this.id = dto.getId();
    	this.name = dto.getName();
    	this.deleted = dto.isDeleted();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Set<PaymentMethod> getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(Set<PaymentMethod> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

}
