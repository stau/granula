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

import nl.tudelft.pds.granula.archiver.source.record.Record;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * Created by wing on 26-2-15.
 */
@XmlRootElement(name="Source")
public class RecordSource extends Source {

    String location;

    public RecordSource() {
        this.name = "unspecified";
        this.type = "RecordSource";
        location = "unspecified";
    }

    public RecordSource(String name, Record record) {
        this.name = name;
        this.type = "RecordSource";
        this.location = record.getRecordLocation().getLocation();
    }

    @XmlAttribute
    public String getLocation() {
        return location;
    }

}
