package nl.tudelft.pds.granula;

import nl.tudelft.pds.granula.archiver.source.WorkloadLog;
import nl.tudelft.pds.granula.modeller.giraph.job.Giraph;

import java.io.File;
import java.util.Arrays;

/**
 * Created by wing on 21-8-15.
 */
public class GiraphArchiver {
    public static void main(String[] args) {


        String repoPath = ArchiverConfiguration.repoPath;

        String workloadDirPath = repoPath + "/data/input/";
        File workloadDir = new File(workloadDirPath);
        File[] workloadFiles = workloadDir.listFiles();

        if(workloadFiles == null || workloadFiles.length < 1) {
            throw new IllegalStateException("No data source found.");
        }

        Arrays.sort(workloadFiles);
        File workloadFile =workloadFiles[workloadFiles.length - 1];
        workloadFile = new File(repoPath + "/data/input/giraph.tar.gz");
        WorkloadLog workloadLog = new WorkloadLog(workloadFile.getName().replace(".tar.gz", ""), workloadDirPath + workloadFile.getName());

        String outputPath = repoPath + "/granula-visualizer/archive/";
//        String outputPath = String.format(\"/home/wing/Workstation/Dropbox/Repo/granula/data/output/giraph.xml\", workloadLog.getName());


        GranulaArchiver granulaArchiver = new GranulaArchiver(workloadLog, new Giraph(), outputPath);
        granulaArchiver.archive();

    }
}
