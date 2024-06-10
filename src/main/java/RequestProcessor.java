public class RequestProcessor {
    
    public static String handleRequest(String reqString) {
        
        CCHttpRequest request = new CCHttpRequest(reqString);
        
        // PING ROUTE - HEALTH CHECK
        if(request.getPath().equalsIgnoreCase("/")) {
            CCHttpResponse response = new CCHttpResponse();
            response.setProtocol("HTTP/1.1");            
            response.setStatus(HttpStatus.OK);
            return response.toString();
        }

        // ECHO ROUTE /echo/{str}
        // SAMPLE REQUEST:
        // GET /echo/abc HTTP/1.1\r\nHost: localhost:4221\r\nUser-Agent: curl/7.64.1\r\nAccept: */*\r\n\r\n
        // SAMPLE RESPONSE:
        // HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: 3\r\n\r\nabc
        if(request.getPath().startsWith("/echo/")) {
            String echoString = request.getPath().substring(6);
            CCHttpResponse response = new CCHttpResponse();
            response.setProtocol("HTTP/1.1");
            response.setStatus(HttpStatus.OK);
            response.setHeader("Content-Type", "text/plain");
            response.setHeader("Content-Length", String.valueOf(echoString.length()));
            response.setBody(echoString);
            return response.toString();
        }
        
        return "HTTP/1.1 404 Not Found\r\n\r\n";
    }
}
