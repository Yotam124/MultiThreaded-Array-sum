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

    public Server(int port, int X, int Z, int Y) {
        // Set values
        int chunksSize = Z / X;
        this.zList = new ArrayList<>();
        for (int i = 1; i <= Z; i++) this.zList.add(i);
        this.sum = 0;

        System.out.println("Z = " + zList);


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


                ArrayList<Integer> subList = new ArrayList<>(zList.subList(fromIndex, toIndex));
                ClientHandler clientThread = new ClientHandler(client, subList);
                clients.add(clientThread);
                pool.execute(clientThread);

                fromIndex = toIndex;
                toIndex += chunksSize;

                counter++;

                if (counter == X) {
                    pool.shutdown();

                    // Wait until all threads are finish
                    while (!pool.isTerminated());

                    for (ClientHandler clientHandler : clients) {
                        this.sum += clientHandler.getPartialSum();
                    }
                    break;

                }


//                for (ClientHandler clientHandler : clients) {
//                    if (clientHandler.getPartialSum() != 0) {
//                        this.sum += clientHandler.getPartialSum();
//                        clients.remove(clientHandler);
//                    }
//                }
//                System.out.println("counter = " + counter);
//                System.out.println("clients.size() = " + clients.size());

//                if (counter == X) {
//                    // Close connection
//                    serverSocket.close();
//
//                    break;
//                }


            }
            System.out.println("\nTotal-sum = " + sum);


        } catch (Exception e) {
            System.out.println(e);
        }


    }


    public static void main(String[] args) {

        Server server = new Server(5000, 5, 20, 2);


    }


}
