package at.ac.tuwien.sepm.groupphase.backend.exception;

public class BadRequestException extends RuntimeException {

    private String fieldname;
    private String description;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String fieldname, String description, String message) {
        super(message);
        this.fieldname = fieldname;
        this.description = description;
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
