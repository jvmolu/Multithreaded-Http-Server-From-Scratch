import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultithreadedWebServer {

    private Socket clientSocket;
    private ServerSocket serverSocket;
    private ExecutorService executorService;

    public String readClientMessage(Socket clientSocket, int bytes) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        char[] buffer = new char[bytes];
        in.read(buffer, 0, bytes);
        return new String(buffer);        
    }

    public MultithreadedWebServer(int port, int num_threads) throws IOException {
        this.clientSocket = null;
        this.serverSocket = new ServerSocket(port);
        this.serverSocket.setReuseAddress(true);
        this.executorService = Executors.newFixedThreadPool(num_threads);
    }

    public void handleConnection(Socket clientSocket) {
        try {
            System.out.println("handling connection");
            String clientMessage = readClientMessage(clientSocket, 1024);
            String outputString = RequestProcessor.handleRequest(clientMessage);
            clientSocket.getOutputStream().write(outputString.getBytes());
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public void start() {
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                // Timeout
                clientSocket.setSoTimeout(9000);
                System.out.println("accepted new connection");
                executorService.submit(() -> handleConnection(clientSocket));
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }
    
}
