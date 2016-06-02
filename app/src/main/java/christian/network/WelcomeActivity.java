package christian.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import christian.network.entity.User;
import christian.network.fragment.WelcomeScreen1;
import christian.network.fragment.WelcomeScreen3;
import christian.network.interfaces.APIServiceInterface;
import christian.network.utils.ApplicationUtility;
import christian.network.fragment.WelcomeScreen2;
import christian.network.adapters.WelcomePagerAdapter;
import christian.network.responses.SignUpResponse;
import christian.network.utils.StaticData;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class WelcomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    TextView tvGoogle, tvFacebook;
    ViewPager vpWelcome;
    CircleIndicator circleIndicator;
    RelativeLayout rlProgress;//, rlPagerContainer;

    //Facebok
    CallbackManager callbackManager;
    LoginManager loginManager;
    //    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;

    //Google
    GoogleApiClient mGoogleApiClient;
    private static final String TAG = "WelcomeActivity";
    private static final int RC_SIGN_IN = 9001;

    //Web Service
    Retrofit retrofit;
    APIServiceInterface apiService;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean isFirstTime = true;
    String fcmToken = "";

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        context = this;
        
        checkInternet();
        FacebookSdk.sdkInitialize(context);

        initSharePref();
//        initGoogleSignIn();
        initWebService();
        initUI();
    }

    private void checkInternet() {
        if (!ApplicationUtility.checkInternet(context)) {
            //Open Network Settings
            ApplicationUtility.openNetworkDialog(this);
        }
    }

    private void initSharePref() {
        sharedPreferences = getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isFirstTime = sharedPreferences.getBoolean(StaticData.FIRST_TIME, true);
    }

    private void initGoogleSignIn() {
        String serverClientId = getString(R.string.google_server_client_id);
        Scope scopeProfile = new Scope(Scopes.PROFILE);
        Scope scopeEmail = new Scope(Scopes.EMAIL);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(serverClientId)
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void initUI() {
        tvGoogle = (TextView) findViewById(R.id.tvGoogle);
        loginManager = LoginManager.getInstance();
        tvFacebook = (TextView) findViewById(R.id.tvFacebook);
        tvFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(context, LoginActivity.class));
                if (ApplicationUtility.checkInternet(context)) {
                    loginByFacebook();
                } else {
                    ApplicationUtility.openNetworkDialog(WelcomeActivity.this);
                }
            }
        });

        tvGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(context, SignupActivity.class));
                if (ApplicationUtility.checkInternet(context)) {
//                    loginByGoogle();
                    fcmToken = sharedPreferences.getString(StaticData.REG_ID,"");
                    Log.e("TOKEN",fcmToken);
                    Toast.makeText(context, "Coming Soon!", Toast.LENGTH_SHORT).show();
                } else {
                    ApplicationUtility.openNetworkDialog(WelcomeActivity.this);
                }
            }
        });

        vpWelcome = (ViewPager) findViewById(R.id.viewPagerWelcome);
        circleIndicator = (CircleIndicator) findViewById(R.id.indicatorWelcome);

        vpWelcome.setAdapter(new WelcomePagerAdapter(getSupportFragmentManager(), getFragments()));
        circleIndicator.setViewPager(vpWelcome);

        rlProgress = (RelativeLayout) findViewById(R.id.rlProgress);
        rlProgress.setVisibility(View.GONE);
//        rlPagerContainer = (RelativeLayout) findViewById(R.id.rlPagerContainer);
//        initAccesssTokenTracker();
    }

    private void initWebService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(StaticData.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIServiceInterface.class);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new WelcomeScreen1());
        fragments.add(new WelcomeScreen2());
        fragments.add(new WelcomeScreen3());

        return fragments;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Integrating Facebook Sign In Starts >
    private void loginByFacebook() {
        rlProgress.setVisibility(View.GONE);
        callbackManager = CallbackManager.Factory.create();
        ArrayList<String> permissions = new ArrayList<String>();
        permissions.add("email");
        loginManager.logInWithReadPermissions(this, permissions);
        loginManager.registerCallback(callbackManager, facebookCallbackManager);

    }

    FacebookCallback<LoginResult> facebookCallbackManager = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            rlProgress.setVisibility(View.VISIBLE);
            accessToken = loginResult.getAccessToken();
            GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                    Log.d("FacebookData", jsonObject.toString());
                    Bundle bundle = ApplicationUtility.getFacebookData(jsonObject, accessToken);
                    signUpUser(bundle, "facebook");
                }
            });
            requestToExecuteAPI(request);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };

    private void requestToExecuteAPI(GraphRequest request) {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, middle_name, last_name, email, gender, birthday, location"); // Facebook permission parameters
        request.setParameters(parameters);
        request.executeAsync();
    }
    // Facebook Signin ended

    private void signUpUser(Bundle bundleString, String authType) {
        fcmToken = sharedPreferences.getString(StaticData.REG_ID,"");
        String jSonParam = ApplicationUtility.getSignUpJSONParam(bundleString, authType, fcmToken);
        String imgUrl = bundleString.getString(StaticData.USER_IMAGE);
        String link = bundleString.getString(StaticData.ACCESSTOKEN_URL);
        try {
            jSonParam = URLEncoder.encode(jSonParam, "UTF-8");
            Log.e("JSONPARAM", jSonParam);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Call<SignUpResponse> call = apiService.signUpUser(jSonParam, StaticData.AUTH_TOKEN, imgUrl, link);
        call.enqueue(signUpResponseCallback);
    }

    Callback<SignUpResponse> signUpResponseCallback = new Callback<SignUpResponse>() {
        @Override
        public void onResponse(Response<SignUpResponse> response, Retrofit retrofit) {
            if(response.body().isSuccess()) {
                Log.e("RESPONSE", response.body().getMessage());
                processSignIn(response.body());
                rlProgress.setVisibility(View.GONE);
            }else{
                Log.e("RESPONSE", response.body().getMessage());
            }
        }

        @Override
        public void onFailure(Throwable t) {
            Toast.makeText(context, "Problem connecting server. Try again", Toast.LENGTH_SHORT).show();
            rlProgress.setVisibility(View.GONE);
        }
    };

    //Integrating Google
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void loginByGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.e(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e(TAG, acct.getDisplayName());
            Log.e(TAG, acct.getEmail());
            Log.e(TAG, acct.getId());
            Log.e(TAG, acct.getIdToken());
            Log.e(TAG, acct.getServerAuthCode());
            Log.e(TAG, acct.getPhotoUrl().toString());
        } else {
            // Signed out, show unauthenticated UI.
            Log.e(TAG, "Result not Success");
        }
    }

    private void processSignIn(SignUpResponse response) {
        Log.e("USER_ID",response.getUser().getUser_id());
        Bundle responseBundle = ApplicationUtility.getResponseBundle(response);
        if (response.isSuccess() && response.getUser().getChurch_id().equalsIgnoreCase("0")) {
            //Save User information
            saveUserSession(response.getUser());
            editor.commit();
            //Go to Church selection page
            Intent cIntent = new Intent(context, SelectchurchActivity.class);
            cIntent.putExtra(StaticData.USER, response.getUser());
            startActivity(cIntent);
            finish();
        } else if (response.isSuccess() && !response.getUser().getChurch_id().equalsIgnoreCase("0")) {
            //Save User information
            saveUserSession(response.getUser());
            //Go to Feeds Page
            editor.commit();
            Intent mIntent = new Intent(context, MainActivity.class);
//                mIntent.putExtra(StaticData.USER_DATA, responseBundle);
            startActivity(mIntent);
            finish();
        } else {
            Toast.makeText(context, "Try Again.. It seems we cannot autenticate your account from server", Toast.LENGTH_LONG).show();
        }
    }

    private void saveUserSession(User user) {
        editor.putString(StaticData.ID, user.getId());
        editor.putString(StaticData.USER_ID, user.getUser_id());
        editor.putString(StaticData.CHURCH_ID, user.getChurch_id());
        editor.putString(StaticData.USER_FIRST_NAME, user.getFirst_name());
        editor.putString(StaticData.USER_LAST_NAME, user.getLast_name());
        editor.putString(StaticData.USER_EMAIL, user.getEmail());
        editor.putString(StaticData.USER_CONTACT, user.getContact_no());
        editor.putString(StaticData.USER_IMAGE, user.getImage());
        editor.putString(StaticData.USER_ADDRESS, user.getAddress());
        editor.putString(StaticData.TYPE, user.getType());
        editor.putBoolean(StaticData.SESSION, true);
        editor.putBoolean(StaticData.FIRST_TIME, false);
//        editor.commit();
    }

}
