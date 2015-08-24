package nl.tudelft.pds.granula.archiver.log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wing on 24-8-15.
 */
public class JobDataSource {

    List<File> jobLogFiles;
    List<File> utilLogFiles;

    public JobDataSource(String logFilePath, String utilLogFilePath) {
        jobLogFiles = new ArrayList<>();
        List<String> jobOLFCollection = new ArrayList<>();
        List<File> olfs = new ArrayList<>(
                FileUtils.listFilesAndDirs(new File(logFilePath), TrueFileFilter.TRUE, TrueFileFilter.TRUE));
        for (File olf : olfs) {
            if(olf.isFile()) {
                jobLogFiles.add(olf);
            }
        }


        utilLogFiles = new ArrayList<>();
        File resourceLogDir = new File(utilLogFilePath);
        List<File> rLogFiles = new ArrayList<>(FileUtils.listFilesAndDirs(resourceLogDir, TrueFileFilter.TRUE, TrueFileFilter.TRUE));

        for (File rLogFile : rLogFiles) {
            if(!rLogFile.getAbsolutePath().contains("__SummaryInfo__")) {
                if(rLogFile.isFile()) {
                    utilLogFiles.add(rLogFile);
                }
            }
        }

    }

    public List<File> getJobLogFiles() {
        return jobLogFiles;
    }

    public List<File> getUtilLogFiles() {
        return utilLogFiles;
    }
}
