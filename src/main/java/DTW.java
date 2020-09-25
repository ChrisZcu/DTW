
import cn.siat.vcc.util.math.Vec2;
import processing.core.PApplet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;

public class DTW extends PApplet {
    private static float[][] trajFull = new float[12][];

    private static void loadRowData(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                String[] traj = line.split(";")[1].split(",");
                float[] trajData = new float[traj.length];
                int j = 0;
                for (String gps : traj) {
                    trajData[j++] = Float.parseFloat(gps);
                }
                trajFull[i++] = trajData;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void calTrajPairDis(float[] traj1, float[] traj2) {
        float[][] disMatrix = new float[traj1.length][traj2.length];

    }

    private float getDis(Vec2 pot1, Vec2 pot2) {
        return (float) Math.pow((Math.pow(pot1.x - pot2.x, 2) + Math.pow(pot1.y - pot2.y, 2)), 0.5);
    }

    public static void main(String[] args) {
        loadRowData("/data/data_10.txt");

    }
}
