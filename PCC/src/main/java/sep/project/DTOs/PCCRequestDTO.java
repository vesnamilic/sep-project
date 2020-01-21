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

	@NotNull
	@Length(min = 8, max = 19)
	private String buyerPan;

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

	@NotNull
	private Float amount;

	private String sellerPan;

	private String sellerBankNumber;

	public PCCRequestDTO() {
		
	}

	public PCCRequestDTO(@NotNull Long acquirerOrderID, @NotNull Date acquirerTimestamp, @NotNull Long merchantOrderID,
			@NotNull Date merchantTimestamp, @NotNull @Length(min = 8, max = 19) String buyerPan,
			@NotNull @Length(min = 3, max = 4) String cvv, @NotNull @Length(min = 1) String name,
			@NotNull @Length(min = 1) String lastName, @NotNull int month, @NotNull int year, @NotNull Float amount,
			String sellerPan, String sellerBankNumber) {
		super();
		this.acquirerOrderID = acquirerOrderID;
		this.acquirerTimestamp = acquirerTimestamp;
		this.merchantOrderID = merchantOrderID;
		this.merchantTimestamp = merchantTimestamp;
		this.buyerPan = buyerPan;
		this.cvv = cvv;
		this.name = name;
		this.lastName = lastName;
		this.month = month;
		this.year = year;
		this.amount = amount;
		this.sellerPan = sellerPan;
		this.sellerBankNumber = sellerBankNumber;
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

	public String getBuyerPan() {
		return buyerPan;
	}

	public void setBuyerPan(String buyerPan) {
		this.buyerPan = buyerPan;
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

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public String getSellerPan() {
		return sellerPan;
	}

	public void setSellerPan(String sellerPan) {
		this.sellerPan = sellerPan;
	}

	public String getSellerBankNumber() {
		return sellerBankNumber;
	}

	public void setSellerBankNumber(String sellerBankNumber) {
		this.sellerBankNumber = sellerBankNumber;
	}

}
