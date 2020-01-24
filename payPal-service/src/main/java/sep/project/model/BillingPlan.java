package sep.project.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BillingPlan {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private String billingPlanId;
	
	@Column
	private Double paymentAmount;
	
	@Column
	private String paymentCurrency;
	
	@Column
	@Enumerated(EnumType.STRING)
	private BillingPlanFrequency frequency;
	
	@Column
	@Enumerated(EnumType.STRING)
	private BillingType type;
	
	BillingPlan(){
		
	}

	public BillingPlan(Double paymentAmount, String paymentCurrency, BillingPlanFrequency frequency, BillingType type, String billingPlanId) {
		this.paymentAmount = paymentAmount;
		this.paymentCurrency = paymentCurrency;
		this.frequency = frequency;
		this.type = type;
		this.billingPlanId = billingPlanId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBillingPlanId() {
		return billingPlanId;
	}

	public void setBillingPlanId(String billingPlanId) {
		this.billingPlanId = billingPlanId;
	}

	public Double getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(Double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getPaymentCurrency() {
		return paymentCurrency;
	}

	public void setPaymentCurrency(String paymentCurrency) {
		this.paymentCurrency = paymentCurrency;
	}

	public BillingPlanFrequency getFrequency() {
		return frequency;
	}

	public void setFrequency(BillingPlanFrequency frequency) {
		this.frequency = frequency;
	}

	public BillingType getType() {
		return type;
	}

	public void setType(BillingType type) {
		this.type = type;
	}
	
}
