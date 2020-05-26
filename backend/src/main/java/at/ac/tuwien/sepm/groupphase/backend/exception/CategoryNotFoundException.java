package at.ac.tuwien.sepm.groupphase.backend.exception;

public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException() {
    }

    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoryNotFoundException(Exception e) {
        super(e);
    }
}
