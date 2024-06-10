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
