import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.MapBox;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.jar.JarOutputStream;

public class UserInterface extends PApplet {
    UnfoldingMap map;

    @Override
    public void settings(){
        size(1000,800,P2D);
    }

    @Override
    public void setup(){
        String WHITE_MAP_PATH = "https://api.mapbox.com/styles/v1/pacemaker-yc/ck4gqnid305z61cp1dtvmqh5y/tiles/512/{z}/{x}/{y}@2x?access_token=pk.eyJ1IjoicGFjZW1ha2VyLXljIiwiYSI6ImNrNGdxazl1aTBsNDAzZW41MDhldmQyancifQ.WPAckWszPCEHWlyNmJfY0A";

        map = new UnfoldingMap(this, new MapBox.CustomMapBoxProvider(WHITE_MAP_PATH));
        map.setZoomRange(1, 20);
        map.setBackgroundColor(255);
        MapUtils.createDefaultEventDispatcher(this, map);
        map.zoomAndPanTo(13, new Location(41.14, -8.639));

        new Thread(){
            @Override
            public void run(){
             loadTotalData("data/porto_full.txt");
            }
        }.start();
    }

    boolean cleanTime = true;
    @Override
    public void draw(){
        if (!map.allTilesLoaded()) {
            map.draw();
        } else {
            if (cleanTime){
                map.draw();
            }

        }
    }

    private Trajectory[] totalTrajector;
    private int[] trajScore;
    private void loadTotalData(String filePath){
        System.out.println("data pre-processing......");
        ArrayList<String> totalTraj = new ArrayList<>();
        try{
            BufferedReader reader= new BufferedReader(new FileReader(filePath));
            String line;
            while((line = reader.readLine())!= null){
                totalTraj.add(line);
            }
            reader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        totalTrajector = new Trajectory[totalTraj.size()];
        trajScore = new int[totalTraj.size()];
        int i=0;
        for (String trajStr : totalTraj){
            String[] item = trajStr.split(";");
            trajScore[i] = Integer.parseInt(item[0]);
            String[] trajPoint = item[1].split(",");
            Point[] trajData = new Point[trajPoint.length / 2 - 1];
            for (int j=0; j < trajPoint.length - 1; j++){
                trajData[j / 2] = new Point(Float.parseFloat(trajPoint[j + 1]), Float.parseFloat(trajPoint[j]));
            }
            totalTrajector[i++] = new Trajectory(trajData);
        }
        System.out.println("data preprocess done");
    }
    public static void main(String[] args) {
        PApplet.main(new String[]{UserInterface.class.getName()});
    }
}

