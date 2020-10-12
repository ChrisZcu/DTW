import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class  DTWCalThread extends Thread {
    private static final int MAX_POS_SIZE = 4000;

    Trajectory[] trajFull;
    int begin;
    int end;
    String path;
    int offset;
    int con;

    double[][] disMatrix = new double[MAX_POS_SIZE][MAX_POS_SIZE];

    public DTWCalThread(Trajectory[] trajFull, int begin, int end, String path, int offSet, int con) {
        this.trajFull = trajFull;
        this.begin = begin;
        this.end = end;
        this.path = path;
        this.offset = offSet;
        this.con = con;
    }

    @Override
    public void run() {
        try {
            for (int i = con; i < end; i++) {

                Trajectory traj = trajFull[i];

                StringBuilder str = new StringBuilder(i + ";");
                float res = 0;
                long t0 = System.currentTimeMillis();
                for (int j = 0; j < i; j++) {
                    res += calTrajPairDis(traj.poiList, trajFull[j].poiList);
                }
                for (int j = i + 1; j < trajFull.length; j++) {
                    res += calTrajPairDis(traj.poiList, trajFull[j].poiList);
                }
                if (begin == offset) {
                    System.out.println(i + "......");
                }
                str.append(res).append(",");
                writeIntoFile(str.append("\n"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double calTrajPairDis(Point[] traj1, Point[] traj2) {
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

    private static double getDis(Point pot1, Point pot2) {
        return Math.hypot(pot1.x - pot2.x, pot1.y - pot2.y);
//        return ((Math.pow(pot1.x - pot2.x, 2) + Math.pow(pot1.y - pot2.y, 2)));
    }

    public void printMsg(int i, int j) {
        System.out.println(i + "," + j + "......");
    }

    public void writeIntoFile(StringBuilder str) {
        new Thread(() -> {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(path + begin + "_.txt", true));
                writer.write(str.toString());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
