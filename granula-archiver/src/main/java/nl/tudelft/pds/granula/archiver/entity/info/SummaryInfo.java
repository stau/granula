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

import nl.tudelft.pds.granula.archiver.entity.Identifier;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import java.util.List;

/**
 * Created by wing on 16-3-15.
 */
public class SummaryInfo extends Info {
    String summary;

    public SummaryInfo(String name) {
        super(name, Identifier.SummaryInfo);
    }

    public void addSummary(String summary, List<Source> sources) {
        this.summary = summary;

        for (Source source : sources) {
            addSource(source);
        }
    }

    @XmlElement(name="Summary")
    public String getSummary() {
        return summary;
    }

    @XmlElements({
            @XmlElement(name="Source", type=InfoSource.class),
            @XmlElement(name="Source", type=RecordSource.class)
    })
    @XmlElementWrapper(name="Sources")
    public List<Source> getSources() {
        return sources;
    }

    public String exportBasic() {
        return String.format("<Info name=\"%s\" value=\"%s\" type=\"%s\" uuid=\"%s\">", name, value, type, uuid);

    }

    public String export() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("<Info name=\"%s\" value=\"%s\" type=\"%s\" uuid=\"%s\">", name, value, type, uuid));

        stringBuilder.append(String.format("<Description>%s</Description>", description));

        stringBuilder.append("<Sources>");
        for (Source source : sources) {
            stringBuilder.append(source.export());
        }
        stringBuilder.append("</Sources>");

        stringBuilder.append(String.format("<Summary>%s</Summary>", summary));

        stringBuilder.append("</Info>");
        return stringBuilder.toString();
    }
}
