package sep.projects.dto;

import sep.project.model.Client;
import sep.project.model.Seller;

public class SellerDTO {
	
	private Long id;
	private String name;
	private boolean deleted;
	private Long clientId;
	
	public SellerDTO() {
		
	}
	
	public SellerDTO(Seller seller) {
		this.clientId = seller.getClient().getId();
		this.id = seller.getId();
		this.name = seller.getName();
		this.deleted = seller.isDeleted();
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

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

}
