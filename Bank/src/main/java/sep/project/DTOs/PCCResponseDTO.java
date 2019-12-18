package sep.project.DTOs;

import java.util.Date;

import sep.project.enums.Status;

public class PCCResponseDTO {

	private Long issuerOrderID;
	
	private Date issuerTimestamp;
	
	private Status status;
	
	private Long acquirerOrderID;
	
	private Long merchantOrderID;

	public PCCResponseDTO() {
		
	}
	
	public Long getIssuerOrderID() {
		return issuerOrderID;
	}

	public void setIssuerOrderID(Long issuerOrderID) {
		this.issuerOrderID = issuerOrderID;
	}

	public Date getIssuerTimestamp() {
		return issuerTimestamp;
	}

	public void setIssuerTimestamp(Date issuerTimestamp) {
		this.issuerTimestamp = issuerTimestamp;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Long getAcquirerOrderID() {
		return acquirerOrderID;
	}

	public void setAcquirerOrderID(Long acquirerOrderID) {
		this.acquirerOrderID = acquirerOrderID;
	}

	public Long getMerchantOrderID() {
		return merchantOrderID;
	}

	public void setMerchantOrderID(Long merchantOrderID) {
		this.merchantOrderID = merchantOrderID;
	}

}
