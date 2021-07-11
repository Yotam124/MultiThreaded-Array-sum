import java.util.ArrayList;
import java.util.List;

public class ParallelSum implements Runnable {

    private long sum;
    private ArrayList<Integer> numList;

    public ParallelSum(ArrayList<Integer> numList) {
        this.numList = numList;
        this.sum = 0;
    }

    public long getSum() {
        return this.sum;
    }

    public ArrayList<Integer> getNumList() {
        return this.numList;
    }

    @Override
    public void run() {
        numList.forEach(integer -> sum += integer);
    }


}
