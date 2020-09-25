
import cn.siat.vcc.util.math.Vec2;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.MapBox;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PApplet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class DTW extends PApplet {
    private static ArrayList<Vec2[]> trajFull = new ArrayList<>(12);
    static UnfoldingMap map = null;

    @Override
    public void settings() {
        size(1000, 800);
    }

    @Override
    public void setup() {
        String WHITE_MAP_PATH = "https://api.mapbox.com/styles/v1/pacemaker-yc/ck4gqnid305z61cp1dtvmqh5y/tiles/512/{z}/{x}/{y}@2x?access_token=pk.eyJ1IjoicGFjZW1ha2VyLXljIiwiYSI6ImNrNGdxazl1aTBsNDAzZW41MDhldmQyancifQ.WPAckWszPCEHWlyNmJfY0A";

        map = new UnfoldingMap(this, new MapBox.CustomMapBoxProvider(WHITE_MAP_PATH));
        map.setZoomRange(1, 20);
        map.setBackgroundColor(255);
        MapUtils.createDefaultEventDispatcher(this, map);
        map.zoomAndPanTo(20, new Location(41.14, -8.639));

        calDTW();
    }

    private void calDTW() {
        loadRowData("data/data_10.txt");
        long t0 = System.currentTimeMillis();
        float[][] trajDisMatrix = new float[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                trajDisMatrix[i][j] = calTrajPairDis(trajFull.get(i), trajFull.get(j));
            }
        }
        System.out.println("time for 12 trajectories: " + (System.currentTimeMillis() - t0) + " ms");
        for (int i = 0; i < 12; i++) {
            long disTotal = 0;
            for (int j = 0; j < 12; j++) {
                disTotal += trajDisMatrix[i][j];
            }
            System.out.println(i + ": " + disTotal);
        }
    }

    private static void loadRowData(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] traj = line.split(";")[1].split(",");
                Vec2[] trajData = new Vec2[traj.length / 2];
                for (int j = 0; j < traj.length - 1; j += 2) {
                    Location loc = new Location(Float.parseFloat(traj[j + 1]), Float.parseFloat(traj[j]));
                    ScreenPosition src = map.getScreenPosition(loc);
                    trajData[j / 2] = new Vec2(src.x, src.y);
                }
                trajFull.add(trajData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static float calTrajPairDis(Vec2[] traj1, Vec2[] traj2) {
        float[][] disMatrix = new float[traj1.length][traj2.length];
        for (int i = 0; i < traj1.length; i++) {
            for (int j = 0; j < traj2.length; j++) {
                disMatrix[i][j] = getDis(traj1[i], traj2[j]);
            }
        }
        for (int i = 1; i < traj1.length; i++) {
            for (int j = 1; j < traj2.length; j++) {
                disMatrix[i][j] = Math.min(Math.min(disMatrix[i - 1][j - 1], disMatrix[i - 1][j]), disMatrix[i][j - 1]) + disMatrix[i][j];
            }
        }
        return disMatrix[traj1.length - 1][traj2.length - 1];
    }

    private static float getDis(Vec2 pot1, Vec2 pot2) {
        return (float) Math.pow((Math.pow(pot1.x - pot2.x, 2) + Math.pow(pot1.y - pot2.y, 2)), 0.5);
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{DTW.class.getName()});
    }
}
