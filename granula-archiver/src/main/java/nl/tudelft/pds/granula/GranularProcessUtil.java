package nl.tudelft.pds.granula;

import java.util.Date;

/**
 * Created by wing on 18-8-15.
 */
public class GranularProcessUtil {
    private long startTime;
    private long endTime;

    private long overallTime;

    private long decompressionTime;
    private long assemblingTime;
    private long writingTime;


    public void displayProcess() {



        System.out.println(String.format("Archiving workload at: %s.", new Date(startTime)));
        System.out.println(String.format("Archived workload at: %s.", new Date(endTime)));

        System.out.println(String.format("Archive decompression takes %.1f seconds.", (decompressionTime - startTime) / 1000.0d));
        System.out.println(String.format("Archive assembling takes %.1f seconds.", (assemblingTime - decompressionTime) / 1000.0d));
        System.out.println(String.format("Archive writing takes %.1f seconds.", (writingTime - assemblingTime) / 1000.0d));
        System.out.println(String.format("Archiving takes %.1f seconds.", (endTime - startTime) / 1000.0d));

    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getOverallTime() {
        return overallTime;
    }

    public void setOverallTime(long overallTime) {
        this.overallTime = overallTime;
    }

    public long getDecompressionTime() {
        return decompressionTime;
    }

    public void setDecompressionTime(long decompressionTime) {
        this.decompressionTime = decompressionTime;
    }


    public long getAssemblingTime() {
        return assemblingTime;
    }

    public void setAssemblingTime(long assemblingTime) {
        this.assemblingTime = assemblingTime;
    }

    public long getWritingTime() {
        return writingTime;
    }

    public void setWritingTime(long writingTime) {
        this.writingTime = writingTime;
    }
}
