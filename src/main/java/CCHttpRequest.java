import java.util.HashMap;

public class CCHttpRequest {

    private String method;
    private String path;
    private String protocol;
    private HashMap<String, String> headers;
    private String body;
    
    private static HashMap<String, String> parseRequestString(String request) {

        HashMap<String, String> requestMap = new HashMap<String, String>();
        // SAMPLE REQUEST:
        // GET /index.html HTTP/1.1\r\nHost: localhost:4221\r\nUser-Agent: curl/7.64.1\r\nAccept: */*\r\n\r\n
        String[] requestParts = request.split("\r\n\r\n");
        String[] requestLine = requestParts[0].split("\r\n");
        String[] requestLineParts = requestLine[0].split(" "); 

        requestMap.put(RequestParts.METHOD.name(), requestLineParts[0]);
        requestMap.put(RequestParts.PATH.name(), requestLineParts[1]);
        requestMap.put(RequestParts.PROTOCOL.name(), requestLineParts[2]);

        // Headers
        String headers = "";
        for (int i = 1; i < requestLine.length; i++) {
            headers += requestLine[i] + "\r\n";
        }

        requestMap.put(RequestParts.HEADERS.name(), headers);

        // Body
        if (requestParts.length > 1) {
            requestMap.put(RequestParts.BODY.name(), requestParts[1]);
        }

        return requestMap;
    }

    private HashMap<String, String> parseHeaders(String headers) {
        String[] headerLines = headers.split("\r\n");
        HashMap<String, String> headersMap = new HashMap<String, String>();
        for (String headerLine : headerLines) {
            String[] headerParts = headerLine.split(":");
            headersMap.put(headerParts[0].trim(), headerParts[1].trim());
        }
        return headersMap;
    }

    public CCHttpRequest(String reqString) {
        HashMap<String, String> requestMap = parseRequestString(reqString);
        this.method = requestMap.get(RequestParts.METHOD.name());
        this.path = requestMap.get(RequestParts.PATH.name());
        this.protocol = requestMap.get(RequestParts.PROTOCOL.name());
        this.headers = parseHeaders(requestMap.get(RequestParts.HEADERS.name()));
        this.body = requestMap.get(RequestParts.BODY.name());
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    @Override
    public String toString() {
        return "Method: " + method + "\nPath: " + path + "\nProtocol: " + protocol + "\nHeaders: " + headers + "\nBody: " + body;
    }
}
