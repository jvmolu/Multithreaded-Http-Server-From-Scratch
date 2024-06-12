import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.function.Function;

/*
* NOTE: Make sure encoding name is same as enum name [GZIP -> gzip] and enum name is in uppercase
*/
public enum ContentEncoding {

    GZIP("gzip", ContentEncodingAlgorithms::gzip);

    private final String encoding;
    private final Function<byte[], byte[]> encoder;

    ContentEncoding(String encoding, Function<byte[], byte[]> encoder) {
        this.encoding = encoding;
        this.encoder = encoder;
    }

    public String getEncoding() {
        return encoding;
    }

    public String encode(String content) {
        return Base64.getEncoder().encodeToString(encoder.apply(content.getBytes()));        
    }

    public static Boolean isSupported(String encoding) {
        try {
            return ContentEncoding.valueOf(encoding.toUpperCase()) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
