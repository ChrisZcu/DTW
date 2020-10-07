import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DTWCalThread extends Thread {
    Trajectory[] trajFull;
    int begin;
    int end;
    String path;
    int offset;
    int con;

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
            try {
                for (int i = con; i < end; i++) {

                    Trajectory traj = trajFull[i];

                    StringBuilder str = new StringBuilder(i + ";");
                    float res = 0;
                    long t0 = System.currentTimeMillis();
                    for (int j = 0; j < i; j++) {
                        res += DTW2.calTrajPairDis(traj.poiList, trajFull[j].poiList);
                    }
                    for (int j = i + 1; j < trajFull.length; j++) {
                        res += DTW2.calTrajPairDis(traj.poiList, trajFull[j].poiList);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printMsg(int i, int j) {
        System.out.println(i + "," + j + "......");
    }

    public void writeIntoFile(StringBuilder str) {
        new Thread() {
            @Override
            public void run() {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(path + begin + "_.txt", true));
                    writer.write(str.toString());
                    writer.close();
                } catch (
                        IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
