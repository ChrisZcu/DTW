//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;
//
//public class FrechetDisThread extends DTWCalThread {
//
//    public FrechetDisThread(Trajectory[] trajFull, int begin, int end, String path) {
//        super(trajFull, begin, end, path,0,0);
//    }
//
//    @Override
//    public void run() {
//        try {
//            for (int i = begin; i < end; i++) {
//                StringBuilder sb = new StringBuilder(i + ";");
//
//                Trajectory traj = trajFull[i];
//
//                for (int j = 0; j < i; j++) {
////                    if (begin == 0)
////                        printMsg(i, j);
//                    sb.append(FrechetDisV2.singleTrajDis(traj.poiList, trajFull[j].poiList)).append(",");
//                }
//                sb.append("0,");
//                for (int j = i + 1; j < trajFull.length; j++) {
////                    if (begin == 0)
////                        printMsg(i, j);
//                    sb.append(FrechetDisV2.singleTrajDis(traj.poiList, trajFull[j].poiList)).append(",");
//                }
//                writeIntoFile(sb);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void writeIntoFile(StringBuilder str) {
//        new Thread(){
//            @Override
//            public void run(){
//                write(str);
//            }
//        }.start();
//    }
//
//    private void write(StringBuilder str) {
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(path + begin + "_.txt", true));
//            writer.write(str.append("\n").toString());
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
