package old.lib.commonlib.requests;

import old.lib.commonlib.datatypes.GhostResponseTypes;

/**
 * Created by aqram on 10/15/14.
 */
public class GhostResponse {

    public final String STATUS;
    public final String REQUESTID;
    public final Bundle MESSAGE;

    public GhostResponse(String status, String reqId, Bundle mes){
        STATUS = status;
        REQUESTID = reqId;
        MESSAGE = mes;
    }
}
