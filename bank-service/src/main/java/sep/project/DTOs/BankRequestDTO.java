package sep.project.DTOs;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class BankRequestDTO {

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

	@NotNull
	private String successURL;

	@NotNull
	private String failedURL;

	@NotNull
	private String errorURL;
	
	public BankRequestDTO() {
		
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

	public void setMerchantPass(String merchantPassword) {
		this.merchantPass = merchantPassword;
	}

	public float getAmount() {
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
