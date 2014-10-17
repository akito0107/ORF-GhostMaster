package old.lib.commonlib.tasks;

import old.lib.commonlib.data.OffloadableData;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by aqram on 10/2/14.
 */
public interface OffloadableTask extends Serializable {
    public OffloadableData run(OffloadableData data);
    public String getName();
}
