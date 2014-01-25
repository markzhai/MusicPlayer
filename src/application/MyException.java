package application;

public class MyException extends Exception {
	private static final long serialVersionUID = 7229989309679792978L;

	MyException(String ErrorMessage) {
		super(ErrorMessage);
	}
}
