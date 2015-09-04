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

package nl.tudelft.pds.granula.modeller.graphx.operation;

import nl.tudelft.pds.granula.archiver.entity.info.Source;
import nl.tudelft.pds.granula.archiver.entity.info.SummaryInfo;
import nl.tudelft.pds.granula.archiver.entity.operation.Operation;
import nl.tudelft.pds.granula.modeller.model.operation.ConcreteOperationModel;
import nl.tudelft.pds.granula.modeller.rule.derivation.BasicSummaryDerivation;
import nl.tudelft.pds.granula.modeller.rule.derivation.FilialAggStringDerivation;
import nl.tudelft.pds.granula.modeller.rule.derivation.FilialLongAggregationDerivation;
import nl.tudelft.pds.granula.modeller.rule.derivation.RecordInfoDerivation;
import nl.tudelft.pds.granula.modeller.rule.derivation.time.DurationDerivation;
import nl.tudelft.pds.granula.modeller.rule.derivation.time.FilialEndTimeDerivation;
import nl.tudelft.pds.granula.modeller.rule.derivation.time.FilialStartTimeDerivation;
import nl.tudelft.pds.granula.modeller.rule.visual.MainInfoTableVisualization;
import nl.tudelft.pds.granula.modeller.graphx.GraphXType;
import nl.tudelft.pds.granula.modeller.rule.linking.StageParentLinking;

import java.util.ArrayList;

public class DagSchedulerStage extends ConcreteOperationModel {

    public DagSchedulerStage() {
        super(GraphXType.DagScheduler, GraphXType.Stage);
    }

    public void loadRules() {
        super.loadRules();

        addLinkingRule(new StageParentLinking(1));


        addInfoDerivation(new RecordInfoDerivation(1, "StackTrace"));
        addInfoDerivation(new FilialLongAggregationDerivation(2, GraphXType.Task, "RunTime"));
        addInfoDerivation(new FilialLongAggregationDerivation(2, GraphXType.Task, "InputSize"));
        addInfoDerivation(new FilialAggStringDerivation(2, GraphXType.Task, "InputMethod"));
        addInfoDerivation(new FilialLongAggregationDerivation(2, GraphXType.Task, "ShuffleRead"));
        addInfoDerivation(new FilialLongAggregationDerivation(2, GraphXType.Task, "ShuffleWrite"));
        addInfoDerivation(new FilialStartTimeDerivation(2));
        addInfoDerivation(new FilialEndTimeDerivation(2));
        addInfoDerivation(new DurationDerivation(3));
        addInfoDerivation(new SummaryDerivation(10));

        addVisualDerivation(new MainInfoTableVisualization(1,
                new ArrayList<String>() {{
                    add("InputMethod");
                    add("RunTime");
                    add("InputSize");
                    add("InputMethod");
                    add("ShuffleRead");
                    add("ShuffleWrite");
                }}));
    }


    protected class SummaryDerivation extends BasicSummaryDerivation {

        public SummaryDerivation(int level) { super(level); }

        @Override
        public boolean execute() {
                Operation operation = (Operation) entity;
                String summary = String.format("The [%s] operation contains a set of independent tasks all computing the same " +
                        "function that need to run as part of a Spark job, where all the tasks have the same shuffle dependencies. " +
                        "Each DAG of tasks run by the scheduler is split up into stages at the boundaries where shuffle occurs, " +
                        "and then the DAGScheduler runs these stages in topological order. ", operation.getName());
                summary += getBasicSummary(operation);

                SummaryInfo summaryInfo = new SummaryInfo("Summary");
                summaryInfo.setValue("A Summary");
                summaryInfo.addSummary(summary, new ArrayList<Source>());
                operation.addInfo(summaryInfo);
                return  true;
        }
    }

}
