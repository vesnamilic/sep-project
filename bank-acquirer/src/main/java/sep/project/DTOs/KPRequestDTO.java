package sep.project.DTOs;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class KPRequestDTO {
	
	@NotNull
	private String merchantID;
	
	@NotNull
	private String merchantPass;
	
	@NotNull
	private Float amount;
	
	@NotNull
	private Long merchantOrderID;
	
	@NotNull
	private Date merchantTimestamp;
	
	public KPRequestDTO() {
		
	}

	public String getMerchantID() {
		return merchantID;
	}

	public void setMerchantID(String merchantID) {
		this.merchantID = merchantID;
	}

	public String getMerchantPass() {
		return merchantPass;
	}

	public void setMerchantPass(String merchantPass) {
		this.merchantPass = merchantPass;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public Long getMerchantOrderID() {
		return merchantOrderID;
	}

	public void setMerchantOrderID(Long merchantOrderID) {
		this.merchantOrderID = merchantOrderID;
	}

	public Date getMerchantTimestamp() {
		return merchantTimestamp;
	}

	public void setMerchantTimestamp(Date merchantTimestamp) {
		this.merchantTimestamp = merchantTimestamp;
	}

}
