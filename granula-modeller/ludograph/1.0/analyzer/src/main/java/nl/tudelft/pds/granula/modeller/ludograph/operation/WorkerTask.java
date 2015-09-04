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

package nl.tudelft.pds.granula.modeller.ludograph.operation;

import nl.tudelft.pds.granula.archiver.entity.info.Source;
import nl.tudelft.pds.granula.archiver.entity.info.SummaryInfo;
import nl.tudelft.pds.granula.archiver.entity.operation.Operation;
import nl.tudelft.pds.granula.modeller.ludograph.LudographType;
import nl.tudelft.pds.granula.modeller.model.operation.ConcreteOperationModel;
import nl.tudelft.pds.granula.modeller.rule.derivation.BasicSummaryDerivation;
import nl.tudelft.pds.granula.modeller.rule.derivation.RecordTimeSeriesDerivation;
import nl.tudelft.pds.granula.modeller.rule.linking.UniqueParentLinking;
import nl.tudelft.pds.granula.modeller.rule.visual.MainInfoTableVisualization;
import nl.tudelft.pds.granula.modeller.rule.visual.TimeSeriesVisualization;

import java.util.ArrayList;

public class WorkerTask extends ConcreteOperationModel {

    public WorkerTask() {
        super(LudographType.Worker, LudographType.Task);
    }

    public void loadRules() {
        super.loadRules();

        addLinkingRule(new UniqueParentLinking(LudographType.BspEngine, LudographType.BspIteration));

        addInfoDerivation(new RecordTimeSeriesDerivation(1, "cpu.user.utilization"));
        addInfoDerivation(new RecordTimeSeriesDerivation(1, "cpu.total.utilization"));

        addInfoDerivation(new RecordTimeSeriesDerivation(1, "memory.heap.used"));
        addInfoDerivation(new RecordTimeSeriesDerivation(1, "memory.nonheap.max"));
        addInfoDerivation(new RecordTimeSeriesDerivation(1, "memory.heap.max"));

        addInfoDerivation(new RecordTimeSeriesDerivation(1, "network.sent.bytes"));
        addInfoDerivation(new RecordTimeSeriesDerivation(1, "network.received.bytes"));
//        addInfoDerivation(new RecordInfoDerivation(1, "ResponseTime"));
//        addInfoDerivation(new ColorDerivation(1, LudographType.ColorGrey));
        addInfoDerivation(new SummaryDerivation(10));
        addVisualDerivation(new MainInfoTableVisualization(1,
                new ArrayList<String>() {{
//                    add("ResponseTime");
                }}));


        TimeSeriesVisualization cpuTSVisualization = new TimeSeriesVisualization(1, "CPUVisual", "CPU", "unknown", "");
        cpuTSVisualization.addY1Info("cpu.user.utilization");
        cpuTSVisualization.addY1Info("cpu.total.utilization");
        addVisualDerivation(cpuTSVisualization);

        TimeSeriesVisualization memoryTSVisualization = new TimeSeriesVisualization(1, "MemoryVisual", "Memory", "unknown", "");
        memoryTSVisualization.addY1Info("memory.heap.used");
        memoryTSVisualization.addY1Info("memory.nonheap.max");
        memoryTSVisualization.addY1Info("memory.heap.max");
        addVisualDerivation(memoryTSVisualization);

        TimeSeriesVisualization networkTSVisualization = new TimeSeriesVisualization(1, "NetworkVisual", "Network", "unknown", "");
        networkTSVisualization.addY1Info("network.sent.bytes");
        networkTSVisualization.addY1Info("network.received.bytes");
        addVisualDerivation(networkTSVisualization);
    }

    protected class SummaryDerivation extends BasicSummaryDerivation {

        public SummaryDerivation(int level) { super(level); }

        @Override
        public boolean execute() {
            Operation operation = (Operation) entity;
            String summary = String.format("The [%s] operation set up the AppMaster and starts to request Yarn Containers required for the BspIteration Execution. " +
                    "This operation starts after AppMaster starts operating, " +
                    "and ends when AppMaster being notified that all containers requests are granted. ",
                    operation.getName());
            summary += getBasicSummary(operation);

            SummaryInfo summaryInfo = new SummaryInfo("Summary");
            summaryInfo.setValue("A Summary");
            summaryInfo.addSummary(summary, new ArrayList<Source>());
            operation.addInfo(summaryInfo);
            return  true;
        }
    }


}
