import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private ObjectOutputStream outputStream;

    private ArrayList<Integer> subZ;

    private long partialSum;

    public ClientHandler(Socket clientSocket, ArrayList<Integer> subZ) throws IOException {
        this.client = clientSocket;
        this.subZ = subZ;

        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);

        outputStream = new ObjectOutputStream(client.getOutputStream());

    }

    public long getPartialSum() {
        return this.partialSum;
    }


    @Override
    public void run() {
        try {
            // Send the sub-list to the client
            outputStream.writeObject(subZ);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Communicate with the client
            while (true) {
                String msg = in.readLine();
                System.out.println(msg);
                // If the client has finished summing the sub-list
                if (msg.contains("subSum=")) {
                    partialSum = Long.parseLong(msg.split("=")[1].trim());
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
