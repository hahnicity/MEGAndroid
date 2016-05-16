package gov.anl.coar.meg.service;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import gov.anl.coar.meg.Util;
import gov.anl.coar.meg.pgp.MEGMessage;

/**
 * Created by greg on 3/6/16.
 */
public class GCMListenerService extends GcmListenerService {
    private static final String TAG = "GCMListenerService";

    @Override
    public void onMessageReceived(
            String from,
            Bundle data
    ) {
        try {
            String action = data.getString("action");
            Log.d(TAG, "message received from: " + from);
            Log.d(TAG, "message action: " + action);
            if (action.contains("decrypt")) {
                String messageId = data.getString("message_id");
                MEGMessage msg = MEGMessage.getEncryptedFromServer(messageId, this, getApplication());
                msg.performDecryptionFlow();
            } else if (action.contains("encrypt")) {
                String messageId = data.getString("message_id");
                MEGMessage msg = MEGMessage.getDecryptedFromServer(messageId, this, getApplication());
                msg.performEncryptionFlow();
            } else if (action.contains("revoke")) {
                Util.deleteAllFiles(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
