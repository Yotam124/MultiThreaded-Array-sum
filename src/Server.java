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

    private int sum;


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

            int counter = 0;

            int fromIndex = 0;
            int toIndex = chunksSize;
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println();
                System.out.println("Client accepted");


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

                if (counter == X) {
                    pool.shutdown();

                    // Wait until all threads are finish
                    while (!pool.isTerminated()) ;

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
        int X = 10;  // Number of clients
        int Z = 100; // Size of the list we want to sum

        if (args.length == 3) {
            try {
                X = Integer.parseInt(args[0]);
                Z = Integer.parseInt(args[1]);

            } catch (Exception e) {
                e.printStackTrace();
                X = 10;  // Number of clients
                Z = 40; // Size of the list we want to sum
            }
        }
        System.out.println("Number of clients: X = " + X);
        System.out.println("Size of the list we want to sum: Z = " + Z);
        System.out.println();

        Server server = new Server(5000, X, Z);


    }


}
