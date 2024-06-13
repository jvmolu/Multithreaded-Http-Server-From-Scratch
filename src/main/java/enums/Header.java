package enums;

public enum Header {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_ENCODING("Content-Encoding"),
    ACCEPT_ENCODING("Accept-Encoding"),
    CONNECTION("Connection"),
    USER_AGENT("User-Agent");

    private final String header;

    Header(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public static Header fromString(String header) {
        for (Header h : Header.values()) {
            if (h.header.equalsIgnoreCase(header)) {
                return h;
            }
        }
        return null;
    }

}
