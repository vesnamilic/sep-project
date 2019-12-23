package sep.project.dto;

import java.util.Date;

public class PaymentResponseDTO {

	private long id;

	private String status;

	private boolean do_not_convert;

	private String price_currency;

	private String price_amount;

	private boolean lightning_network;

	private String underpaid_amount;

	private String overpaid_amount;

	private boolean is_refundable;

	private String receive_currency;

	private String receive_amount;

	private Date created_at;

	private String order_id;

	private String payment_url;

	private String token;

	public PaymentResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPrice_currency() {
		return price_currency;
	}

	public void setPrice_currency(String price_currency) {
		this.price_currency = price_currency;
	}

	public String getPrice_amount() {
		return price_amount;
	}

	public void setPrice_amount(String price_amount) {
		this.price_amount = price_amount;
	}

	public String getReceive_currency() {
		return receive_currency;
	}

	public void setReceive_currency(String receive_currency) {
		this.receive_currency = receive_currency;
	}

	public String getReceive_amount() {
		return receive_amount;
	}

	public void setReceive_amount(String receive_amount) {
		this.receive_amount = receive_amount;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getPayment_url() {
		return payment_url;
	}

	public void setPayment_url(String payment_url) {
		this.payment_url = payment_url;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isDo_not_convert() {
		return do_not_convert;
	}

	public void setDo_not_convert(boolean do_not_convert) {
		this.do_not_convert = do_not_convert;
	}

	public boolean isLightning_network() {
		return lightning_network;
	}

	public void setLightning_network(boolean lightning_network) {
		this.lightning_network = lightning_network;
	}

	public String getUnderpaid_amount() {
		return underpaid_amount;
	}

	public void setUnderpaid_amount(String underpaid_amount) {
		this.underpaid_amount = underpaid_amount;
	}

	public String getOverpaid_amount() {
		return overpaid_amount;
	}

	public void setOverpaid_amount(String overpaid_amount) {
		this.overpaid_amount = overpaid_amount;
	}

	public boolean isIs_refundable() {
		return is_refundable;
	}

	public void setIs_refundable(boolean is_refundable) {
		this.is_refundable = is_refundable;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Date getCreated_at() {
		return created_at;
	}

	@Override
	public String toString() {
		return "PaymentResponseDTO [id=" + id + ", status=" + status + ", do_not_convert=" + do_not_convert
				+ ", price_currency=" + price_currency + ", price_amount=" + price_amount + ", lightning_network="
				+ lightning_network + ", underpaid_amount=" + underpaid_amount + ", overpaid_amount=" + overpaid_amount
				+ ", is_refundable=" + is_refundable + ", receive_currency=" + receive_currency + ", receive_amount="
				+ receive_amount + ", created_at=" + created_at + ", order_id=" + order_id + ", payment_url="
				+ payment_url + ", token=" + token + "]";
	}

}
