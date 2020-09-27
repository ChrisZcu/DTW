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

    private static void calDTWMatrix() {
        ExecutorService threadPool = new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());

        int totLen = trajFull.length;
        int segLen = totLen / 8;
        System.out.println(totLen + ", " + segLen);
        for (int i = 0; i < 8; i++) {
            DTWCalThread st = new DTWCalThread(trajFull, i * segLen, (i + 1) * segLen, path);
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
    private static String dataFilePath = fullFilePath;

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
                Vec2[] trajData = new Vec2[data.length / 2 - 1];
                for (int j = 0; j < data.length - 2; j += 2) {
                    trajData[j / 2] = new Vec2(Float.parseFloat(data[j + 1]), Float.parseFloat(data[j]));
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
//        PApplet.main(new String[]{DTW4TotalTraj.class.getName()});
        if (args.length > 0) {
            dataFilePath = args[0];
            path = args[1];
        }
        loadData();

        calDTWMatrix();
    }

}
