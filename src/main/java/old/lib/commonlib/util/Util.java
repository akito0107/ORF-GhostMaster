package old.lib.commonlib.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by aqram on 10/2/14.
 */
public class Util {

    public static String makeSHA1Hash(String input){
        MessageDigest md = null;

        try{
            md = MessageDigest.getInstance("SHA1");
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        md.reset();
        byte[] buffer = input.getBytes();
        md.update(buffer);
        byte[] digest = md.digest();

        String hexStr = "";
        for (int i = 0; i < digest.length; i++) {
            hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return hexStr;
    }

}
