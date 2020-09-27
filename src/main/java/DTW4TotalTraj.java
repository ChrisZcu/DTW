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


public class DTW4TotalTraj extends PApplet {
//    private UnfoldingMap map;
//    private final int ZOOMLEVEL = 20;
//    private final Location PRESENT_CENTER = new Location(new Location(41.14, -8.639));

    private static String path = "data/";
    private static Trajectory[] trajFull;

//    @Override
//    public void settings() {
//        size(1000, 800, P2D);
//    }
//
//    @Override
//    public void setup() {
//        String WHITE_MAP_PATH = "https://api.mapbox.com/styles/v1/pacemaker-yc/ck4gqnid305z61cp1dtvmqh5y/tiles/512/{z}/{x}/{y}@2x?access_token=pk.eyJ1IjoicGFjZW1ha2VyLXljIiwiYSI6ImNrNGdxazl1aTBsNDAzZW41MDhldmQyancifQ.WPAckWszPCEHWlyNmJfY0A";
//
//        map = new UnfoldingMap(this, new MapBox.CustomMapBoxProvider(WHITE_MAP_PATH));
//        map.setZoomRange(1, 20);
//        map.setBackgroundColor(255);
//        MapUtils.createDefaultEventDispatcher(this, map);
//        map.zoomAndPanTo(ZOOMLEVEL, PRESENT_CENTER);
//
//        loadData();
//
//        calDTWMatrix();
//    }

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

    private static void loadData() {
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
/*
            StringBuilder str = new StringBuilder();
            int trajId = 0;
            for (String traj : trajStr) {
                DTW.printMsg(i, 0);
                String[] data = traj.split(";")[1].split(",");
                str.append(trajId++).append(";");
                for (int j = 0; j < data.length - 2; j += 2) {
                    Location loc = new Location(Float.parseFloat(data[j + 1]), Float.parseFloat(data[j]));
                    ScreenPosition src = map.getScreenPosition(loc);
                    str.append(src.x).append(",").append(src.y).append(",");
                }
                str.replace(str.length() - 1, str.length(), "\n");
            }
            System.out.println("writing......");
            BufferedWriter writer = new BufferedWriter(new FileWriter("data/GPS.txt"));
            writer.write(str.toString());
            System.out.println("write done");
            */

            trajFull = new Trajectory[trajStr.size()];
            for (String traj : trajStr) {
                DTW.printMsg(i, 0);
                String[] data = traj.split(";")[1].split(",");
                Vec2[] trajData = new Vec2[data.length / 2 - 1];
                for (int j = 0; j < data.length - 2; j += 2) {
//                    Location loc = new Location(Float.parseFloat(data[j + 1]), Float.parseFloat(data[j]));
//                    ScreenPosition src = map.getScreenPosition(loc);
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

    static class Trajectory {
        Vec2[] scr;

        public Trajectory(Vec2[] scr) {
            this.scr = scr;
        }
    }
}
