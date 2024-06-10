import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

enum RequestParts {
  METHOD,
  PATH,
  PROTOCOL,
  HEADERS,
  BODY
}

public class Main {

  public static HashMap<String, String> parseRequest(String request) {

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

  public static String readClientMessage(Socket clientSocket, Integer bytes) throws IOException {
    byte[] buffer = new byte[bytes];
    int bytesRead = clientSocket.getInputStream().read(buffer);
    return new String(buffer, 0, bytesRead);
  }

  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    
    try {
      serverSocket = new ServerSocket(4221);
      // Since the tester restarts your program quite often, setting SO_REUSEADDR
      // ensures that we don't run into 'Address already in use' errors
      serverSocket.setReuseAddress(true);
      clientSocket = serverSocket.accept(); // Wait for connection from client.
      System.out.println("accepted new connection");

      // Get Message from client
      String clientMessage = readClientMessage(clientSocket, 1024);      
      System.out.println("Message from client: " + clientMessage);
      
      // Parse the request
      HashMap<String, String> requestMap = parseRequest(clientMessage);
      System.out.println("Request Map: " + requestMap);

      if(requestMap.get(RequestParts.PATH.name()).equalsIgnoreCase("/")) {
        // SEND HTTP/1.1 200 OK\r\n\r\n
        clientSocket.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
        clientSocket.close();
        return;
      }

      // SEND HTTP/1.1 404 NOT FOUND\r\n\r\n
      clientSocket.getOutputStream().write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
      clientSocket.close();

    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
