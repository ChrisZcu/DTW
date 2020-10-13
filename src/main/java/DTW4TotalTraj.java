
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class DTW4TotalTraj {

    private static String path = "data/dtw_cd_20w/";
    private static Trajectory[] trajFull;
    private static int offSet = 0;


    private static void calDTWMatrix() {
        ExecutorService threadPool = new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());

        int[] beginAry = new int[8];
        int segLen = 1_0000;

        for (int i = 0; i < 8; i++) {
            beginAry[i] = offSet + i * segLen;
        }

        System.out.println("per len: " + segLen);
        for (int i = 0; i < 8; i++) {
            int lower = i * segLen + offSet;
            int upper = Math.min((i + 1) * segLen + offSet, trajFull.length);
            System.out.printf("thread %d, [%d, %d)%n", i, lower, upper);
            DTWCalThread st = new DTWCalThread(trajFull, lower, upper, path, offSet, beginAry[i]);
            threadPool.submit(st);
        }

        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.err.println(e);
        }
        System.out.println("ALL Done");
    }

    private static String filePath = "data/data_100.txt";
    private static String filePath5W = "E:\\zcz\\dbgroup\\DemoSystem\\data\\GPS\\Porto5w\\Porto5w.txt";
    private static String fullFilePath = "data/porto_full.txt";
    private static String fullSrceenData = "data/screen_point_zoom17.txt";
    private static String max100File = "data/data_100.txt";
    private static String max1000File = "data/data_1000.txt";
    private static String max10000File = "data/data_10000.txt";

    private static final String szPart = "C:\\Users\\Administrator\\Desktop\\zhengxin\\vfgs\\sz_score.txt";
    private static final String cdPart = "C:\\Users\\Administrator\\Desktop\\zhengxin\\vfgs\\cd_new_score.txt";

    private static String dataFilePath = cdPart;
    private static int LIMIT = 20_0000;

    public static void loadData() {
        try {
            ArrayList<String> trajStr = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(dataFilePath));
            String line;
            int cnt = 0;
            while (cnt < LIMIT && (line = reader.readLine()) != null) {
                trajStr.add(line);
                cnt ++;
            }
            reader.close();
            System.out.println(trajStr.size());
            System.out.println("load done");
            int i = 0;

            trajFull = new Trajectory[trajStr.size()];
            for (String traj : trajStr) {
                String[] data = traj.split(";")[1].split(",");
                Point[] trajData = new Point[data.length / 2];
                for (int j = 0; j < data.length - 1; j += 2) {
                    trajData[j / 2] = new Point(Double.parseDouble(data[j]), Double.parseDouble(data[j + 1]));
                }
                trajFull[i++] = new Trajectory(trajData);
            }
            trajStr.clear();
            System.out.println("process done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            dataFilePath = args[0];
            path = args[1];
            offSet = Integer.parseInt(args[2]);
        }
        loadData();
        long t0 = System.currentTimeMillis();
        calDTWMatrix();
        System.out.println("time: " + (System.currentTimeMillis() - t0));
    }
}
