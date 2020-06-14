package at.ac.tuwien.sepm.groupphase.backend.exception;

public class InsufficientAuthorizationException extends RuntimeException {
    public InsufficientAuthorizationException(String msg) {
        super(msg);
    }
}
