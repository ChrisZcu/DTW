package baseline;

import model.Point;
import model.Trajectory;

public final class DTW {
    public static final int MAX_POS_SIZE = 4000;

    private static final double[][] disMatrix = new double[MAX_POS_SIZE][MAX_POS_SIZE];
    private static Trajectory[] trajFull;

    /**
     * Call it before call {@link #getDisSum}
     */
    public static void setTrajFull(Trajectory[] trajFull) {
        DTW.trajFull = trajFull;
    }

    /**
     * Get DTW dis sum of one traj, use {@link #trajFull}
     * @param traj the traj that need to cal
     * @param threshold if the sum is already large that the threshold (which is the top of {@link MaxHeap},
     *                  then this traj can not be the result.
     *                  Input {@code Double.MAX_VALUE} to disable it.
     * @return Sum or {@code Double.MAX_VALUE} if it is larger than (or equal to) threshold
     */
    public static double getDisSum(Trajectory traj, double threshold) {
        double res = 0;

        int fullLen = trajFull.length;
        int tid = traj.getTid();
        Point[] poiList = trajFull[tid].getPoiList();        // take pointer to save time

        for (int i = 0; i < tid; i++) {
            res += getTrajDis(poiList, trajFull[i].getPoiList());
            if (res >= threshold) {
                return Double.MAX_VALUE;
            }
        }
        // not to cal on itself (is 0)
        for (int i = tid + 1; i < fullLen; i++) {
            res += getTrajDis(poiList, trajFull[i].getPoiList());
            if (res >= threshold) {
                return Double.MAX_VALUE;
            }
        }

        return res;
    }

    /**
     * DTW distance of two trajs
     */
    private static double getTrajDis(Point[] poiList1, Point[] poiList2) {
        int len1 = poiList1.length;
        int len2 = poiList2.length;
        double rate = len2 * 1.0 / len1;

        for (int i = 0; i < len1; i++) {
            int base = (int) (rate * i);
            int lowBound = Math.max(base - 5, 0);
            int upBound = Math.min(base + 5, len2);
            for (int j = lowBound; j < upBound; j++) {
                disMatrix[i][j] = getPointDis(poiList1[i], poiList2[j]);
            }
        }
        for (int i = 1; i < len1; i++) {
            int base = (int) (rate * i);

            int lowBound = Math.max(base - 5, 1);
            int upBound = Math.min(base + 5, len2);
            for (int j = lowBound; j < upBound; j++) {
                disMatrix[i][j] = Math.min(Math.min(disMatrix[i - 1][j - 1], disMatrix[i - 1][j]),
                        disMatrix[i][j - 1]) + disMatrix[i][j];
            }
        }

        return disMatrix[len1 - 1][len2 - 1];
    }

    private static double getPointDis(Point pot1, Point pot2) {
        double tmp1 = pot1.x - pot2.x;
        double tmp2 = pot1.y - pot2.y;
        return tmp1 * tmp1 + tmp2 * tmp2;
    }
}
