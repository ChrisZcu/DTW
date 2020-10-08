package baseline;

import model.Point;
import model.Trajectory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Util {
    /**
     * Read all line into a list.
     * @param limit the max line when reading. -1 means read all.
     */
    public static List<String> readAllLines(String filePath, int limit) {
        // set initial capacity if the limit is set.
        List<String> res = (limit == -1) ? new ArrayList<>() : new ArrayList<>(limit);
        LineIterator it = null;
        int cnt = 0;

        System.out.print("Read raw string data from " + filePath + " ...");

        try {
            it = FileUtils.lineIterator(new File(filePath), "UTF-8");

            long loadTime = -System.currentTimeMillis();

            while (it.hasNext() && (limit == -1 || cnt < limit)) {
                res.add(it.nextLine());
                ++cnt;
            }

            loadTime += System.currentTimeMillis();

            System.out.println("\b\b\bfinished. size: " + res.size());
            System.out.println("Load to mem time : " + loadTime);

        } catch (IOException | NoSuchElementException e) {
            System.out.println("\b\b\bfailed. \nProblem line: " + cnt);
            e.printStackTrace();
        } finally {
            if (it != null) {
                LineIterator.closeQuietly(it);
            }
        }

        return res;
    }

    /**
     * Translate traj data list from the raw string lines
     */
    public static Trajectory[] lineStrToTraj(List<String> lineStrList) {
        Trajectory[] ret = new Trajectory[lineStrList.size()];
        int cnt = 0;
        for (String s : lineStrList) {
            String[] item = s.split(";");
            String[] data = item[1].split(",");

            Point[] poiList = new Point[data.length / 2 - 1];
            for (int i = 0, j = 0; i < data.length - 2; i = i + 2, j++) {
                // the longitude and latitude are reversed
                poiList[j] = new Point(Float.parseFloat(data[i + 1]),
                        Float.parseFloat(data[i]));
            }
            ret[cnt] = new Trajectory(cnt, poiList);;
            cnt ++;
        }
        return ret;
    }

    /**
     * Format:
     * trajId,0 (not used for now)
     */
    public static void saveTrajListToFile(String filePath, Trajectory[] trajList) {
        System.out.print("Write traj list to " + filePath + " ...");

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            for (Trajectory traj : trajList) {
                writer.write(traj.getTid() + "," + 0);
                writer.newLine();
            }
            System.out.println("\b\b\bfinished.");

        } catch (IOException e) {
            System.out.println("\b\b\bfailed.");
            e.printStackTrace();
        }
    }
}
