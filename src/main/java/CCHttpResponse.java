import java.util.HashMap;

public class CCHttpResponse {
    
    private HttpStatus status;
    private String protocol;
    private HashMap<String, String> headers;
    private String body;
    
    public CCHttpResponse() {
        this.status = HttpStatus.OK;
        this.protocol = "";
        this.headers = new HashMap<String, String>();
        this.body = "";
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

    public void setBody(String body) {
        this.body = body;
    }

    public static class Builder {
        
        private HttpStatus status;
        private String protocol;
        private HashMap<String, String> headers;
        private String body;

        public Builder() {
            this.status = HttpStatus.OK;
            this.protocol = "";
            this.headers = new HashMap<String, String>();
            this.body = "";
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

        public Builder body(String body) {
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
            if (encoding != null && ContentEncoding.isSupported(encoding)) {
                // Not Encoding the body for now
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
        response += "\r\n" + body;
        return response;
    }
}
