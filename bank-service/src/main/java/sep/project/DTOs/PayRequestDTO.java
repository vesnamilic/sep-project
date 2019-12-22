package sep.project.DTOs;

public class PayRequestDTO {

	private String email;

	private Float priceAmount;

	public PayRequestDTO() {
		
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Float getPriceAmount() {
		return priceAmount;
	}

	public void setPriceAmount(Float priceAmount) {
		this.priceAmount = priceAmount;
	}

}
