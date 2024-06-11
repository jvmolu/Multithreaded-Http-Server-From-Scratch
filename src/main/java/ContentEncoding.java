import java.util.HashSet;

public enum ContentEncoding {

    GZIP("gzip");

    private static final HashSet<String> supportedEncodings = new HashSet<String>();
    private final String encoding;

    static {
        for (ContentEncoding encoding : ContentEncoding.values()) {
            supportedEncodings.add(encoding.getEncoding());
        }
    }

    ContentEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return encoding;
    }

    public static Boolean isSupported(String encoding) {
        return supportedEncodings.contains(encoding);
    }
}
