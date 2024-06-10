import java.io.IOException;

public class Main {

  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");
    
    try {
      MultithreadedWebServer server = new MultithreadedWebServer(4221, 1);
      server.start();
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    } catch (Exception e) {
      System.out.println("Unhandled Exception: " + e.getMessage());
    }
  }
}
