/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package christian.network.services;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import christian.network.R;
import christian.network.utils.StaticData;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private static final String FRIENDLY_ENGAGE_TOPIC = "friendly_engage";

    /**
     * The Application's current Instance ID token is no longer valid and thus a new one must be requested.
     */
    public void onTokenRefresh() {
        // If you need to handle the generation of a token, initially or
        // after a refresh this is where you should do that.
        String token = "";
        try {
            token = FirebaseInstanceId.getInstance().getToken(getResources().getString(R.string.fcm_sender_id),"FCM");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "FCM Token: " + token);
        saveToken(token);

        // Once a token is generated, we subscribe to topic.
        FirebaseMessaging.getInstance()
                .subscribeToTopic(FRIENDLY_ENGAGE_TOPIC);
    }

    private void saveToken(String token){
        SharedPreferences preferences = getSharedPreferences(StaticData.APP_PREFERENCE,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(StaticData.REG_ID,token);
        editor.commit();
        Log.e("TOKEN", token);
    }

}
