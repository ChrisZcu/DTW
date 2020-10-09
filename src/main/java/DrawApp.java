import de.fhpotsdam.unfolding.UnfoldingMap;
import processing.core.PApplet;
import processing.core.PImage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DrawApp extends PApplet {
    private UnfoldingMap map;
    PImage mapImage = null;

    @Override
    public void settings() {
        size(1200, 800, P2D);
    }

    @Override
    public void setup() {
        getTrajToScore("data/dtw");
    }

    public void keyPressed() {
        if (key == 'q') {
            trajShow = trajFull;
        } else if (key == 'w') {

        } else if (key == 'e') {
            trajShow = new Trajectory[0];
        }
    }

    private HashMap<Integer, Double> getTrajToScore(String filePath) {
        ArrayList<String> metaData = new ArrayList<>();
        try {
            File dir = new File(filePath);
            String[] fileList = dir.list();
            for (String file : fileList) {
                String subPath = filePath + "/" + file;
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
        HashMap<Integer, Double> trajToScore = new HashMap<>(metaData.size());
        for (String str : metaData) {
            String[] item = str.split(";");
            int id = Integer.parseInt(item[0]);
            double score = Double.parseDouble(item[1].split(",")[0]);
            trajToScore.put(id, score);
        }
        System.out.println(metaData.size() + ", " + trajToScore.size());
        return trajToScore;
    }

    private Trajectory[] trajFull;
    Trajectory[] trajShow;

    public void loadData() {
        try {
            ArrayList<String> trajStr = new ArrayList<>(2400000);
            BufferedReader reader = new BufferedReader(new FileReader("data/GPS/porto_full.txt"));
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
        PApplet.main(new String[]{
                DrawApp.class.getName()
        });
    }
}
