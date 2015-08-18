/*
 * Copyright 2015 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.tudelft.pds.granula;

import nl.tudelft.pds.granula.archiver.archive.ArchiveBuilder;
import nl.tudelft.pds.granula.archiver.entity.operation.Job;
import nl.tudelft.pds.granula.archiver.entity.operation.Workload;
import nl.tudelft.pds.granula.archiver.hierachy.HierachyBuilder;
import nl.tudelft.pds.granula.archiver.log.LogManager;
import nl.tudelft.pds.granula.archiver.log.WorkloadLog;
import nl.tudelft.pds.granula.archiver.record.JobRecord;
import nl.tudelft.pds.granula.archiver.record.RecordManager;
import nl.tudelft.pds.granula.modeller.giraph.GranularProcessUtil;
import nl.tudelft.pds.granula.modeller.giraph.job.Giraph;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by wing on 2-2-15.
 */
public class GranularArchiver {


    public GranularArchiver() {

    }

    public GranularArchiver(LogManager logManager) {

    }

    public void archive() {

    }

    public static void main(String[] args) {

        GranularProcessUtil granularProcessUtil = new GranularProcessUtil();

        granularProcessUtil.setStartTime(System.currentTimeMillis());


        String workloadDirPath = "/home/wing/Workstation/Dropbox/Repo/granula/data/input/";
        File workloadDir = new File(workloadDirPath);
        File[] workloadFiles = workloadDir.listFiles();
        Arrays.sort(workloadFiles);
        File workloadFile =workloadFiles[workloadFiles.length - 1];
//        workloadFile = new File("home/wing/Workstation/Dropbox/Projects/Result/log/giraph.tar.gz");
        WorkloadLog workloadLog = new WorkloadLog(workloadFile.getName().replace(".tar.gz", ""), workloadDirPath + workloadFile.getName());

        LogManager logManager = new LogManager(workloadLog);
        logManager.decompressLog();

        granularProcessUtil.setDecompressionTime(System.currentTimeMillis());

        RecordManager recordManager = new GiraphRecordManager(logManager.getWorkloadLog());
        List<JobRecord> jobRecords = recordManager.extract();


        granularProcessUtil.setExtractionTime(System.currentTimeMillis());


        List<Job> jobs = new ArrayList<>();

        int jobCounter = 0;
        for (JobRecord jobRecord : jobRecords) {
            Giraph giraph = new Giraph();

            HierachyBuilder hierachyBuilder = new HierachyBuilder(jobRecord, giraph);
            Job job  = hierachyBuilder.exportJob();

            ArchiveBuilder archiveBuilder = new ArchiveBuilder();


            archiveBuilder.build(job);
            jobs.add(job);

            jobCounter++;
            System.out.println("Job " + jobCounter + " completed with archive assembling. ");
        }

        granularProcessUtil.setAssemblingTime(System.currentTimeMillis());

        Workload workload = new Workload();

        for (Job job : jobs) {
            workload.addJob(job);
        }

        String xml = workload.export();
//        String xml = XMLFormatter.format(workload.export());

        try {
            PrintWriter writer;
            writer = new PrintWriter("/home/wing/Workstation/Dropbox/Repo/granula/granula-visualizer/archive/most-updated.xml", "UTF-8");
            writer.print(xml);
            writer.close();

            writer = new PrintWriter(String.format("/home/wing/Workstation/Dropbox/Repo/granula/data/output/%s.xml", workloadLog.getName()), "UTF-8");
            writer.print(xml);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        granularProcessUtil.setWritingTime(System.currentTimeMillis());


        granularProcessUtil.setEndTime(System.currentTimeMillis());
        granularProcessUtil.displayProcess();

    }

}
