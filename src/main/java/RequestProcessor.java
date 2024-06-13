import java.io.File;
import java.nio.file.Files;

import enums.ContentEncoding;
import enums.Header;
import enums.HttpStatus;
import enums.Protocol;
import objects.CCHttpRequest;
import objects.CCHttpResponse;

public class RequestProcessor {
    
    public static String getPossibleEncoding(CCHttpRequest request) {
        if (request.getHeaders().containsKey(Header.ACCEPT_ENCODING.getHeader())) {
            for (String value : request.getHeaders().get(Header.ACCEPT_ENCODING.getHeader()).split(",")) {
                if (ContentEncoding.isSupported(value.trim())) {
                    return value.trim();
                }
            }
        }
        return null;
    }

    public static CCHttpResponse handleRequest(String reqString) {
        return handleRequest(reqString, "default");
    }

    public static CCHttpResponse handleRequest(String reqString, String filesDirectory) {
        
        System.out.println("Recieved request: " + reqString);

        CCHttpRequest request = new CCHttpRequest(reqString);
        CCHttpResponse.Builder responseBuilder = new CCHttpResponse.Builder();
        responseBuilder.protocol(Protocol.HTTP_1_1.getProtocol());
        responseBuilder.status(HttpStatus.NOT_FOUND);

        // ENCODING
        String encoding = getPossibleEncoding(request);
        if (encoding != null) {
            responseBuilder.header(Header.CONTENT_ENCODING.getHeader(), encoding);
        }
        
        // PING ROUTE - HEALTH CHECK
        if(request.getPath().equalsIgnoreCase("/")) {
            responseBuilder.status(HttpStatus.OK);
        }

        // ECHO ROUTE /echo/{str}
        // SAMPLE REQUEST:
        // GET /echo/abc HTTP/1.1\r\nHost: localhost:4221\r\nUser-Agent: curl/7.64.1\r\nAccept: */*\r\n\r\n
        // SAMPLE RESPONSE:
        // HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: 3\r\n\r\nabc
        else if(request.getPath().startsWith("/echo/")) {
            String echoString = request.getPath().substring(6);
            responseBuilder.status(HttpStatus.OK)
                .header(Header.CONTENT_TYPE.getHeader(), "text/plain")
                .header(Header.CONTENT_LENGTH.getHeader(), String.valueOf(echoString.length()))
                .body(echoString.getBytes());
        }

        // USER AGENT ROUTE
        // SAMPLE REQUEST:
        // GET /user-agent HTTP/1.1\r\nHost: localhost:4221\r\nUser-Agent: curl/7.64.1\r\nAccept: */*\r\n\r\n
        // SAMPLE RESPONSE:
        // HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: 14\r\n\r\ncurl/7.64.1
        else if(request.getPath().equalsIgnoreCase("/user-agent")) {
            String userAgent = request.getHeaders().get(Header.USER_AGENT.getHeader());
            responseBuilder.status(HttpStatus.OK)
                .header(Header.CONTENT_TYPE.getHeader(), "text/plain")
                .header(Header.CONTENT_LENGTH.getHeader(), String.valueOf(userAgent.length()))
                .body(userAgent.getBytes());
        }

        // /files/{filename}
        // SAMPLE REQUEST:
        // GET /files/index.html HTTP/1.1\r\nHost: localhost:4221\r\nUser-Agent: curl/7.64.1\r\nAccept: */*\r\n\r\n
        // SAMPLE RESPONSE:
        // HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: 14\r\n\r\n<html>content</html>
        else if(request.getPath().startsWith("/files/") && request.getMethod().equalsIgnoreCase("GET")) {
            String fileName = request.getPath().substring(7);
            if (fileName.contains("..")) {
                responseBuilder.status(HttpStatus.FORBIDDEN);
            }
            else {
                try {
                    File file = new File(filesDirectory + "/" + fileName);
                    if (!file.exists() || !file.isFile()) {
                        responseBuilder.status(HttpStatus.NOT_FOUND);
                    }
                    else {
                        String fileContent = new String(Files.readAllBytes(file.toPath()));
                        responseBuilder.status(HttpStatus.OK)
                            .header(Header.CONTENT_TYPE.getHeader(), "application/octet-stream")
                            .header(Header.CONTENT_LENGTH.getHeader(), String.valueOf(fileContent.length()))
                            .body(fileContent.getBytes());
                    }
                } catch (Exception e) {
                    responseBuilder.status(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }


        // POST /files/{filename}
        // SAMPLE REQUEST:
        // POST /files/file_123 HTTP/1.1\r\nHost: localhost:4221\r\nUser-Agent: curl/7.64.1\r\nAccept: */*\r\nContent-Type: application/octet-stream\r\nContent-Length: 5\r\n\r\n12345
        // SAMPLE RESPONSE:
        // HTTP/1.1 201 Created\r\n\r\n
        else if(request.getPath().startsWith("/files/") && request.getMethod().equalsIgnoreCase("POST")) {
            String fileName = request.getPath().substring(7);
            if (fileName.contains("..")) {
                responseBuilder.status(HttpStatus.FORBIDDEN);
            } else {
                try {
                    File file = new File(filesDirectory + "/" + fileName);
                    if (file.exists()) {
                        responseBuilder.status(HttpStatus.CONFLICT);
                    }
                    else {
                        Files.write(file.toPath(), request.getBody().getBytes());
                        responseBuilder.status(HttpStatus.CREATED);
                    }
                } catch (Exception e) {
                    responseBuilder.status(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
        
        return responseBuilder.build();
    }
}
