import cn.siat.vcc.util.math.Vec2;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;

public class FrechetDis4Total {
    private static String path = "data/";
    private static Trajectory[] trajFull;

    private static void calFrechetDisMatrix() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("cal-pool-%d").build();

        ExecutorService threadPool = new ThreadPoolExecutor(4, 4,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        int totLen = trajFull.length;
        int segLen = totLen / 4;
        System.out.println(totLen + ", " + segLen);

        for (int i = 0; i < 4; i++) {
            FrechetDisThread ft = new FrechetDisThread(trajFull, i * segLen, (i + 1) * segLen, path);
            threadPool.submit(ft);
        }

        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ALL Done");
    }


    private static String filePath = "data/data_100_ran.txt";
    private static String filePath5W = "E:\\zcz\\dbgroup\\DemoSystem\\data\\GPS\\Porto5w\\Porto5w.txt";
    private static String fullFilePath = "E:\\zcz\\dbgroup\\DemoSystem\\data\\GPS\\porto_full.txt";

    private static String max100File = "data/data_100.txt";
    private static String max1000File = "data/data_1000.txt";
    private static String max10000File = "data/data_10000.txt";

    private static String dataFilePath = max100File;

    public static void loadData() {
        try {
            ArrayList<String> trajStr = new ArrayList<>(2400000);
            BufferedReader reader = new BufferedReader(new FileReader(dataFilePath));
            String line;
            while ((line = reader.readLine()) != null) {
                trajStr.add(line);
            }
            reader.close();
            System.out.println("load done");
            int i = 0;

            trajFull = new Trajectory[trajStr.size()];
            for (String traj : trajStr) {
                DTW.printMsg(i, 0);
                String[] data = traj.split(";")[1].split(",");
                Point[] trajData = new Point[data.length / 2 - 1];
                for (int j = 0; j < data.length - 2; j += 2) {
                    trajData[j / 2] = new Point(Float.parseFloat(data[j + 1]), Float.parseFloat(data[j]));
                }
                trajFull[i++] = new Trajectory(trajData);
            }
            trajStr.clear();
            System.out.println("load done");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        if (args.length > 0) {
            dataFilePath = args[0];
            path = args[1];
        }

        loadData();
        calFrechetDisMatrix();
    }

}
