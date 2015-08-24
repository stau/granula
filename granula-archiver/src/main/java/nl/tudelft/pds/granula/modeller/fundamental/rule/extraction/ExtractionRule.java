package nl.tudelft.pds.granula.modeller.fundamental.rule.extraction;

import nl.tudelft.pds.granula.archiver.record.Record;
import nl.tudelft.pds.granula.modeller.fundamental.rule.Rule;

import java.io.File;
import java.util.List;

/**
 * Created by wing on 21-8-15.
 */
public abstract class ExtractionRule extends Rule {

    public ExtractionRule(int level) {
        super(level);
    }

    public abstract List<Record> extractRecordFromFile(File file);

}
