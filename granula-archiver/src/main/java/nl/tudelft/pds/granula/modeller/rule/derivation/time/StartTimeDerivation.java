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

package nl.tudelft.pds.granula.modeller.rule.derivation.time;

import nl.tudelft.pds.granula.archiver.entity.info.BasicInfo;
import nl.tudelft.pds.granula.archiver.entity.info.Info;
import nl.tudelft.pds.granula.archiver.entity.info.RecordSource;
import nl.tudelft.pds.granula.archiver.entity.info.Source;
import nl.tudelft.pds.granula.archiver.entity.operation.Operation;
import nl.tudelft.pds.granula.archiver.source.record.Record;
import nl.tudelft.pds.granula.archiver.source.record.RecordInfo;
import nl.tudelft.pds.granula.modeller.rule.derivation.DerivationRule;

import java.util.ArrayList;
import java.util.List;

public class StartTimeDerivation extends DerivationRule {

    public StartTimeDerivation(int level) {
        super(level);
    }

    @Override
    public boolean execute() {
        Operation operation = (Operation) entity;

        Record startEventRecord = operation.getRecord("StartTime");
        long startime = Long.parseLong(startEventRecord.getAttr(RecordInfo.InfoValue));
        List<Source> sources = new ArrayList<>();
        sources.add(new RecordSource("StartTime", startEventRecord));

        BasicInfo info = new BasicInfo("StartTime");
        info.setDescription("The [StartTime] of an operation is retrieved from the [StartTime] record in the log.");
        info.addInfo(String.valueOf(startime), sources);
        operation.addInfo(info);
        return  true;
    }
}
