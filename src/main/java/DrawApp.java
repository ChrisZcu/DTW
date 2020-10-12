import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.MapBox;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PApplet;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DrawApp extends PApplet {
    private UnfoldingMap map;

    private Trajectory[] trajFull;

    private Trajectory[] trajDWT;
    private Trajectory[] trajDWTPart;

    private Trajectory[] trajShow;


    @Override
    public void settings() {
        size(1200, 800, P2D);
    }

    @Override
    public void setup() {
        initMap();

        loadData();
        Pair[] pairList = getTrajToScore("data/dtw_sz_20w");        // !!!
        Arrays.sort(pairList, (p1, p2) -> Double.compare(p2.score, p1.score));
        initTrajDWT(pairList);
        initTrajDWTPart(pairList);

        // default
        trajShow = trajDWTPart;

        initPaint();
    }

    private void initMap() {
        String WHITE_MAP_PATH = "https://api.mapbox.com/styles/v1/pacemaker-yc/ck4gqnid305z61cp1dtvmqh5y/tiles/512/{z}/{x}/{y}@2x?access_token=pk.eyJ1IjoicGFjZW1ha2VyLXljIiwiYSI6ImNrNGdxazl1aTBsNDAzZW41MDhldmQyancifQ.WPAckWszPCEHWlyNmJfY0A";

        map = new UnfoldingMap(this, new MapBox.CustomMapBoxProvider(WHITE_MAP_PATH));
        map.setZoomRange(1, 20);
        map.setBackgroundColor(255);
        MapUtils.createDefaultEventDispatcher(this, map);
        // !!!
//        map.zoomAndPanTo(11, new Location(41.14, -8.639));
        map.zoomAndPanTo(11, new Location(22.577456, 113.97001));
    }

    private void initPaint() {
        noFill();
        stroke(Color.RED.getRGB());
        strokeWeight(1f);
    }

    @Override
    public void draw() {
        surface.setTitle("" + frameCount);
        map.draw();
        drawTraj();
    }

    private void drawTraj() {
        for (Trajectory traj : trajShow) {
            beginShape();
            for (Point poi : traj.poiList) {
                // inverse on purpose
                Location loc = new Location(poi.y, poi.x);
                ScreenPosition sp = map.getScreenPosition(loc);
                vertex(sp.x, sp.y);
            }
            endShape();
        }
    }

    @Override
    public void keyReleased() {
        if (key == 'q') {
            trajShow = trajFull;
            System.out.println("show: Full");
        } else if (key == 'w') {
            trajShow = trajDWT;
            System.out.println("show: DWT");
        } else if (key == 'e') {
            trajShow = trajDWTPart;
            System.out.println("show: DWT part");
        }
    }

    private static class Pair {
        int tid;
        double score;

        public Pair(int tid, double score) {
            this.tid = tid;
            this.score = score;
        }
    }

    private Pair[] getTrajToScore(String filePath) {
        ArrayList<String> metaData = new ArrayList<>();
        try {
            File dir = new File(filePath);
            String[] fileList = dir.list();
            for (String file : fileList) {
                String subPath = filePath + "/" + file;
                System.out.println("Read from " + subPath);
                BufferedReader reader = new BufferedReader(new FileReader(subPath));
                String line;
                while ((line = reader.readLine()) != null) {
                    metaData.add(line);
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        HashMap<Integer, Double> trajToScore = new HashMap<>(metaData.size());
        Pair[] pairList = new Pair[metaData.size()];
        int idx = 0;
        for (String str : metaData) {
            String[] item = str.split(";");
            int id = Integer.parseInt(item[0]);
            double score = Double.parseDouble(item[1].split(",")[0]);
//            trajToScore.put(id, score);
            pairList[idx++] = new Pair(id, score);
        }
//        System.out.println(metaData.size() + ", " + trajToScore.size());
//        return trajToScore;
        System.out.println("metaData.size() = " + metaData.size());
        return pairList;
    }

    /**
     * Init {@link #trajDWT}
     */
    private void initTrajDWT(Pair[] pairList) {
        System.out.println("init traj dwt list");
        trajDWT = new Trajectory[pairList.length];
        int idx = 0;
        for (Pair pair : pairList) {
            Trajectory traj = trajFull[pair.tid];
            traj.tid = pair.tid;
            trajDWT[idx++] = traj;
        }

        // temp
        saveDTW();
    }

    /**
     * Init {@link #trajDWTPart}, set it to the first 20k traj in pairList
     */
    private void initTrajDWTPart(Pair[] pairList) {
        int len = (int) (trajDWT.length * 0.01);
        System.out.println("init traj dwt part list. size = " + len);
        trajDWTPart = new Trajectory[len];
        for (int i = 0; i < trajDWTPart.length; i++) {
            int tid = pairList[i].tid;
            trajDWTPart[i] = trajFull[tid];
        }

        // temp
        saveDTWPart();
    }

    private void saveDTW() {
        String path = "data/dwt_sz_20w.txt";   // !!!

        System.out.print("Write dtw result to " + path + " ...");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (Trajectory traj : trajDWT) {
                writer.write(traj.tid + "," + 0);
                writer.newLine();
            }
            System.out.println("\b\b\bfinished.");
        } catch (IOException e) {
            System.out.println("\b\b\bfailed.");
            e.printStackTrace();
        }
    }

    private void saveDTWPart() {
        String path = "data/dwt_sz_0.01.txt";   // !!!

        System.out.print("Write dtw part result to " + path + " ...");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (Trajectory traj : trajDWTPart) {
                writer.write(traj.tid + "," + 0);
                writer.newLine();
            }
            System.out.println("\b\b\bfinished.");
        } catch (IOException e) {
            System.out.println("\b\b\bfailed.");
            e.printStackTrace();
        }
    }

    public void loadData() {
        try {
            ArrayList<String> trajStr = new ArrayList<>();
            // !!!
            BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Administrator\\Desktop\\zhengxin\\vfgs\\sz_score.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                trajStr.add(line);
            }
            reader.close();
            System.out.println(trajStr.size());
            System.out.println("load done");
            int i = 0;

            trajFull = new Trajectory[trajStr.size()];
            for (String traj : trajStr) {
                String[] data = traj.split(";")[1].split(",");
                Point[] trajData = new Point[data.length / 2 - 1];
                for (int j = 0; j < data.length - 3; j += 2) {
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
        PApplet.main(new String[]{
                DrawApp.class.getName()
        });
    }
}
