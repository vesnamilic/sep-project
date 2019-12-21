package sep.project.DTOs;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class BuyerDTO {

	@NotNull
	private String name;

	@NotNull
	private String lastName;

	@NotNull
	@Length(min = 8, max = 19)
	private String pan; //Prvih 6 cifara jedinstveno identifikuju banku

	@NotNull
	@Length(min = 3, max = 4)
	private String cvv;

	@NotNull
	private int month;

	@NotNull
	private int year;

	public BuyerDTO() {

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

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
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

}
