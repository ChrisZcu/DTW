import cn.siat.vcc.util.math.Vec2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DTWCalThread extends Thread {
    Trajectory[] trajFull;
    int begin;
    int end;
    String path;
    int offset;

    public DTWCalThread(Trajectory[] trajFull, int begin, int end, String path, int offSet) {
        this.trajFull = trajFull;
        this.begin = begin;
        this.end = end;
        this.path = path;
        this.offset = offSet;
    }

    @Override
    public void run() {
        try {
            try {
                for (int i = begin; i < end; i++) {
                    double[] res = new double[trajFull.length];
                    if (begin == offset) {
                        printMsg(i, 0);
                    }
                    Trajectory traj = trajFull[i];

                    StringBuilder str = new StringBuilder(i + ";");


                    for (int j = 0; j < i; j++) {
                        if (begin == offset) {
                            printMsg(i, j);
                        }
//                        str.append(DTW2.calTrajPairDis(traj.scr, trajFull[j].scr)).append(",");
                        res[j] = DTW2.calTrajPairDis(traj.scr, trajFull[j].scr);
                    }
//                    str.append("0,");
                    for (int j = i + 1; j < trajFull.length; j++) {
                        if (begin == offset) {
                            printMsg(i, j);
                        }
//                        str.append(DTW2.calTrajPairDis(traj.scr, trajFull[j].scr)).append(",");
                        res[j] = DTW2.calTrajPairDis(traj.scr, trajFull[j].scr);
                    }

//                    str.append("\n");
                    for (Double d : res) {
                        str.append(d).append(",");
                    }
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
        new Thread() {
            @Override
            public void run() {
                if (j % 1000 == 0)
                    System.out.println(i + "," + j + "......");
            }
        }.start();
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
