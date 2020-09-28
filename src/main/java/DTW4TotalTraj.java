import cn.siat.vcc.util.math.Vec2;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.MapBox;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import javafx.geometry.Pos;
import processing.core.PApplet;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class DTW4TotalTraj {

    private static String path = "data/";
    private static Trajectory[] trajFull;
    private static int offSet = 1000000;

    private static void calDTWMatrix() {
        ExecutorService threadPool = new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());

        int totLen = trajFull.length - offSet;
        int segLen = totLen / 8;
        System.out.println(totLen + ", " + segLen);
        for (int i = 0; i < 8; i++) {
            DTWCalThread st = new DTWCalThread(trajFull, i * segLen + offSet, (i + 1) * segLen + offSet, path, offSet);
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

    String filePath = "data/data_100.txt";
    private static String filePath5W = "E:\\zcz\\dbgroup\\DemoSystem\\data\\GPS\\Porto5w\\Porto5w.txt";
    private static String fullFilePath = "E:\\zcz\\dbgroup\\DemoSystem\\data\\GPS\\porto_full.txt";
    private static String fullSrceenData = "data/screen_point_zoom17.txt";
    private static String max100File = "data/data_100.txt";
    private static String max1000File = "data/data_1000.txt";
    private static String max10000File = "data/data_10000.txt";

    private static String dataFilePath = fullSrceenData;

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
//                DTW.printMsg(i, 0);
                String[] data = traj.split(";")[1].split(",");
                Vec2[] trajData = new Vec2[data.length / 2];
                for (int j = 0; j < data.length - 1; j += 2) {
                    trajData[j / 2] = new Vec2(Integer.parseInt(data[j]), Integer.parseInt(data[j + 1]));
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
//        PApplet.main(new String[]{DTW4TotalTraj.class.getName()});
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
