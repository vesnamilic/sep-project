package sep.project.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import sep.project.dto.SubscriptionPlanDTO;

@Entity
public class SubscriptionPlan {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column
	@Enumerated(EnumType.STRING)
	private SubscriptionType type;
	
	@Column
	@Enumerated(EnumType.STRING)
	private SubscriptionFrequency frequency;
	
	@Column
	private Integer cyclesNumber;
	
	@ManyToOne
	private Seller seller;
	
	public SubscriptionPlan() {
		
	}

	public SubscriptionPlan(Long id, SubscriptionType type, SubscriptionFrequency frequency, Integer cyclesNumber, Seller seller) {
		this.id = id;
		this.type = type;
		this.frequency = frequency;
		this.cyclesNumber = cyclesNumber;
		this.seller = seller;
	}
	
	public SubscriptionPlan(SubscriptionPlanDTO dto, Seller seller) {
		this.type = SubscriptionType.FIXED;
		this.frequency = dto.getFrequency();
		this.cyclesNumber = dto.getCyclesNumber();
		this.seller = seller;
	}

	public SubscriptionType getType() {
		return type;
	}

	public void setType(SubscriptionType type) {
		this.type = type;
	}

	public SubscriptionFrequency getFrequency() {
		return frequency;
	}

	public void setFrequency(SubscriptionFrequency frequency) {
		this.frequency = frequency;
	}

	public Integer getCyclesNumber() {
		return cyclesNumber;
	}

	public void setCyclesNumber(Integer cyclesNumber) {
		this.cyclesNumber = cyclesNumber;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}
	
}
