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

package nl.tudelft.pds.granula.modeller.giraph.operation;

import nl.tudelft.pds.granula.archiver.entity.info.Source;
import nl.tudelft.pds.granula.archiver.entity.info.SummaryInfo;
import nl.tudelft.pds.granula.archiver.entity.operation.Operation;
import nl.tudelft.pds.granula.modeller.fundamental.rule.derivation.BasicSummaryDerivation;
import nl.tudelft.pds.granula.modeller.fundamental.rule.linking.IdentifierParentLinking;
import nl.tudelft.pds.granula.modeller.fundamental.model.operation.ConcreteOperationModel;
import nl.tudelft.pds.granula.modeller.fundamental.rule.derivation.ColorDerivation;
import nl.tudelft.pds.granula.modeller.fundamental.rule.visual.MainInfoTableVisualization;
import nl.tudelft.pds.granula.modeller.giraph.GiraphType;

import java.util.ArrayList;

public class BspWorkerPrepSuperstep extends ConcreteOperationModel {

    public BspWorkerPrepSuperstep() {
        super(GiraphType.BspWorker, GiraphType.PrepSuperstep);
    }

    public void loadRules() {
        super.loadRules();
        addLinkingRule(new IdentifierParentLinking(GiraphType.GlobalCoordinator, GiraphType.Unique, GiraphType.GlobalSuperstep, GiraphType.Equal));
        addInfoDerivation(new ColorDerivation(1, GiraphType.ColorGrey));
//         RecordInfoDerivation receivedMsgReqsVolume = new RecordInfoDerivation(1, "ReceivedMsgReqsVolume");
//        addInfoDerivation(receivedMsgReqsVolume);
//        receivedMsgReqsVolume.setDescription("[ReceivedMsgReqsVolume] is the volume of all worker message requests received by a BspWorker in a superstep. " +
//                "This value is aggregated from WorkerRequestServerHandler.processRequest() -> (WritableRequest) request).getType() == RequestType.SEND_WORKER_MESSAGES_REQUEST -> .getSerializedSize() ");

        addInfoDerivation(new SummaryDerivation(10));
        addVisualDerivation(new MainInfoTableVisualization(1,
                new ArrayList<String>() {{
//                    add("SentMsgs");
                }}));
    }

    protected class SummaryDerivation extends BasicSummaryDerivation {

        public SummaryDerivation(int level) { super(level); }

        @Override
        public boolean execute() {
            Operation operation = (Operation) entity;
            String summary = String.format("The [%s] operation is executed before the Computation and the MessageSend operation in each superstep, " +
                    "which involves swapping the message store, resolving graph mutation, " +
                    "exchanging vertex partition(?) and gathering global graph statistics etc. ", operation.getName());
            summary += getBasicSummary(operation);

            SummaryInfo summaryInfo = new SummaryInfo("Summary");
            summaryInfo.setValue("A Summary");
            summaryInfo.addSummary(summary, new ArrayList<Source>());
            operation.addInfo(summaryInfo);
            return  true;
        }
    }

}
