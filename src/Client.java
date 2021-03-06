import java.net.*;
import java.io.*;
import java.util.ArrayList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Client {


    private Socket socket;
    private PrintWriter out;
    ObjectInputStream inputStream;

    int Y;
    String name;

    public Client(String ipAddress, int port, String name, int Y) {
        this.Y = Y;
        this.name = name;

        try {
            // Open connection
            socket = new Socket(ipAddress, port);
            System.out.println(name + " Connected");

            System.out.println("Number of CPU cores: " + this.Y);

            out = new PrintWriter(socket.getOutputStream(), true);
            inputStream = new ObjectInputStream(socket.getInputStream());


            // Communicate;
            @SuppressWarnings("unchecked")
            ArrayList<Integer> list = (ArrayList<Integer>) inputStream.readObject();
            out.println(name + ": " + list);

            int chunksSize = (int) Math.ceil(((double) list.size() / this.Y));

            ExecutorService executor = Executors.newFixedThreadPool(this.Y);

            int fromIndex = 0;
            int toIndex = chunksSize;
            ArrayList<ParallelSum> parallelSumList = new ArrayList<>();

            // Parallel sum of the list, divided between Y cores
            for (int i = 0; i < this.Y; i++) {
                ArrayList<Integer> subList = new ArrayList<>(list.subList(fromIndex, toIndex));
                ParallelSum parallelSum = new ParallelSum(subList);
                parallelSumList.add(parallelSum);
                executor.execute(parallelSum);

                fromIndex = toIndex;
                toIndex += chunksSize;
                if (toIndex > list.size()) {
                    toIndex = list.size();
                }
            }

            executor.shutdown();

            // Wait until all threads are finish
            while (!executor.isTerminated()) ;

            int core_i = 1;
            long sum = 0;
            for (ParallelSum p : parallelSumList) {
                System.out.println("core_" + core_i + ": sum= " + p.getSum() + ", subList= " + p.getNumList());
                sum += p.getSum();
                core_i++;
            }
            System.out.println("Core's-sum = " + sum + "\n");

            // Send the sum to the Server
            out.println("subSum= " + sum);


            socket.close();
            out.close();
            inputStream.close();


        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        String ipAddress = "127.0.0.1";
        int port = 5000;
        int numberOfCores = Runtime.getRuntime().availableProcessors();

        int x_DefaultValue = 10;
        int y_DefaultValue = Math.max((numberOfCores / x_DefaultValue), 2);


        int X = x_DefaultValue;   // Number of clients
        int Y = y_DefaultValue;  // Number of cores for each client
        if (args.length == 2) { // Getting X and Y
            try {
                X = Integer.parseInt(args[0]);
                if (X <= 0) {
                    X = x_DefaultValue;
                    System.err.println("** X must be greater then 0. Default value now used **");
                }
                Y = Integer.parseInt(args[1]);
                if (Y <= 0) {
                    Y = y_DefaultValue;
                    System.err.println("** Y must be greater then 0. Default value now used **");
                }
            } catch (Exception e) {
                e.printStackTrace();
                X = x_DefaultValue;
                Y = y_DefaultValue;
            }
        } else if (args.length == 1) { // Getting only X
            try {
                X = Integer.parseInt(args[0]);
            } catch (Exception e) {
                e.printStackTrace();
                X = x_DefaultValue;
            }
        }


        ArrayList<Client> clients = new ArrayList<>();
        for (int i = 1; i <= X; i++) {
            clients.add(new Client(ipAddress, port, "Client-" + i, Y));
        }
    }
}
