package sep.project.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import sep.project.model.SubscriptionFrequency;
import sep.project.model.SubscriptionPlan;
import sep.project.model.SubscriptionType;


public class SubscriptionPlanDTO {
	
	private Long id;
		
	@NotNull
	private SubscriptionFrequency frequency;
	
	private SubscriptionType type;
	
	@NotNull
	@Positive
	private Integer cyclesNumber;
	
	public SubscriptionPlanDTO(){
		
	}
	
	public SubscriptionPlanDTO(SubscriptionPlan subscriptionPlan) {
		this.id = subscriptionPlan.getId();
		this.frequency = subscriptionPlan.getFrequency();
		this.cyclesNumber = subscriptionPlan.getCyclesNumber();
		this.type = subscriptionPlan.getType();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SubscriptionFrequency getFrequency() {
		return frequency;
	}

	public void setFrequency(SubscriptionFrequency frequency) {
		this.frequency = frequency;
	}

	public SubscriptionType getType() {
		return type;
	}

	public void setType(SubscriptionType type) {
		this.type = type;
	}

	public Integer getCyclesNumber() {
		return cyclesNumber;
	}

	public void setCyclesNumber(Integer cyclesNumber) {
		this.cyclesNumber = cyclesNumber;
	}

}