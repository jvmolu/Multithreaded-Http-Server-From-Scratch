import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

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

      // SEND HTTP/1.1 200 OK\r\n\r\n
      clientSocket.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
      clientSocket.close();

    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
