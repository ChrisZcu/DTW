
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.MapBox;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import org.lwjgl.Sys;
import processing.core.PApplet;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

public class DTW2 extends PApplet {
    private int totalSize = 1;

    private ArrayList<Point[]> trajFull = new ArrayList<>(totalSize);
    static UnfoldingMap map = null;

    @Override
    public void settings() {
        size(1000, 800, P2D);
    }

    @Override
    public void setup() {
        String WHITE_MAP_PATH = "https://api.mapbox.com/styles/v1/pacemaker-yc/ck4gqnid305z61cp1dtvmqh5y/tiles/512/{z}/{x}/{y}@2x?access_token=pk.eyJ1IjoicGFjZW1ha2VyLXljIiwiYSI6ImNrNGdxazl1aTBsNDAzZW41MDhldmQyancifQ.WPAckWszPCEHWlyNmJfY0A";

        map = new UnfoldingMap(this, new MapBox.CustomMapBoxProvider(WHITE_MAP_PATH));
        map.setZoomRange(1, 20);
        map.setBackgroundColor(255);
        MapUtils.createDefaultEventDispatcher(this, map);
        map.zoomAndPanTo(13, new Location(41.14, -8.639));

        calDTW();

//        map.zoomAndPanTo(12, new Location(41.14, -8.639));

    }

    @Override
    public void draw() {
        map.draw();
        int i = 0;
        for (Point[] traj : trajFull) {
            if (rmvTrajId.contains(i)) {
                drawSingleTraj(traj, false);
            } else {
                drawSingleTraj(traj, true);
            }
            i++;
        }
    }

    private void drawSingleTraj(Point[] traj, boolean color) {

        noFill();
        strokeWeight(1);
        if (!color)
            stroke(new Color(190, 46, 29).getRGB());
        else {
            stroke(new Color(235, 200, 68).getRGB());
        }
        beginShape();
        for (Point poi : traj) {
            vertex((float) poi.x, (float) poi.y);
        }
        endShape();
    }

    private HashSet<Integer> rmvTrajId = new HashSet<>(9);

    private void calDTW() {
//        loadRowData("data/data_" + totalSize + ".txt");
//        loadRowData("data/data_100.txt");
        loadRowData("data/portoDecreasing.txt");
        long t0 = System.currentTimeMillis();
        double[][] trajDisMatrix = new double[totalSize][trajFull.size()];
        long[] timeList = new long[trajFull.size()];
        int size = -1;
        for (int i = 0; i < totalSize; i++) {
            System.out.println("longest size: " + trajFull.get(i).length);

            for (int j = i + 1; j < trajFull.size(); j++) {
                if (trajFull.get(j).length == size) {
                    continue;
                }
                size = trajFull.get(j).length;
                long tmpTime0 = System.currentTimeMillis();
                trajDisMatrix[i][j] = calTrajPairDis(trajFull.get(i), trajFull.get(j));
                timeList[j] = (System.currentTimeMillis() - tmpTime0);
                System.out.println(trajFull.get(i).length + ", " + trajFull.get(j).length + ", time: " + timeList[j]);
//                break;
            }
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("data/timeRecord.txt"));
            writer.write(Arrays.toString(timeList));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("done");
        for (int i = 0; i < totalSize; i++) {
            for (int j = 0; j < i; j++) {
                trajDisMatrix[i][j] = trajDisMatrix[j][i];
            }
        }
//        System.out.println(Arrays.deepToString(trajDisMatrix));
        try {
            System.out.println("writing...");
            BufferedWriter writer = new BufferedWriter(new FileWriter("data/record.txt", true));
            writer.write("time for trajectories matrix: " + (System.currentTimeMillis() - t0) + " ms\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < totalSize * 0.9; i++) {
            double min = Float.MAX_VALUE;
            int id = -1;
            for (int j = 0; j < totalSize; j++) {
                if (rmvTrajId.contains(j)) {
                    continue;
                }
                double influ;
                influ = getSingleDisSum(trajDisMatrix[j]);
                if (influ < min) {
                    id = j;
                    min = influ;
                }
            }
            rmvTrajId.add(id);
        }
//        System.out.println(rmvTrajId);
        for (int i = 0; i < totalSize; i++) {
            long disTotal = 0;
            for (int j = 0; j < totalSize; j++) {
                disTotal += trajDisMatrix[i][j];
            }
//            System.out.println(i + ": " + disTotal);
        }
    }

    private float getSingleDisSum(double[] trajInflu) {
        float res = 0f;
        int i = 0;
        for (double influ : trajInflu) {
            if (!rmvTrajId.contains(i++))
                res += influ;
        }
        return res;
    }

    private void loadRowData(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] traj = line.split(";")[1].split(",");
                Point[] trajData = new Point[traj.length / 2 - 1];
                for (int j = 0; j < traj.length - 2; j += 2) {
//                    Location loc = new Location(Float.parseFloat(traj[j + 1]), Float.parseFloat(traj[j]));
//                    ScreenPosition src = map.getScreenPosition(loc);
                    trajData[j / 2] = new Point(Float.parseFloat(traj[j + 1]), Float.parseFloat(traj[j]));
                }
                trajFull.add(trajData);
            }
            System.out.println("Load Done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static double calTrajPairDis(Point[] path1, Point[] path2) {
//        Point[] traj1;
//        Point[] traj2;
//        if (path1.length >= path2.length) {
//            traj1 = path1;
//            traj2 = path2;
//        } else {
//            traj1 = path2;
//            traj2 = path1;
//        }
//        System.out.println(traj1.length + ", " + traj2.length);
//
//        double[][] disMatrix = new double[traj1.length][traj2.length];
//        for (int i = 0; i < traj1.length; i++) {
//            int lowBound = Math.max(i - 5, 0);
//            int upBound = Math.min(i + 5, traj2.length);
//            for (int j = lowBound; j < upBound; j++) {
//                disMatrix[i][j] = getDis(traj1[i], traj2[j]);
//            }
//        }
//        System.out.println("-------------------------------------");
//        for (double[] ary : disMatrix) {
//            System.out.println(Arrays.toString(ary));
//        }
//        System.out.println("-------------------------------------");
//        for (int i = 1; i < traj1.length; i++) {
//            int lowBound = Math.max(i - 5, 1);
//            int upBound = Math.min(i + 5, traj2.length);
//
//            for (int j = lowBound; j < upBound; j++) {
//                disMatrix[i][j] = Math.min(Math.min(disMatrix[i - 1][j - 1], disMatrix[i - 1][j]), disMatrix[i][j - 1]) + disMatrix[i][j];
//            }
//        }
//        return disMatrix[traj1.length - 1][traj2.length - 1];
//    }

    public static double getDis(Point pot1, Point pot2) {
        return Math.hypot(pot1.x - pot2.x, pot1.y - pot2.y);

//        return ((Math.pow(pot1.x - pot2.x, 2) + Math.pow(pot1.y - pot2.y, 2)));
    }

    public static void printMsg(int i, int j) {
        new Thread() {
            @Override
            public void run() {
                System.out.println(i + ", " + j);
            }
        }.start();
    }


    public static double calTrajPairDis(Point[] traj1, Point[] traj2) {

        double[][] disMatrix = new double[traj1.length][traj2.length];
        double a = traj2.length * 1.0 / traj1.length;

        for (int i = 0; i < traj1.length; i++) {
            int base = (int) (a * i);
            int lowBound = Math.max(base - 5, 0);
            int upBound = Math.min(base + 5, traj2.length);
            for (int j = lowBound; j < upBound; j++) {
                disMatrix[i][j] = getDis(traj1[i], traj2[j]);
            }
        }
        for (int i = 1; i < traj1.length; i++) {
            int base = (int) (a * i);

            int lowBound = Math.max(base - 5, 1);
            int upBound = Math.min(base + 5, traj2.length);
            for (int j = lowBound; j < upBound; j++) {
                disMatrix[i][j] = Math.min(Math.min(disMatrix[i - 1][j - 1], disMatrix[i - 1][j]), disMatrix[i][j - 1]) + disMatrix[i][j];
            }
        }
        return disMatrix[traj1.length - 1][traj2.length - 1];
    }

    private static HashSet<Integer>[] getCaledIdSet(int num) {
        HashSet<Integer>[] hashSetList = new HashSet[num];
        for (int i = 0; i < num; i++) {
            hashSetList[i] = new HashSet<>();
        }
        String fileNamePattern = "data/%d_.txt";
        int size = (2389863 - 2000000) / num;
        for (int i = 0; i < 6; i++) {
            System.out.println(i);
            int begin = i * size;
            String fileName = String.format(fileNamePattern, begin + 2000000);
            try {
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                String line = null;
                ArrayList<String> tmp = new ArrayList<>();
                int nums = 0;
                String lastLine = line;
                while (true) {
                    lastLine = line;
                    line = reader.readLine();
                    if (line == null)
                        break;
                }
                System.out.println(lastLine.split(";")[0]);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return hashSetList;
    }


    public static void main(String[] args) {

        PApplet.main(new String[]{DTW2.class.getName()});
    }
}
