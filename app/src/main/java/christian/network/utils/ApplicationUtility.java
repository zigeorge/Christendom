package christian.network.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import christian.network.R;
import christian.network.customcontrol.TouchImageView;
import christian.network.entity.User;
import christian.network.responses.SignUpResponse;

/**
 * Created by User on 29-Mar-16.
 */
public class ApplicationUtility {

    public static Bundle getFacebookData(JSONObject object, AccessToken accessToken) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");
            String accesstokenUrl = StaticData.FACEBOOK_ACCESSTOKEN_URL + accessToken.getToken() + "&format=json";
            Log.e(StaticData.ACCESSTOKEN_URL, accesstokenUrl);
            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=200");
                Log.e("profile_pic", profile_pic + "");
                bundle.putString(StaticData.USER_IMAGE, profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString(StaticData.USER_ID, id);
            if (object.has(StaticData.USER_FIRST_NAME))
                bundle.putString(StaticData.USER_FIRST_NAME, object.getString(StaticData.USER_FIRST_NAME));
            if (object.has(StaticData.USER_MIDDLE_NAME))
                bundle.putString(StaticData.USER_MIDDLE_NAME, object.getString(StaticData.USER_MIDDLE_NAME));
            if (object.has(StaticData.USER_LAST_NAME))
                bundle.putString(StaticData.USER_LAST_NAME, object.getString(StaticData.USER_LAST_NAME));
            if (object.has(StaticData.USER_EMAIL))
                bundle.putString(StaticData.USER_EMAIL, object.getString(StaticData.USER_EMAIL));
            if (object.has(StaticData.USER_GENDER))
                bundle.putString(StaticData.USER_GENDER, object.getString(StaticData.USER_GENDER));
            bundle.putString(StaticData.ACCESSTOKEN_URL, accesstokenUrl);
            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSignUpJSONParam(Bundle bundle, String authType) {
        String user_id = bundle.getString(StaticData.USER_ID);
        String email = bundle.getString(StaticData.USER_EMAIL);
        String first_name = bundle.getString(StaticData.USER_FIRST_NAME);
        String last_name = bundle.getString(StaticData.USER_LAST_NAME);
        String jSONParam = "{\"" + StaticData.USER_ID + "\":\"" + user_id + "\",\"auth_type\":\"" + authType + "\",\"" + StaticData.USER_EMAIL + "\":\"" + email + "\",\"" +
                StaticData.USER_FIRST_NAME + "\":\"" + first_name + "\",\"" + StaticData.USER_LAST_NAME + "\":\"" + last_name + "\",\"" + StaticData.USER_ADDRESS + "\":\"\",\"" +
                StaticData.USER_CONTACT + "\":\"\"}";
        return jSONParam;
    }

    public static Bundle getResponseBundle(SignUpResponse response) {
        Bundle bundle = new Bundle();
        User aUser = response.getUser();
        bundle.putString(StaticData.USER_ID, aUser.getUser_id());
        bundle.putString(StaticData.USER_FIRST_NAME, aUser.getFirst_name());
        bundle.putString(StaticData.USER_LAST_NAME, aUser.getLast_name());
        bundle.putString(StaticData.USER_EMAIL, aUser.getEmail());
        bundle.putString(StaticData.CHURCH_ID, aUser.getChurch_id());
        bundle.putString(StaticData.ID, aUser.getId());
        bundle.putString(StaticData.USER_IMAGE, aUser.getImage());
        bundle.putString(StaticData.USER_TYPE, aUser.getType());
        bundle.putString(StaticData.USER_CONTACT, aUser.getContact_no());
        bundle.putString(StaticData.USER_ADDRESS, aUser.getAddress());

        return bundle;

    }

    public static boolean checkInternet(Context context){
        ConnectivityManager check = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = check.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static void openNetworkDialog(final Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

// 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(activity.getString(R.string.con_message))
                .setTitle(activity.getString(R.string.con_title)).setCancelable(false).setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        }).setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        }).create();
        builder.show();
    }

    public static Date formatDate(String inputDate, SimpleDateFormat sdFormat) {
        Date dt = null;
        try {
            dt = sdFormat.parse(inputDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dt;
    }

    public static void hideKeyboard(Context context, View view){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void openScreenshotDialog(String imgUrl, Context con) {
        final Dialog dialog = new Dialog(con, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.screenshot_dialog);

        TouchImageView image = (TouchImageView) dialog.findViewById(R.id.ivScreenshot);
        Picasso.with(con).load(imgUrl).placeholder(R.drawable.progress_loading).into(image);

        ImageView ivCross = (ImageView) dialog.findViewById(R.id.ivCross);
        // if button is clicked, close the custom dialog
        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
