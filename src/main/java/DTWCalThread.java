import cn.siat.vcc.util.math.Vec2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DTWCalThread extends Thread {
    Trajectory[] trajFull;
    int begin;
    int end;
    String path;

    public DTWCalThread(Trajectory[] trajFull, int begin, int end, String path) {
        this.trajFull = trajFull;
        this.begin = begin;
        this.end = end;
        this.path = path;
    }

    @Override
    public void run() {
        int bound = 0;
        StringBuilder str = new StringBuilder();

        for (int i = begin; i < end; i++) {
            if (begin == 0) {
                printMsg(i);
            }
            bound++;
            Trajectory traj = trajFull[i];

            str.append(i).append(";");

            for (int j = 0; j < i; j++) {
                str.append(DTW.calTrajPairDis(traj.scr, trajFull[j].scr)).append(",");
            }
            str.append("0,");
            for (int j = i + 1; j < trajFull.length; j++) {
                str.append(DTW.calTrajPairDis(traj.scr, trajFull[j].scr)).append(",");
            }

            str.append("\n");

            if (bound == 10) {
                writeIntoFile(str);
                str.delete(0, str.length());
                bound = 0;
            }
        }
        writeIntoFile(str);
    }

    public void printMsg(int i) {
        new Thread() {
            @Override
            public void run() {
                System.out.println(i + "......");
            }
        }.start();
    }

    public void writeIntoFile(StringBuilder str) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + begin + "_.txt", true));
            writer.write(str.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
