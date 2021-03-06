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

package nl.tudelft.pds.granula.modeller.rule.filling;

import nl.tudelft.pds.granula.archiver.entity.Identifier;
import nl.tudelft.pds.granula.archiver.entity.operation.Actor;
import nl.tudelft.pds.granula.archiver.entity.operation.Job;
import nl.tudelft.pds.granula.archiver.entity.operation.Mission;
import nl.tudelft.pds.granula.archiver.entity.operation.Operation;
import nl.tudelft.pds.granula.modeller.model.job.JobModel;

public class UniqueOperationFilling extends FillingRule {

    String actorType;
    String missionType;

    public UniqueOperationFilling(int level, String actorType, String missionType) {
        super(level);
        this.actorType = actorType;
        this.missionType = missionType;

    }

    @Override
    public boolean execute() {
        Job job = (entity instanceof Job) ? (Job) entity : ((Operation) entity).getJob();


        Operation operation = new Operation();

        Actor actor = new Actor(actorType, Identifier.Unique);
        actor.addOperation(operation);

        Mission mission = new Mission(missionType, Identifier.Unique);
        mission.addOperation(operation);

        operation.setMission(mission);
        operation.setActor(actor);
        operation.setJob(job);
        operation.setModel(((JobModel) job.getModel()).getOperationModel(operation.getType()));
        job.addMemberOperations(operation);

        return  true;
    }
}
