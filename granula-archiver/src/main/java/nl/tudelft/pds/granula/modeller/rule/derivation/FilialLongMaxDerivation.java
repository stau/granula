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

package nl.tudelft.pds.granula.modeller.rule.derivation;

import nl.tudelft.pds.granula.archiver.entity.info.BasicInfo;
import nl.tudelft.pds.granula.archiver.entity.info.Info;
import nl.tudelft.pds.granula.archiver.entity.info.InfoSource;
import nl.tudelft.pds.granula.archiver.entity.info.Source;
import nl.tudelft.pds.granula.archiver.entity.operation.Operation;

import java.util.ArrayList;
import java.util.List;

public class FilialLongMaxDerivation extends DerivationRule {

    String infoName;
    String aggInfoName;
    String missionType;

    public FilialLongMaxDerivation(int level, String missionType, String infoName, String aggInfoName) {
        super(level);
        this.missionType = missionType;
        this.infoName = infoName;
        this.aggInfoName = aggInfoName;
    }

    @Override
    public boolean execute() {
        Operation operation = (Operation) entity;

        long max = Long.MIN_VALUE;
        List<Source> sources = new ArrayList<>();
        List<Info> usedInfos = new ArrayList<>();
        for (Operation suboperation : operation.getChildren()) {
            if(suboperation.getMission().getType().equals(missionType)) {
                Info info = suboperation.getInfo(infoName);
                max = Math.max(max, Long.parseLong(info.getValue()));;
                usedInfos.add(info);
            }
        }
        sources.add(new InfoSource(infoName, usedInfos));
        BasicInfo aggInfo = new BasicInfo(aggInfoName);

        long value = max;
        aggInfo.setDescription(String.format("[%s] is maximum of [%s]s of all children operation with mission type %s. ", aggInfoName, infoName, missionType));
        aggInfo.addInfo(String.valueOf(value), sources);
        operation.addInfo(aggInfo);

        return true;
    }
}
