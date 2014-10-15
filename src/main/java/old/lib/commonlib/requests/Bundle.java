package old.lib.commonlib.requests;

import java.util.HashMap;

/**
 * Controll Channel内でやりとりされるデータ形式
 * Created by aqram on 10/15/14.
 *
 * APPID
 * SEQ
 * MES
 * を格納
 *
 */
public class Bundle {


    private HashMap<String, String> mMap;

    public Bundle(){
        mMap = new HashMap<String,String>();
    }

    public Bundle(HashMap<String, String> data){
        mMap = data;
    }

    public String getData(String key){
        return mMap.get(key);
    }

    public void putData(String key, String value){
        mMap.put(key, value);
    }
}
