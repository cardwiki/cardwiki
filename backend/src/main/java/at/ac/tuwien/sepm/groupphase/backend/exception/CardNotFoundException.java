package at.ac.tuwien.sepm.groupphase.backend.exception;

public class CardNotFoundException extends NotFoundException {
        public CardNotFoundException() {
        }

        public CardNotFoundException(String message) {
            super(message);
        }

        public CardNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

        public CardNotFoundException(Exception e) {
            super(e);
        }
}
