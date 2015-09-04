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

package nl.tudelft.pds.granula.modeller.ludograph.job;

import nl.tudelft.pds.granula.archiver.entity.operation.Job;
import nl.tudelft.pds.granula.modeller.ludograph.LudographType;
import nl.tudelft.pds.granula.modeller.model.job.JobModel;
import nl.tudelft.pds.granula.modeller.rule.derivation.DerivationRule;
import nl.tudelft.pds.granula.modeller.ludograph.operation.*;
import nl.tudelft.pds.granula.modeller.rule.extraction.LudographExtractionRule;
import nl.tudelft.pds.granula.modeller.rule.filling.UniqueOperationFilling;

/**
 * Created by wing on 12-3-15.
 */
public class Ludograph extends JobModel {

    public Ludograph() {
        super();
        addOperationModel(new TopActorTopMission());
        addOperationModel(new BspEngineBspIteration());
        addOperationModel(new WorkerTask());
        addOperationModel(new ExecutorStep());
    }

    public void loadRules() {
        addFillingRule(new UniqueOperationFilling(1, LudographType.TopActor, LudographType.TopMission));
        addFillingRule(new UniqueOperationFilling(1, LudographType.BspEngine, LudographType.BspIteration));
        addInfoDerivation(new JobNameDerivationRule(2));
        addExtraction(new LudographExtractionRule(1));
    }



    protected class JobNameDerivationRule extends DerivationRule {

        public JobNameDerivationRule(int level) {
            super(level);
        }

        @Override
        public boolean execute() {

            Job job = (Job) entity;

//            Operation bspIteration = null;
//            Operation containerAssignment = null;
//            for (Operation operation : job.getTopOperation().getChildren()) {
//                if (operation.hasType(LudographType.AppMaster, LudographType.BspEngine)) {
//                    for (Operation suboperation : operation.getChildren()) {
//                        if (suboperation.hasType(LudographType.BspMaster, LudographType.BspIteration)) {
//                            bspIteration = suboperation;
//                        }
//                    }
//                }
//            }
//            for (Operation operation : job.getTopOperation().getChildren()) {
//                if (operation.hasType(LudographType.AppMaster, LudographType.Deployment)) {
//                    for (Operation suboperation : operation.getChildren()) {
//                        if (suboperation.hasType(LudographType.AppMaster, LudographType.ContainerAssignment)) {
//                            containerAssignment = suboperation;
//                        }
//                    }
//                }
//            }
//
//            Info numContainers = containerAssignment.getInfo("NumContainers");
//            Info containerHeapSize = containerAssignment.getInfo("ContainerHeapSize");
//
//            Info computeClass = bspIteration.getInfo("ComputationClass");
//            Info dataInputPath = bspIteration.getInfo("DataInputPath");
//
//            String fileName = new File(dataInputPath.getValue()).getName();
//
//            String jobName = String.format("%s-%s, %sx%sMB",
//                    computeClass.getValue().replace("Computation", ""), fileName,
//                    numContainers.getValue(), containerHeapSize.getValue());
//
//            BasicInfo jobNameInfo = new BasicInfo("JobName");
//            jobNameInfo.addInfo(jobName, new ArrayList<Source>());
//            job.addInfo(jobNameInfo);

//            job.setName(jobName);
            job.setName("random job name");
            job.setType("Ludograph");

            return true;

        }
    }
}
