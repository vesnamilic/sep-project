package sep.project.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import sep.project.DTOs.PCCRequestDTO;

@Entity
public class PCCRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

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

	public PCCRequest() {

	}

	public PCCRequest(PCCRequestDTO pccRequestDTO) {
		this.acquirerOrderID = pccRequestDTO.getAcquirerOrderID();
		this.acquirerTimestamp = pccRequestDTO.getAcquirerTimestamp();
		this.merchantOrderID = pccRequestDTO.getMerchantOrderID();
		this.merchantTimestamp = pccRequestDTO.getMerchantTimestamp();
		this.senderPan = pccRequestDTO.getSenderPan();
		this.cvv = pccRequestDTO.getCvv();
		this.name = pccRequestDTO.getName();
		this.lastName = pccRequestDTO.getLastName();
		this.month = pccRequestDTO.getMonth();
		this.year = pccRequestDTO.getYear();
		this.returnURL = pccRequestDTO.getReturnURL();
		this.amount = pccRequestDTO.getAmount();
		this.recieverPan = pccRequestDTO.getRecieverPan();
		this.sellerBankNumber = pccRequestDTO.getSellerBankNumber();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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
