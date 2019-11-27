package sep.project.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Seller {
	
	@Id
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

}
