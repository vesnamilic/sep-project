package sep.project.model;

public enum TransactionStatus {
	NEW("new"),
	PENDING("pending"),
	CONFIRMING("confirming"),
	PAID("paid"),
	INVALID("invalid"),
	EXPIRED("expired"),
	CANCELED("canceled"),
	REFUNDED("refunded");

	public final String name;

	private TransactionStatus(String name) {
		this.name = name;
	}
}
