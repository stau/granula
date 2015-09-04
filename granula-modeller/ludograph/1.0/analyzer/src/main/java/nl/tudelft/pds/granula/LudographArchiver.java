package nl.tudelft.pds.granula;

import nl.tudelft.pds.granula.archiver.source.JobDirectorySource;
import nl.tudelft.pds.granula.modeller.ludograph.job.Ludograph;
import nl.tudelft.pds.granula.util.JobListGenerator;

import java.io.File;
import java.util.Arrays;

/**
 * Created by wing on 21-8-15.
 */
public class LudographArchiver {
    public static void main(String[] args) {

        String repoPath = "/home/wlngai/Downloads/granula";
        String inputPath = repoPath + "/data/input/ludograph-stau";
        String outputPath = repoPath + "/granula-visualizer/data/archive/";

        JobDirectorySource jobDirSource = new JobDirectorySource(inputPath);
        jobDirSource.load();

        GranulaArchiver granulaArchiver = new GranulaArchiver(jobDirSource, new Ludograph(), outputPath);
        granulaArchiver.archive();

        // generate list
        (new JobListGenerator()).generateRecentJobsList(repoPath);

    }
}
