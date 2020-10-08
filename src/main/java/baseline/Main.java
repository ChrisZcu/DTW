package baseline;

import model.Trajectory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Main {
    // param for full
    public static final String DATA_PATH = "";
    public static final int LIMIT = -1;

    // param for subpart
    // if END == 0, means processing to the last one in full list.
    public static final int BEGIN = 0;
    public static final int END = 0;       // exclude
    public static final int SIZE = 3_0000; // the size of the heap

    public static final String RES_PATH = "";

    private static Trajectory[] trajFull;
    private static Trajectory[] trajPart;
    private static MaxHeap heap;

    public static void main(String[] args) {
        init();
        runAlgo();
        saveResToFile();
    }

    private static void init() {
        List<String> lineList = Util.readAllLines(DATA_PATH, LIMIT);
        trajFull = Util.lineStrToTraj(lineList);

        DTW.setTrajFull(trajFull);      // must call it before run

        trajPart = generateTrajPart();

        if (trajPart.length <= SIZE) {
            // no need to pick top-k
            System.out.println("No need to pick.");
            Util.saveTrajListToFile(RES_PATH, trajPart);
            System.exit(0);
        }

        System.out.println("Begin init max heap");
        heap = new MaxHeap(trajPart, SIZE);
        System.out.println("Heap init done");
    }

    /**
     * Create a sublist according to begin and end (exclude).
     * This list will be sorted by poiList size.
     */
    private static Trajectory[] generateTrajPart() {
        int tmpEnd = (END == 0) ? trajFull.length : END;

        System.out.printf("Generate trajPart = trajFull[%d, %d]%n", BEGIN, tmpEnd);

        Trajectory[] trajPart = Arrays.copyOfRange(trajFull, BEGIN, tmpEnd);
        Arrays.sort(trajPart, Comparator.comparing(traj -> traj.getPoiList().length));

        return trajPart;
    }

    private static void saveResToFile() {
        Trajectory[] resList = heap.getTrajArr();
        Util.saveTrajListToFile(RES_PATH, resList);
    }

    private static void runAlgo() {
        int lastIdx = trajPart.length - 1;
        for (int curIdx = SIZE; curIdx <= lastIdx; curIdx++) {
            Trajectory traj = trajPart[curIdx];
            double maxHeapTopScore = heap.getTopTraj().getScore();

            double score = DTW.getDisSum(traj, maxHeapTopScore);

            if (score >= maxHeapTopScore) {
                // don't to update heap
                continue;
            }

            heap.replaceTop(traj);
        }
    }
}
