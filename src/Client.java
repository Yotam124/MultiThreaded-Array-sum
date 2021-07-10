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
        this.Y = (Y < 1) ? Runtime.getRuntime().availableProcessors() : Y;
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


//            int chunksSize = list.size() / this.Y;
            int chunksSize = (int)Math.ceil(((double)list.size() / this.Y));

            ExecutorService executor = Executors.newFixedThreadPool(this.Y);

            int fromIndex = 0;
            int toIndex = chunksSize;
            ArrayList<ParallelSum> parallelSumList = new ArrayList<>();
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
            int sum = 0;
            for (ParallelSum p : parallelSumList) {
                System.out.println("core_" + core_i + ": sum= " + p.getSum() + ", subList= " + p.getNumList());
                sum += p.getSum();
                core_i++;
            }
            System.out.println("Core's-sum = " + sum + "\n");

            out.println("subSum= " + sum);


            socket.close();
            out.close();
            inputStream.close();


        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        int x_DefaultValue = 10;
        int y_DefaultValue = -1;


        int X = x_DefaultValue;   // Number of clients
        int Y = y_DefaultValue;  // Number of cores for each client
        if (args.length == 2) { // Getting X and Y
            try {
                X = Integer.parseInt(args[0]);
                Y = Integer.parseInt(args[1]);
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
            clients.add(new Client("127.0.0.1", 5000, "Client-" + i, Y));
        }
    }
}
