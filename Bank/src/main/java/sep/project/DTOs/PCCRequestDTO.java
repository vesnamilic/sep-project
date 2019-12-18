package sep.project.DTOs;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class PCCRequestDTO {

	@NotNull
	private Long acquirerOrderID;

	@NotNull
	private Date acquirerTimestamp;

	@NotNull
	private Long merchantOrderID;

	@NotNull
	private Date merchantTimestamp;

	@Length(min = 8, max = 19)
	private String senderPan;

	@NotNull
	@Length(min = 3, max = 4)
	private String cvv;

	@NotNull
	@Length(min = 1)
	private String name;

	@NotNull
	@Length(min = 1)
	private String lastName;

	@NotNull
	private int month;

	@NotNull
	private int year;

	private String returnURL;

	@NotNull
	private Float amount;

	private String recieverPan;

	private String sellerBankNumber;

	public PCCRequestDTO() {

	}

	/*public PCCRequestDTO(PCCRequest pccRequest) {
		
        this.cvv =  pccRequest.getCvv();
		this.name =  pccRequest.getName();
        this.lastName =  pccRequest.getLastName();
        this.month =  pccRequest.getMonth();
        this.year =  pccRequest.getYear();
        this.acquirerOrderID =  pccRequest.getAcquirerOrderID();
        this.acquirerTimestamp =  pccRequest.getAcquirerTimestamp();
        this.merchantOrderID =  pccRequest.getMerchantOrderID();
        this.merchantTimestamp =  pccRequest.getMerchantTimestamp();
        this.senderPan =  pccRequest.getSenderPan();
        this.returnURL =  pccRequest.getReturnURL();
        this.amount =  pccRequest.getAmount();
        this.recieverPan =  pccRequest.getRecieverPan();
        this.sellerBankNumber =  pccRequest.getSellerBankNumber();
        
    }*/

	public Long getAcquirerOrderID() {
		return acquirerOrderID;
	}

	public void setAcquirerOrderID(Long acquirerOrderID) {
		this.acquirerOrderID = acquirerOrderID;
	}

	public Date getAcquirerTimestamp() {
		return acquirerTimestamp;
	}

	public void setAcquirerTimestamp(Date acquirerTimestamp) {
		this.acquirerTimestamp = acquirerTimestamp;
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

	public String getSenderPan() {
		return senderPan;
	}

	public void setSenderPan(String senderPan) {
		this.senderPan = senderPan;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getReturnURL() {
		return returnURL;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public String getRecieverPan() {
		return recieverPan;
	}

	public void setRecieverPan(String recieverPan) {
		this.recieverPan = recieverPan;
	}

	public String getSellerBankNumber() {
		return sellerBankNumber;
	}

	public void setSellerBankNumber(String sellerBankNumber) {
		this.sellerBankNumber = sellerBankNumber;
	}

}
