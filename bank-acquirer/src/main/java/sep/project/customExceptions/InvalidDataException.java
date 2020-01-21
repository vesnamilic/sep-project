package sep.project.customExceptions;

public class InvalidDataException extends Exception {

	private static final long serialVersionUID = -4392692034922761658L;

	public InvalidDataException(String s) {
		super(s);
	}
}
