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
	
	private String successURL;
	
	private String failedURL;
	
	private String errorURL;

	public BankRequestDTO () {
		
	}
	
	public BankRequestDTO(@NotNull String merchantID, @NotNull String merchantPass, @NotNull Double amount,
			@NotNull Long merchantOrderID, @NotNull Date merchantTimestamp, String successURL, String failedURL,
			String errorURL) {
		super();
		this.merchantID = merchantID;
		this.merchantPass = merchantPass;
		this.amount = amount;
		this.merchantOrderID = merchantOrderID;
		this.merchantTimestamp = merchantTimestamp;
		this.successURL = successURL;
		this.failedURL = failedURL;
		this.errorURL = errorURL;
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

	public String getSuccessURL() {
		return successURL;
	}

	public void setSuccessURL(String successURL) {
		this.successURL = successURL;
	}

	public String getFailedURL() {
		return failedURL;
	}

	public void setFailedURL(String failedURL) {
		this.failedURL = failedURL;
	}

	public String getErrorURL() {
		return errorURL;
	}

	public void setErrorURL(String errorURL) {
		this.errorURL = errorURL;
	}
	
}
