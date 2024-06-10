import java.io.File;
import java.nio.file.Files;

public class RequestProcessor {
    
    public static String handleRequest(String reqString) {
        return handleRequest(reqString, "default");
    }

    public static String handleRequest(String reqString, String filesDirectory) {
        
        System.out.println("Recieved request: " + reqString);

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

        // USER AGENT ROUTE
        // SAMPLE REQUEST:
        // GET /user-agent HTTP/1.1\r\nHost: localhost:4221\r\nUser-Agent: curl/7.64.1\r\nAccept: */*\r\n\r\n
        // SAMPLE RESPONSE:
        // HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: 14\r\n\r\ncurl/7.64.1
        if(request.getPath().equalsIgnoreCase("/user-agent")) {
            String userAgent = request.getHeaders().get("User-Agent");
            CCHttpResponse response = new CCHttpResponse();
            response.setProtocol("HTTP/1.1");
            response.setStatus(HttpStatus.OK);
            response.setHeader("Content-Type", "text/plain");
            response.setHeader("Content-Length", String.valueOf(userAgent.length()));
            response.setBody(userAgent);
            return response.toString();
        }

        // /files/{filename}
        // SAMPLE REQUEST:
        // GET /files/index.html HTTP/1.1\r\nHost: localhost:4221\r\nUser-Agent: curl/7.64.1\r\nAccept: */*\r\n\r\n
        // SAMPLE RESPONSE:
        // HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: 14\r\n\r\n<html>content</html>
        if(request.getPath().startsWith("/files/") && request.getMethod().equalsIgnoreCase("GET")) {
            String fileName = request.getPath().substring(7);
            if (fileName.contains("..")) {
                return "HTTP/1.1 403 Forbidden\r\n\r\n";
            }
            try {
                File file = new File(filesDirectory + "/" + fileName);
                if (!file.exists() || !file.isFile()) {
                    return "HTTP/1.1 404 Not Found\r\n\r\n";
                }
                String fileContent = new String(Files.readAllBytes(file.toPath()));
                CCHttpResponse response = new CCHttpResponse();
                response.setProtocol("HTTP/1.1");
                response.setStatus(HttpStatus.OK);
                response.setHeader("Content-Type", "application/octet-stream");
                response.setHeader("Content-Length", String.valueOf(fileContent.length()));
                response.setBody(fileContent);
                return response.toString();
            } catch (Exception e) {
                return "HTTP/1.1 500 Internal Server Error\r\n\r\n";
            }
        }


        // POST /files/{filename}
        // SAMPLE REQUEST:
        // POST /files/file_123 HTTP/1.1\r\nHost: localhost:4221\r\nUser-Agent: curl/7.64.1\r\nAccept: */*\r\nContent-Type: application/octet-stream\r\nContent-Length: 5\r\n\r\n12345
        // SAMPLE RESPONSE:
        // HTTP/1.1 201 Created\r\n\r\n
        if(request.getPath().startsWith("/files/") && request.getMethod().equalsIgnoreCase("POST")) {
            String fileName = request.getPath().substring(7);
            if (fileName.contains("..")) {
                return "HTTP/1.1 403 Forbidden\r\n\r\n";
            }
            try {
                File file = new File(filesDirectory + "/" + fileName);
                if (file.exists()) {
                    return "HTTP/1.1 409 Conflict\r\n\r\n";
                }
                Files.write(file.toPath(), request.getBody().getBytes());
                CCHttpResponse response = new CCHttpResponse();
                response.setProtocol("HTTP/1.1");
                response.setStatus(HttpStatus.CREATED);
                return response.toString();
            } catch (Exception e) {
                return "HTTP/1.1 500 Internal Server Error\r\n\r\n";
            }
        }        
        
        return "HTTP/1.1 404 Not Found\r\n\r\n";
    }
}
