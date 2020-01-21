package sep.project.customExceptions;

public class PaymentException extends Exception {

	private static final long serialVersionUID = -138300236995764817L;

	public PaymentException(String s) {
		super(s);
	}

}