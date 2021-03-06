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

package nl.tudelft.pds.granula.archiver.entity.info;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wing on 26-2-15.
 */
@XmlRootElement(name="Source")
public class InfoSource extends Source {
    List<String> infoUuids;
    String infoUuid;

    public InfoSource() {
        this.name = "unspecified";
        this.type = "InfoSource";
        infoUuids = new ArrayList<>();
    }

    public InfoSource(String name, List<Info> infos) {
        this.type = "InfoSource";
        this.name = name;
        infoUuids = new ArrayList<>();
        for (Info info : infos) {
            infoUuids.add(info.getUuid());
        }

        String uuidsText = "";
        for (String infoUuid : infoUuids) {
            uuidsText += (uuidsText.length() == 0) ? infoUuid : ";" + infoUuid;
        }
        infoUuid = uuidsText;
    }

    public InfoSource(String name, Info info) {
        this.type = "InfoSource";
        this.name = name;
        infoUuids = new ArrayList<>();
        infoUuids.add(info.getUuid());

        String uuidsText = "";
        for (String infoUuid : infoUuids) {
            uuidsText += (uuidsText.length() == 0) ? infoUuid : ";" + infoUuid;
        }
        infoUuid = uuidsText;
    }

    public List<String> getInfoUuids() {
        return infoUuids;
    }

    @XmlAttribute
    public String getInfoUuid() {
        return infoUuid;
    }
}
