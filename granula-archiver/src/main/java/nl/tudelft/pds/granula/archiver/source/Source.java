package nl.tudelft.pds.granula.archiver.source;

import java.io.InputStream;
import java.util.List;

/**
 * Created by wing on 24-8-15.
 */
public abstract class Source {

    public abstract void verify();
    public abstract void load();

}
