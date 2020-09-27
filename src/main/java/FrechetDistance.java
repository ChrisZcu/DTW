import cn.siat.vcc.util.math.Vec2;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.MapBox;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PApplet;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class FrechetDistance extends PApplet {

    UnfoldingMap map;
    private final int ZOOMLEVEL = 13;
    private final Location PRESENT = new Location(41.14, -8.639);


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
        map.zoomAndPanTo(ZOOMLEVEL, PRESENT);

        loadData("data/data_100_ran.txt");
//        calFreDis();
        random();
        System.out.println(rmvSet.size());
        System.out.println(rmvSet);
    }

    @Override
    public void draw() {
        map.draw();
        int i = 0;
        for (Point[] traj : trajFull) {
//            drawSingleTraj(traj, false);
            if (rmvSet.contains(i)) {
//                drawSingleTraj(traj, false);
            } else {
//                System.out.println(rmvSet.size());
                drawSingleTraj(traj, false);
            }
            i++;
        }
    }

    private void random() {
        Random ran = new Random(1);
        while (rmvSet.size() != trajFull.length * 0.5) {
            rmvSet.add(ran.nextInt(trajFull.length - 1));
        }
    }

    private void drawSingleTraj(Point[] traj, boolean color) {

        noFill();
        strokeWeight(1);
        if (!color) {
            stroke(new Color(190, 46, 29).getRGB());
        } else {
            stroke(new Color(235, 200, 68).getRGB());
        }
        beginShape();
        for (Point poi : traj) {
            vertex((float) poi.x, (float) poi.y);
        }
        endShape();
    }


    private HashSet<Integer> rmvSet;
    private Point[][] trajFull;

    private void calFreDis() {
        double[][] disMatrix = disMatrixCal();
        freRmv(disMatrix);
    }

    private void freRmv(double[][] disMatrix) {
        for (int i = 0; i < trajFull.length * 0.5; i++) {
            int id = -1;
            double maxDis = Double.MIN_VALUE;
            for (int j = 0; j < disMatrix.length; j++) {
                if (rmvSet.contains(j)) {
                    continue;
                }
                double[] trajDis = disMatrix[j];
                double localMinDis = Double.MAX_VALUE;
                for (int k = 0; k < trajDis.length; k++) {
                    if (rmvSet.contains(k)) {
                        continue;
                    }
                    if (disMatrix[j][k] < localMinDis) {
                        localMinDis = disMatrix[j][k];

                    }
                }
                System.out.println(localMinDis);
                if (localMinDis > maxDis) {
                    maxDis = localMinDis;
                    id = j;
                }
            }
            rmvSet.add(id);
        }
    }

    private double[][] disMatrixCal() {
        double[][] disMatrix = new double[trajFull.length][trajFull.length];

        for (int i = 0; i < trajFull.length; i++) {
            for (int j = 0; j < i; j++) {
                disMatrix[i][j] = singleTrajDis(trajFull[i], trajFull[j]);
            }
            disMatrix[i][i] = Double.MAX_VALUE;
            for (int j = i + 1; j < trajFull.length; j++) {
                disMatrix[i][j] = singleTrajDis(trajFull[i], trajFull[j]);
            }
        }
        System.out.println("cal done");
//        System.out.println(Arrays.deepToString(disMatrix));

        return disMatrix;
    }

    private double singleTrajDis(Point[] traj1, Point[] traj2) {
        double dis = Double.MIN_VALUE;
        for (Point point : traj1) {
            dis = Math.max(dis, point2TrajDis(point, traj2));
        }
        return dis;
    }

    private double point2TrajDis(Point point, Point[] traj) {
        double dis = Double.MAX_VALUE;
        for (int i = 0; i < traj.length - 1; i++) {
            dis = Math.min(dis, point2Line(traj[i], traj[i + 1], point));
        }
        return dis;
    }

    //优化
    private double point2Line(Point p1, Point p2, Point p) {
        double ans = 0;
        double a, b, c;
        a = distance(p1, p2);
        b = distance(p1, p);
        c = distance(p2, p);
        if (c + b == a) {//点在线段上
            ans = 0;
            return ans;
        }
        if (a <= 0.00001) {//不是线段，是一个点
            ans = b;
            return ans;
        }
        if (c * c >= a * a + b * b) { //组成直角三角形或钝角三角形，p1为直角或钝角
            ans = b;
            return ans;
        }
        if (b * b >= a * a + c * c) {// 组成直角三角形或钝角三角形，p2为直角或钝角
            ans = c;
            return ans;
        }
        // 组成锐角三角形，则求三角形的高
        double p0 = (a + b + c) / 2;// 半周长
        double s = (double) Math.sqrt(p0 * (p0 - a) * (p0 - b) * (p0 - c));// 海伦公式求面积
        ans = 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高）
        return ans;
    }

    private double distance(Point p1, Point p2) {
        return Math.hypot(p1.x - p2.x, p1.y - p2.y);
    }

    private void loadData(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            ArrayList<String> trajList = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                trajList.add(line);
            }
            rmvSet = new HashSet<Integer>((int) (trajList.size() * 0.5));
            trajFull = new Point[(trajList.size())][];
            int i = 0;
            for (String traj : trajList) {
                String[] data = traj.split(";")[1].split(",");
                Point[] trajVec = new Point[data.length / 2 - 1];
                for (int j = 0; j < data.length - 2; j += 2) {
                    ScreenPosition src = map.getScreenPosition(new Location(Double.parseDouble(data[j + 1]), Double.parseDouble(data[j])));
                    trajVec[j / 2] = new Point(src.x, src.y);
                }
                trajFull[i++] = trajVec;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{FrechetDistance.class.getName()});
    }

    class Point {
        public double x;
        public double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
