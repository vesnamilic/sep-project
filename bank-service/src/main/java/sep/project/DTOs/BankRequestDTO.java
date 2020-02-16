package sep.project.DTOs;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class BankRequestDTO {

	@NotNull
	private String merchantID;
	
	@NotNull
	private String merchantPass;
	
	@NotNull
	private Double amount;
	
	@NotNull
	private Long merchantOrderID;
	
	@NotNull
	private Date merchantTimestamp;
	
	public BankRequestDTO () {
		
	}
	
	public BankRequestDTO(@NotNull String merchantID, @NotNull String merchantPass, @NotNull Double amount,
			@NotNull Long merchantOrderID, @NotNull Date merchantTimestamp) {
		super();
		this.merchantID = merchantID;
		this.merchantPass = merchantPass;
		this.amount = amount;
		this.merchantOrderID = merchantOrderID;
		this.merchantTimestamp = merchantTimestamp;
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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
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
