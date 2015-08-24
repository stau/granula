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
import nl.tudelft.pds.granula.modeller.fundamental.model.job.JobModel;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Created by wing on 2-2-15.
 */
public class GranularArchiver {

    GranularProcessUtil granularProcessUtil;

    // inputs
    WorkloadLog workloadLog;
    JobModel jobModel;
    String outputPath;

    // managers
    LogManager logManager;
    RecordManager recordManager;
    HierachyBuilder hierachyBuilder;
    ArchiveBuilder archiveBuilder;

    // deliverables
    JobRecord jobRecord;
    Job job;
    Workload workload;



    public GranularArchiver(WorkloadLog workloadLog, JobModel jobModel, String outputPath) {
        granularProcessUtil = new GranularProcessUtil();
        this.workloadLog = workloadLog;
        this.jobModel = jobModel;
        this.outputPath = outputPath;
    }

    public void archive() {

        granularProcessUtil.setStartTime(System.currentTimeMillis());
        prepare();

        granularProcessUtil.setDecompressionTime(System.currentTimeMillis());
        assemble();

        granularProcessUtil.setAssemblingTime(System.currentTimeMillis());
        write();

        granularProcessUtil.setWritingTime(System.currentTimeMillis());

        granularProcessUtil.setEndTime(System.currentTimeMillis());
        granularProcessUtil.displayProcess();
    }


    public void prepare() {
        logManager = new LogManager(workloadLog);
        logManager.decompressLog();
    }

    public void assemble() {
        job = new Job();
        job.setModel(jobModel);

        recordManager = new RecordManager(job, workloadLog.getJobDataSources().get(0));
        recordManager.extract();

        hierachyBuilder = new HierachyBuilder(job);
        archiveBuilder = new ArchiveBuilder();
        archiveBuilder.build(job);

        workload = new Workload();
        workload.addJob(job);
    }

    public void write() {
        String xml = workload.export();
//      String xml = XMLFormatter.format(workload.export());

        try {
            PrintWriter writer;
            writer = new PrintWriter(outputPath, "UTF-8");
            writer.print(xml);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
