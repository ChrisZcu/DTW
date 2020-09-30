
import org.lwjgl.Sys;

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
                    double res = 0;
                    long t0 = System.currentTimeMillis();
                    for (int j = 0; j < i; j++) {
//                        str.append(DTW2.calTrajPairDis(traj.poiList, trajFull[j].scr)).append(",");
                        res += DTW2.calTrajPairDis(traj.poiList, trajFull[j].poiList);
                    }
//                    str.append("0,");
                    for (int j = i + 1; j < trajFull.length; j++) {
//                        str.append(DTW2.calTrajPairDis(traj.poiList, trajFull[j].scr)).append(",");
                        res += DTW2.calTrajPairDis(traj.poiList, trajFull[j].poiList);
                    }
                    if (begin == offset)
                        System.out.println(i + ", time used for single trajectory: " + (System.currentTimeMillis() - t0));
//                    str.append("\n");
                    str.append(res).append(",");
                    writeIntoFile(str.append("\n"));
//                    str.delete(0, str.length());
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
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + begin + "_.txt", true));
            writer.write(str.toString());
            writer.close();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }


}
