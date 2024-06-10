import java.io.IOException;

public class Main {

  public static void main(String[] args) {

    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    // --directory flag
    String filesDirectory = null;
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--directory")) {
        filesDirectory = args[i + 1];
      }
    }
    
    try {
      MultithreadedWebServer server = new MultithreadedWebServer(4221, 10, filesDirectory);
      server.start();
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    } catch (Exception e) {
      System.out.println("Unhandled Exception: " + e.getMessage());
    }
  }
}
