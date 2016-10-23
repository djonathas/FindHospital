package com.example.catolica.findhospital;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by catolica on 23/10/16.
 */

public class MyFirebaseMessagingIDService extends FirebaseInstanceIdService {
    private static final String TAG = "firebase";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "Refreshed token: " + refreshedToken);
    }
}
