import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class CCHttpResponse {
    
    private HttpStatus status;
    private String protocol;
    private HashMap<String, String> headers;
    private byte[] body;
    
    public CCHttpResponse() {
        this.status = HttpStatus.OK;
        this.protocol = "";
        this.headers = new HashMap<String, String>();
        this.body = new byte[0];
    }
    
    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers.putAll(headers);
    }

    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public static class Builder {
        
        private HttpStatus status;
        private String protocol;
        private HashMap<String, String> headers;
        private byte[] body;

        public Builder() {
            this.status = HttpStatus.OK;
            this.protocol = "";
            this.headers = new HashMap<String, String>();
            this.body = new byte[0];
        }

        public Builder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder headers(HashMap<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public CCHttpResponse build() {
            CCHttpResponse response = new CCHttpResponse();
            response.status = this.status;
            response.protocol = this.protocol;
            response.headers = this.headers;
            response.body = this.body;
            // Content-Encoding
            String encoding = this.headers.get("Content-Encoding");
            System.out.println("Encoding response using " + encoding);
            if (encoding != null && ContentEncoding.isSupported(encoding)) {
                System.out.println("Encoding response using " + encoding);
                byte[] encodedData = ContentEncoding.valueOf(encoding.toUpperCase()).encode(this.body);
                response.headers.put("Content-Length", String.valueOf(encodedData.length));
                response.body = encodedData;
            }
            return response;
        }
    }

    @Override
    public String toString() {
        String response = protocol + " " + status.toString() + "\r\n";
        for (String key : headers.keySet()) {
            response += key + ": " + headers.get(key) + "\r\n";
        }
        response += "\r\n" + new String(body, StandardCharsets.UTF_8);
        return response;
    }

    public void writeToStream(OutputStream outputStream) throws IOException {
        outputStream.write(protocol.getBytes());
        outputStream.write(" ".getBytes());
        outputStream.write(status.toString().getBytes());
        outputStream.write("\r\n".getBytes());
        for (String key : headers.keySet()) {
            outputStream.write(key.getBytes());
            outputStream.write(": ".getBytes());
            outputStream.write(headers.get(key).getBytes());
            outputStream.write("\r\n".getBytes());
        }
        outputStream.write("\r\n".getBytes());
        outputStream.write(body);
        return;
    }
}
