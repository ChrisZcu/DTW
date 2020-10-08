package baseline;

import model.Trajectory;

import java.util.Arrays;

/**
 * Max Heap of Traj.
 * <p>
 * Its arr begins at index 1.
 * <p>
 * TODO NEED TEST
 */
public final class MaxHeap {
    private final int size;             // size of the heap
    private final Trajectory[] heapArr;

    /**
     * Init the heap by the giving trajPart.
     * Suppose that this traj list is sorted by point number.
     * Only use first {@link #size} traj, and require the giving list is larger that this size.
     */
    public MaxHeap(Trajectory[] trajPart, int size) {
        this.size = size;
        this.heapArr = new Trajectory[size + 1];

        System.arraycopy(trajPart, 0, this.heapArr, 1, size);
        initScore();
        initHeap();
    }

    public int getSize() {
        return size;
    }

    /**
     * Cal all traj's score in the heap
     */
    private void initScore() {
        System.out.println("Init score");
        for (int i = 1; i <= size; i++) {
            Trajectory traj = heapArr[i];
            double score = DTW.getDisSum(traj, Double.MAX_VALUE);
            traj.setScore(score);
        }
    }

    /**
     * Init the heap in O(n) time.
     */
    private void initHeap() {
        System.out.println("Init heap order");
        for (int i = size; i >= 1; i--) {
            shiftDown(i);
        }
    }

    /**
     * Replace the heap top and then refresh the heap by {@link #shiftDown}.
     * Notice that there is no score determination.
     */
    public void replaceTop(Trajectory traj) {
        heapArr[1] = traj;
        shiftDown(1);
    }

    /**
     * Suppose that the heap is always non-empty.
     *
     * @return the traj at the heap top
     */
    public Trajectory getTopTraj() {
        return heapArr[1];
    }

    /**
     * Suppose that all input is valid.
     */
    private void shiftDown(int index) {
		/*if (index <= 0) {
			return;
		}*/

        while (true) {
            int leftIdx = 2 * index;
            if (leftIdx > size) {
                // no child
                return;
            }
            int rightIdx = 2 * index + 1;
            int maxIdx;

            if (rightIdx > size) {
                // no right child
                maxIdx = leftIdx;
            } else {
                // has right child, take bigger one
                maxIdx = heapArr[leftIdx].getScore() > heapArr[rightIdx].getScore() ?
                        leftIdx : rightIdx;
            }

            if (heapArr[index].getScore() >= heapArr[maxIdx].getScore()) {
                // no need for shiftDown
                return;
            }

            Trajectory tmp = heapArr[index];
            heapArr[index] = heapArr[maxIdx];
            heapArr[maxIdx] = tmp;

            // move to next
            index = maxIdx;
        }
    }

    /**
     * @return An array copy of {@link #heapArr}, without first null places.
     */
    public Trajectory[] getTrajArr() {
        return Arrays.copyOfRange(heapArr, 1, size + 1);
    }
}

