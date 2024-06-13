package enums;
public enum HttpStatus {
    
    OK(200, "OK"),
    CREATED(201, "Created"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    CONFLICT(409, "Conflict"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");
    
    private final Integer code;
    private final String message;
    
    HttpStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String toString() {
        return code + " " + message;
    }

}
