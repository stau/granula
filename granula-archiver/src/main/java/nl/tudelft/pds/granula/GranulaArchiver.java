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

import nl.tudelft.pds.granula.archiver.archive.ArchiveManager;
import nl.tudelft.pds.granula.archiver.entity.operation.Job;
import nl.tudelft.pds.granula.archiver.entity.operation.Workload;
import nl.tudelft.pds.granula.archiver.hierachy.HierachyManager;
import nl.tudelft.pds.granula.archiver.log.LogManager;
import nl.tudelft.pds.granula.archiver.log.WorkloadLog;
import nl.tudelft.pds.granula.archiver.record.JobRecord;
import nl.tudelft.pds.granula.archiver.record.RecordManager;
import nl.tudelft.pds.granula.modeller.model.job.JobModel;
import nl.tudelft.pds.granula.util.ProgressUtil;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by wing on 2-2-15.
 */
public class GranulaArchiver {

    ProgressUtil progressUtil;

    // inputs
    WorkloadLog workloadLog;
    JobModel jobModel;
    String outputPath;

    // managers
    LogManager logManager;
    RecordManager recordManager;
    HierachyManager hierachyManager;
    ArchiveManager archiveManager;

    // deliverables
    JobRecord jobRecord;
    Job job;
    Workload workload;



    public GranulaArchiver(WorkloadLog workloadLog, JobModel jobModel, String outputPath) {
        progressUtil = new ProgressUtil();
        this.workloadLog = workloadLog;
        this.jobModel = jobModel;
        this.outputPath = outputPath;
    }

    public void archive() {

        progressUtil.setStartTime(System.currentTimeMillis());
        prepare();

        progressUtil.setDecompressionTime(System.currentTimeMillis());
        assemble();

        progressUtil.setAssemblingTime(System.currentTimeMillis());
        write();

        progressUtil.setWritingTime(System.currentTimeMillis());

        progressUtil.setEndTime(System.currentTimeMillis());
        progressUtil.displayProcess();
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

        hierachyManager = new HierachyManager(job);
        hierachyManager.build();

        archiveManager = new ArchiveManager();
        archiveManager.build(job);

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
