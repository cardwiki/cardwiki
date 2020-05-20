package at.ac.tuwien.sepm.groupphase.backend.exception;


public class DeckNotFoundException extends NotFoundException {

    public DeckNotFoundException() {
    }

    public DeckNotFoundException(String message) {
        super(message);
    }

    public DeckNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeckNotFoundException(Exception e) {
        super(e);
    }
}
