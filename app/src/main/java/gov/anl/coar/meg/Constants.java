package gov.anl.coar.meg;

/**
 * Created by greg on 2/28/16.
 */
public class Constants {
    public static final int ENCRYPTION_BITS = 2048;
    public static final String RSA = "RSA";
    public static final String SPONGY_CASTLE = "SC";
    // Eh... we can make it sound less like SSH in the future
    public static final String SECRETKEY_FILENAME = "id_rsa";
    public static final String PUBLICKEY_FILENAME = "id_rsa.pub";
    public static final String PHONENUMBER_FILENAME = "phone_number.txt";
    public static final String LOGIN_BUT_NO_KEY =
            "It seems like you still need to complete the MEG installation step";
    // Obviously this will change in the future
    public static final String MEG_API_URL = "http://grehm.us/megserver";
    public static final long INSTANCE_ID_RETRY_TIME = 5;
}
