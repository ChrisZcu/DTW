import com.google.common.util.concurrent.ThreadFactoryBuilder;
import sun.nio.ch.ThreadPool;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.*;

public class FrechetDisThread extends DTWCalThread {
    ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("demo-pool-%d").build();
    ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    public FrechetDisThread(Trajectory[] trajFull, int begin, int end, String path) {
        super(trajFull, begin, end, path);
    }

    @Override
    public void run() {
        for (int i = begin; i < end; i++) {
            StringBuilder sb = new StringBuilder();

            if (begin == 0) {
                printMsg(i);
            }
            Trajectory traj = trajFull[i];

            for (int j = 0; j < i; j++) {
                sb.append(FrechetDisV2.singleTrajDis(traj.poiList, trajFull[j].poiList)).append(",");
            }
            sb.append("0,");
            for (int j = i + 1; j < trajFull.length; j++) {
                sb.append(FrechetDisV2.singleTrajDis(traj.poiList, trajFull[j].poiList)).append(",");
            }
            writeIntoFile(sb);
        }
    }

    @Override
    public void writeIntoFile(StringBuilder str) {
        singleThreadPool.execute(() -> write(str));
        singleThreadPool.shutdown();
    }

    private void write(StringBuilder str) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + begin + "_.txt", true));
            writer.write(str.append("\n").toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
