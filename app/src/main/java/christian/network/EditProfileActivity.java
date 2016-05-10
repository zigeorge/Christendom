package christian.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

import christian.network.customcontrol.FloatingHintEditText;
import christian.network.entity.User;
import christian.network.interfaces.APIServiceInterface;
import christian.network.responses.CommonResponse;
import christian.network.utils.ApplicationUtility;
import christian.network.utils.StaticData;
import christian.network.utils.UserNChurchUtils;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class EditProfileActivity extends AppCompatActivity {

    ImageView ivProfilePhoto;
    FloatingHintEditText fhetFirstName, fhetLastName, fhetAddress, fhetContactNo;
    Button btnSubmit;
    Toolbar toolbar;

    //Web Service
    Retrofit retrofit;
    APIServiceInterface apiService;

    SharedPreferences preferences;
    User user = new User();

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        context = this;

        initUI();
        initActionBar();
        initWebService();
        addListenerForButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfoFromPref();
    }

    private void initUI() {
        ivProfilePhoto = (ImageView) findViewById(R.id.ivProfilePhoto);
        fhetFirstName = (FloatingHintEditText) findViewById(R.id.fhetFirstName);
        fhetLastName = (FloatingHintEditText) findViewById(R.id.fhetLastName);
        fhetAddress = (FloatingHintEditText) findViewById(R.id.fhetAddress);
        fhetContactNo = (FloatingHintEditText) findViewById(R.id.fhetContactNo);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private void initWebService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(StaticData.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIServiceInterface.class);
    }

    private void getUserInfoFromPref() {
        preferences = getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE);
        user.setUser_id(preferences.getString(StaticData.USER_ID, ""));
        user.setFirst_name(preferences.getString(StaticData.USER_FIRST_NAME, ""));
        user.setLast_name(preferences.getString(StaticData.USER_LAST_NAME, ""));
        user.setAddress(preferences.getString(StaticData.USER_ADDRESS, ""));
        user.setContact_no(preferences.getString(StaticData.USER_CONTACT, ""));
        user.setImage(preferences.getString(StaticData.USER_IMAGE, ""));
        setDataInUI();
    }

    private void setDataInUI() {
        checkAndSet(user.getFirst_name(), fhetFirstName);
        checkAndSet(user.getLast_name(), fhetLastName);
        checkAndSet(user.getAddress(), fhetAddress);
        checkAndSet(user.getContact_no(), fhetContactNo);
        Picasso.with(context).load(user.getImage()).placeholder(R.drawable.profile_thumb)
                .error(R.drawable.profile_thumb).into(ivProfilePhoto);
    }

    private void checkAndSet(String text, FloatingHintEditText floatingHintEditText) {
        if (!TextUtils.isEmpty(text)) {
            floatingHintEditText.setText(text);
        }
    }

    private void addListenerForButton() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFields()) {
                    submitChanges();
                }
            }
        });
    }

    private boolean checkFields() {
        boolean isValid = true;
        if (TextUtils.isEmpty(fhetFirstName.getText())) {
            fhetFirstName.setError("First Name required");
            isValid = false;
        }
        if (TextUtils.isEmpty(fhetLastName.getText())) {
            fhetLastName.setError("Last Name required");
            isValid = false;
        }
        return isValid;
    }

    private void submitChanges() {
        if (ApplicationUtility.checkInternet(context)) {
            HashMap<String, String> hmParams = new HashMap<String, String>();
            hmParams.put(StaticData.USER_ID, user.getUser_id());
            hmParams.put(StaticData.FIRSTNAME, fhetFirstName.getText().toString());
            hmParams.put(StaticData.LASTNAME, fhetLastName.getText().toString());
            hmParams.put(StaticData.USER_CONTACT, fhetAddress.getText().toString());
            hmParams.put(StaticData.USER_ADDRESS, fhetContactNo.getText().toString());
            hmParams.put(StaticData.TYPE,"all");
            String jsonParam = UserNChurchUtils.prepareJsonParam(hmParams);
            Log.e("JsonParam", jsonParam);
            Call<CommonResponse> call = apiService.editProfileInfo(jsonParam, StaticData.AUTH_TOKEN);
            call.enqueue(getProfileResponse);
        } else {
            ApplicationUtility.openNetworkDialog(this);
        }
    }

    private void updatePreference(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(StaticData.USER_FIRST_NAME,fhetFirstName.getText().toString());
        editor.putString(StaticData.USER_LAST_NAME,fhetLastName.getText().toString());
        editor.putString(StaticData.USER_CONTACT,fhetContactNo.getText().toString());
        editor.putString(StaticData.USER_ADDRESS,fhetAddress.getText().toString());
        editor.commit();
    }

    Callback<CommonResponse> getProfileResponse = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Response<CommonResponse> response, Retrofit retrofit) {
            if (response.body().isSuccess()) {
                updatePreference();
                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            Toast.makeText(context, "Problem connecting to server", Toast.LENGTH_SHORT).show();
        }
    };
}
