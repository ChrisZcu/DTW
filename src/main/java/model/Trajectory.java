package model;

public final class Trajectory {
    private final int tid;
    private final Point[] poiList;

    private double score;

    public Trajectory(int tid, Point[] poiList) {
        this.tid = tid;
        this.poiList = poiList;
    }

    public final int getTid() {
        return tid;
    }

    public final Point[] getPoiList() {
        return poiList;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Trajectory{" +
                "tid=" + tid +
                ", score=" + score +
                '}';
    }
}