package stellarium.initials;

public class RegistrationException extends RuntimeException {

	public RegistrationException(String string) {
		super("Invalid Registration: " + string + " (Code Error!)");
	}
	
}
