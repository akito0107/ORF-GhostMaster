package old.lib.commonlib.data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by aqram on 10/2/14.
 */
public class OffloadableData implements Serializable{

    private final String NAME_SPACE;
    private HashMap<String, double[]> mData;

    public OffloadableData(String ID, String seq){
        NAME_SPACE = ID + ":" + seq + "/";
        mData = new HashMap<String, double[]>();
    }

    public void putData(String name, double[] body){
        mData.put(NAME_SPACE + name, body);
    }

    public double[] getData(String name){
        return mData.get(NAME_SPACE + "name");
    }
}
