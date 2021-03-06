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

package nl.tudelft.pds.granula.archiver.source;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wing on 30-1-15.
 */
public class WorkloadFileSource extends CompressedSource {
    List<JobSource> embeddedJobSources;

    public WorkloadFileSource(String tarFilePath) {
        super(tarFilePath, "/tmp/Granula/Log", "tar");
        embeddedJobSources = new ArrayList<>();
    }

    @Override
    public void verify() {
        super.verify();
    }

    @Override
    public void load() {

        super.load();

        List<File> jobLogDirs = Arrays.asList(new File(this.tmpDirPath + "/OperationLog/").listFiles());

        File utilLogDir = new File(this.tmpDirPath + "/UtilizationLog/");


        for (File jobLogDir : jobLogDirs) {
            JobSource embeddedJobSource = new JobEmbeddedSource();
            embeddedJobSource.setOperationSource(new DirectorySource(jobLogDir.getAbsolutePath()));
            embeddedJobSource.setUtilSource(new DirectorySource(utilLogDir.getAbsolutePath()));
            embeddedJobSources.add(embeddedJobSource);
            embeddedJobSource.load();
        }
    }

    public List<JobSource> getEmbeddedJobSources() {
        return embeddedJobSources;
    }
}
