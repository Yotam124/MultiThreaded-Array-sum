import java.util.ArrayList;
import java.util.List;

public class ParallelSum implements Runnable {

    private int sum;
    private ArrayList<Integer> numList;

    public ParallelSum(ArrayList<Integer> numList) {
        this.numList = numList;
        this.sum = 0;
    }

    public int getSum() {
        return this.sum;
    }

    public ArrayList<Integer> getNumList() {
        return this.numList;
    }

    @Override
    public void run() {

        numList.forEach(integer -> sum += integer);
//        int a = 0;
//        for (int i=0 ; i<100000000 / numList.get(1)  ; i++) {
//            a += i;
//        }
        System.out.println("parallel array: " + numList.toString());
    }


}
