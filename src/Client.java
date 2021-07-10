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

    public Client(String ipAddress, int port, int Y, String name) {
        this.Y = Y;
        this.name = name;

        try {
            // Open connection
            socket = new Socket(ipAddress, port);
            System.out.println(name + " Connected");

            out = new PrintWriter(socket.getOutputStream(), true);
            inputStream = new ObjectInputStream(socket.getInputStream());


            // Communicate;
            ArrayList<Integer> list = (ArrayList<Integer>) inputStream.readObject();
            out.println(name + ": " + list);


            int chunksSize = list.size() / Y;

            ExecutorService executor = Executors.newFixedThreadPool(Y);

            int fromIndex = 0;
            int toIndex = chunksSize;
            ArrayList<ParallelSum> parallelSumList = new ArrayList<>();
            for (int i = 0; i < Y; i++) {
                ArrayList<Integer> subList = new ArrayList<>(list.subList(fromIndex, toIndex));
                ParallelSum parallelSum = new ParallelSum(subList);
                parallelSumList.add(parallelSum);
                executor.execute(parallelSum);

                fromIndex = toIndex;
                toIndex += chunksSize;
            }

            executor.shutdown();

            // Wait until all threads are finish
            while (!executor.isTerminated());

            int core_i = 1;
            int sum = 0;
            for (ParallelSum p : parallelSumList) {
                System.out.println("core_" + core_i + ": sum= " + p.getSum() + ", subList= " + p.getNumList());
                sum += p.getSum();
                core_i++;
            }
            System.out.println("Core's-sum = " + sum + "\n");

            out.println("subSum= " + sum);

//            socket.close();
//            out.close();


        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        int[] a = {1, 2, 3, 4};

        ArrayList<Client> clients = new ArrayList<>();
        int x = 5;
        for (int i = 1; i <= x; i++) {
            clients.add(new Client("127.0.0.1", 5000, 2, "Client-" + i));
        }
    }
}
