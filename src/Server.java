import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Server {

    private ServerSocket serverSocket;

    private ArrayList<ClientHandler> clients;
    private ExecutorService pool;
    private ArrayList<Integer> zList;

    private long sum;


    public Server(int port, int X, int Z) {
        // Set values
        int chunksSize = (int)Math.ceil(((double)Z / X));
        System.out.println("chunksSize = " + chunksSize);
        this.zList = new ArrayList<>();

        // Fill zList with values between 1 to Z (in increasing order)
        for (int i = 1; i <= Z; i++) this.zList.add(i);
        System.out.println("Z = " + this.zList);

        this.sum = 0;

        clients = new ArrayList<>();
        pool = Executors.newFixedThreadPool(X);


        try {
            // Open connection
            serverSocket = new ServerSocket(port);
            System.out.println("Server started \nWaiting for a client...");

            // Count the connected clients
            int counter = 0;

            int fromIndex = 0;
            int toIndex = chunksSize;
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println();
                System.out.println("Client accepted");

                // create a sub list for the client to sum
                ArrayList<Integer> subList = new ArrayList<>(this.zList.subList(fromIndex, toIndex));
                ClientHandler clientThread = new ClientHandler(client, subList);
                clients.add(clientThread);
                pool.execute(clientThread);

                fromIndex = toIndex;
                toIndex += chunksSize;
                if (toIndex > zList.size()) {
                    toIndex = zList.size();
                }

                counter++;

                // If all expected clients are connected
                if (counter == X) {
                    pool.shutdown();

                    // Wait until all threads are finish
                    while (!pool.isTerminated()) ;

                    // Sum all the clients sub-sum.
                    for (ClientHandler clientHandler : clients) {
                        this.sum += clientHandler.getPartialSum();
                    }
                    break;

                }
            }
            System.out.println("\nTotal-sum = " + sum);

            serverSocket.close();

        } catch (Exception e) {
            System.out.println(e);
        }


    }


    public static void main(String[] args) {
        int port = 5000;

        int x_DefaultValue = 10;
        int z_DefaultValue = 100;

        int X = x_DefaultValue;  // Number of clients
        int Z = z_DefaultValue; // Size of the list we want to sum

        if (args.length == 2) {
            try {
                X = Integer.parseInt(args[0]);
                if (X <= 0) {
                    X = x_DefaultValue;
                    System.err.println("** X must be greater then 0. Default value now used **");
                }

                Z = Integer.parseInt(args[1]);
                if (Z <= 0) {
                    Z = z_DefaultValue;
                    System.err.println("** Z must be greater then 0. Default value now used **");
                }

            } catch (Exception e) {
                e.printStackTrace();
                X = x_DefaultValue;  // Number of clients
                Z = z_DefaultValue; // Size of the list we want to sum
            }
        }
        System.out.println("Number of clients: X = " + X);
        System.out.println("Size of the list we want to sum: Z = " + Z);
        System.out.println();

        Server server = new Server(port, X, Z);


    }


}
