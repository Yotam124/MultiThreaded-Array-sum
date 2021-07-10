import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private ObjectOutputStream outputStream;

    private List<Integer> subZ;

    private int partialSum;

    public ClientHandler(Socket clientSocket, List<Integer> subZ) throws IOException {
        this.client = clientSocket;
        this.subZ = subZ;

        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);

        outputStream = new ObjectOutputStream(client.getOutputStream());

    }

    public int getPartialSum() {
        return this.partialSum;
    }


    @Override
    public void run() {
        try {
            outputStream.writeObject(subZ);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            while (true) {
                String request = in.readLine();
                System.out.println(request);
                if (request.contains("subSum=")) {
                    partialSum = Integer.parseInt(request.split("=")[1].trim());
                    break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
