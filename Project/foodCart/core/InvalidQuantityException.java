package core;

public class InvalidQuantityException extends Exception {
	public InvalidQuantityException() {
		super("Quantity is less than 1.");
	}
}
