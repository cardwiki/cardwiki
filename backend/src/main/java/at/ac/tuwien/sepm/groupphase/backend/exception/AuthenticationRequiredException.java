package at.ac.tuwien.sepm.groupphase.backend.exception;

public class AuthenticationRequiredException extends RuntimeException {
    public AuthenticationRequiredException(String msg) {
        super(msg);
    }
}
