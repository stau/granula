package nl.tudelft.pds.granula;

import nl.tudelft.pds.granula.archiver.source.WorkloadLog;
import nl.tudelft.pds.granula.modeller.graphx.job.GraphX;

import java.io.File;
import java.util.Arrays;

/**
 * Created by wing on 21-8-15.
 */
public class GraphXArchiver {
    public static void main(String[] args) {

        String workloadDirPath = "/home/wing/Workstation/Dropbox/Repo/granula/data/input/";
        File workloadDir = new File(workloadDirPath);
        File[] workloadFiles = workloadDir.listFiles();
        Arrays.sort(workloadFiles);
        File workloadFile =workloadFiles[workloadFiles.length - 1];
        workloadFile = new File("/home/wing/Workstation/Dropbox/Repo/granula/data/input/graphx.tar.gz");
        WorkloadLog workloadLog = new WorkloadLog(workloadFile.getName().replace(".tar.gz", ""), workloadDirPath + workloadFile.getName());

        String outputPath = "/home/wing/Workstation/Dropbox/Repo/granula/granula-visualizer/archive/most-updated.xml";
//        String outputPath = String.format("/home/wing/Workstation/Dropbox/Repo/granula/data/output/graphx.xml", workloadLog.getName());


        GranulaArchiver granulaArchiver = new GranulaArchiver(workloadLog, new GraphX(), outputPath);
        granulaArchiver.archive();

    }
}
