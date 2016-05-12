package christian.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import christian.network.adapters.FeedsAdapter;
import christian.network.entity.User;
import christian.network.interfaces.APIServiceInterface;
import christian.network.responses.CommonResponse;
import christian.network.entity.Feed;
import christian.network.responses.ProfileInfoResponse;
import christian.network.utils.ApplicationUtility;
import christian.network.utils.StaticData;
import christian.network.utils.UserNChurchUtils;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class ProfileActivity extends AppCompatActivity implements FeedsAdapter.OnCommentClickedListener {

    ImageView ivChurchCp, ivGetPhoto, ivPost, ivFollow, ivProfileSettings;
    CircleImageView civProfilePhoto, civProfileImage;
    TextView tvUserName, tvChurchName, tvEmail, tvAddress, tvContactNo, tvWriteStatusLabel;
    EditText etWritePost;
    RelativeLayout rlProgressLayout, rlEditFeed;
    Toolbar toolbar;
    RecyclerView rvUserFeeds;

    SharedPreferences preferences;

    String user_id, prefUserId;
    boolean is_followed = false;
    User user = new User();
    ArrayList<Feed> userFeeds = new ArrayList<>();

    FeedsAdapter profileFeedsAdapter;

    //Web Service
    Retrofit retrofit;
    APIServiceInterface apiService;

    boolean isMyPforile = false;
    boolean isUpdateFeed = false;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = this;

        initUI();
        initWebService();
        setPreference();
        getIntentData();
        initActionBar();
        addTextChangeListener();
        addClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
    }

    private void initUI() {
        ivChurchCp = (ImageView) findViewById(R.id.ivChurchCp);
        ivGetPhoto = (ImageView) findViewById(R.id.ivGetPhoto);
        civProfileImage = (CircleImageView) findViewById(R.id.civProfileImage);
        civProfilePhoto = (CircleImageView) findViewById(R.id.civProfilePhoto);
        toolbar = (Toolbar) findViewById(R.id.toolbar_feed);
        ivPost = (ImageView) toolbar.findViewById(R.id.toolbar_menu);
        ivPost.setVisibility(View.GONE);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvChurchName = (TextView) findViewById(R.id.tvChurchName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvContactNo = (TextView) findViewById(R.id.tvContactNo);
        etWritePost = (EditText) findViewById(R.id.etWritePost);
        rlProgressLayout = (RelativeLayout) findViewById(R.id.rlProgressLayout);
        rlProgressLayout.setVisibility(View.GONE);
        rvUserFeeds = (RecyclerView) findViewById(R.id.rvUserFeeds);
        rvUserFeeds.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvUserFeeds.setLayoutManager(llm);
        tvWriteStatusLabel = (TextView) findViewById(R.id.tvWriteStatusLabel);
        rlEditFeed = (RelativeLayout) findViewById(R.id.rlEditFeed);
        ivFollow = (ImageView) findViewById(R.id.ivFollow);
        ivProfileSettings = (ImageView) findViewById(R.id.ivProfileSettings);
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getUserName());
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

    private void addTextChangeListener() {
        etWritePost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = etWritePost.getText().toString();
                if (count == 0 || TextUtils.isEmpty(str)) {
                    ivPost.setVisibility(View.GONE);
                } else {
                    ivPost.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void addClickListeners() {
        ivPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etWritePost.getText())) {
                    etWritePost.setError("You must write something first");
                } else {
                    rlProgressLayout.setVisibility(View.VISIBLE);
                    etWritePost.setClickable(false);
                    ApplicationUtility.hideKeyboard(context, v);
                    postFeed();
                }
            }
        });

        ivFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUser(user);
            }
        });
        ivProfileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sIntent = new Intent(context, EditProfileActivity.class);
                startActivity(sIntent);
            }
        });
    }

    private void setPreference() {
        preferences = getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE);
    }

    private void getIntentData() {
        user_id = getIntent().getStringExtra(StaticData.USER_ID);
        is_followed = getIntent().getBooleanExtra(StaticData.USER_IS_FOLLOWED, false);
        isMyPforile = getIntent().getBooleanExtra(StaticData.USER_PROFILE, true);
    }

    private void getUserInfo() {
        prefUserId = preferences.getString(StaticData.USER_ID, "");

        if (isMyPforile) {
            user_id = prefUserId;
            getUserInfoFromPref();
            isUpdateFeed = false;
            getUserInfoFromServer();
        } else {
            hideUserComponents();
            isUpdateFeed = false;
            getUserInfoFromServer();
        }
    }

    private String getUserName() {

        if (isMyPforile) {
            String full_name = preferences.getString(StaticData.USER_FIRST_NAME, "") + " "
                    + preferences.getString(StaticData.USER_LAST_NAME, "");
            return full_name;
        } else {
            String full_name = getIntent().getStringExtra(StaticData.USER_FULL_NAME);
            return full_name;
        }
    }

    private void getUserInfoFromPref() {
        user.setFirst_name(preferences.getString(StaticData.USER_FIRST_NAME, "User"));
        user.setLast_name(preferences.getString(StaticData.USER_LAST_NAME, "Name"));
        user.setAddress(preferences.getString(StaticData.USER_ADDRESS, ""));
        user.setContact_no(preferences.getString(StaticData.USER_CONTACT, ""));
        user.setEmail(preferences.getString(StaticData.USER_EMAIL, ""));
        Log.e("Image", preferences.getString(StaticData.USER_IMAGE, ""));
        user.setImage(preferences.getString(StaticData.USER_IMAGE, "").substring(22));
        user.setChurch_name(preferences.getString(StaticData.CHURCH_NAME, ""));
        Log.e("Image", preferences.getString(StaticData.CHURCH_CP, ""));
        user.setImage_cover(preferences.getString(StaticData.CHURCH_CP, ""));
        Log.e("Church CP", user.getImage_cover());
        ivFollow.setVisibility(View.GONE);
        ivProfileSettings.setVisibility(View.VISIBLE);
        setDataInUI();
    }

    private void hideUserComponents() {
        ivFollow.setVisibility(View.VISIBLE);
        ivProfileSettings.setVisibility(View.GONE);
        tvWriteStatusLabel.setVisibility(View.GONE);
        rlEditFeed.setVisibility(View.GONE);
        rlProgressLayout.setVisibility(View.GONE);
    }

    private void getUserInfoFromServer() {
        if (ApplicationUtility.checkInternet(context)) {
            HashMap<String, String> hmParams = new HashMap<String, String>();
            hmParams.put(StaticData.USER_ID, user_id);
            String jsonParam = UserNChurchUtils.prepareJsonParam(hmParams);
            Log.e("JsonParam", jsonParam);
            Call<ProfileInfoResponse> call = apiService.getProfileInfo(jsonParam, StaticData.AUTH_TOKEN);
            call.enqueue(getProfileResponse);
        } else {
            ApplicationUtility.openNetworkDialog(this);
        }
    }

    private void setDataInUI() {
        tvUserName.setText(user.getFirst_name() + " " + user.getLast_name());
        tvChurchName.setText(user.getChurch_name());
        checkTextTakeAction(user.getEmail(), tvEmail);
        checkTextTakeAction(user.getContact_no(), tvContactNo);
        checkTextTakeAction(user.getAddress(), tvAddress);
        Log.e("IMAGE_CP", user.getImage_cover());
        Log.e("IMAGE_PP", user.getImage());
        Picasso.with(context).load(user.getProfileImage(user.getUser_id())).error(R.drawable.profile_thumb).placeholder(R.drawable.profile_thumb).into(civProfileImage);
        Picasso.with(context).load(user.getProfileImage(user.getUser_id())).error(R.drawable.profile_thumb).placeholder(R.drawable.profile_thumb).into(civProfilePhoto);
        Picasso.with(context).load(user.getImage_cover()).into(ivChurchCp);
        if (is_followed) {
            setFollowing();
        } else {
            setFollow();
        }
    }

    private void setFollowing() {
        ivFollow.setImageResource(R.drawable.ic_profile_following);
    }

    private void setFollow() {
        ivFollow.setImageResource(R.drawable.ic_profile_follow);
    }

    private void checkTextTakeAction(String text, TextView tvText) {
        if (TextUtils.isEmpty(text)) {
            tvText.setVisibility(View.GONE);
        } else {
            tvText.setText(text);
        }
    }

    private void prepareData(ProfileInfoResponse profileInfoResponse) {
        user = profileInfoResponse.getUser();
        userFeeds = modifyFeeds(profileInfoResponse.getPost());
        profileFeedsAdapter = new FeedsAdapter(userFeeds, context, this);
        rvUserFeeds.setAdapter(profileFeedsAdapter);
        if (!isUpdateFeed) {
            setDataInUI();
        }
    }

    private ArrayList<Feed> modifyFeeds(ArrayList<Feed> feeds) {
        for (Feed feed :
                feeds) {
            feed.setChurch_name(user.getChurch_name());
            feed.setAddress(user.getAddress());
            feed.setImage(user.getImage().substring(22));
            feed.setContact_no(user.getContact_no());
            feed.setFirst_name(user.getFirst_name());
            feed.setLast_name(user.getLast_name());
        }
        return feeds;
    }

    private void postFeed() {
        if (ApplicationUtility.checkInternet(context)) {
            HashMap<String, String> hmParams = new HashMap<String, String>();
            hmParams.put(StaticData.USER_ID, user_id);
            hmParams.put(StaticData.TYPE, "member");                //Must be dynamic
            hmParams.put(StaticData.POST, etWritePost.getText().toString());
            hmParams.put(StaticData.IMG_URL, "");
            String jsonParam = UserNChurchUtils.prepareJsonParam(hmParams);
            Log.e("JsonParam", jsonParam);
            Call<CommonResponse> call = apiService.postUserFeed(jsonParam, StaticData.AUTH_TOKEN);
            call.enqueue(userPostResponse);
        } else {
            rlProgressLayout.setVisibility(View.GONE);
            etWritePost.setClickable(true);
            etWritePost.setTextColor(getResources().getColor(R.color.black));
            ApplicationUtility.openNetworkDialog(this);
        }
    }

    private void followUser(User user) {
        if (ApplicationUtility.checkInternet(context)) {
            HashMap<String, String> hmParams = new HashMap<String, String>();
            hmParams.put(StaticData.USER_ID, prefUserId);
            hmParams.put(StaticData.FOLLOW_ID, user_id);
            String jsonParam = UserNChurchUtils.prepareJsonParam(hmParams);
            Log.e("PastorJsonParam", jsonParam);
            Call<CommonResponse> call;
            if (user.isFollowed()) {
                call = apiService.unFollowUser(jsonParam, StaticData.AUTH_TOKEN);
            } else {
                call = apiService.followUser(jsonParam, StaticData.AUTH_TOKEN);
            }
            call.enqueue(followUserResponse);
        } else {
            ApplicationUtility.openNetworkDialog(this);
        }
    }

    Callback<CommonResponse> followUserResponse = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Response<CommonResponse> response, Retrofit retrofit) {
            if (response.body().isSuccess()) {
                if (user.isFollowed()) {
                    setFollow();
                } else {
                    setFollowing();
                }
            } else {
                setFollowing();
                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Throwable t) {

        }
    };

    Callback<ProfileInfoResponse> getProfileResponse = new Callback<ProfileInfoResponse>() {
        @Override
        public void onResponse(Response<ProfileInfoResponse> response, Retrofit retrofit) {
            if (response.body().isSuccess()) {
                rlProgressLayout.setVisibility(View.GONE);
                prepareData(response.body());
            } else {
                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            Toast.makeText(context, "Check your internet connection.", Toast.LENGTH_SHORT).show();
        }
    };

    Callback<CommonResponse> userPostResponse = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Response<CommonResponse> response, Retrofit retrofit) {
//            rlProgressLayout.setVisibility(View.GONE);
            isUpdateFeed = true;
            rlProgressLayout.setVisibility(View.GONE);
            getUserInfoFromServer();
            etWritePost.setClickable(true);
            if (response.body().isSuccess()) {
                etWritePost.setText("");
            } else {
                etWritePost.setClickable(true);
                etWritePost.setTextColor(getResources().getColor(R.color.black));
                Toast.makeText(context, "Try Again!!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            rlProgressLayout.setVisibility(View.GONE);
            etWritePost.setClickable(true);
            etWritePost.setTextColor(getResources().getColor(R.color.black));
            Toast.makeText(context, "Problem updating post. Please try again.", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onCommentClicked(String post_id, int noOfLikes) {
        Intent cIntent = new Intent(context, CommentActivity.class);
        cIntent.putExtra(StaticData.POST_ID, post_id);
        cIntent.putExtra(StaticData.NO_OF_LIKES, noOfLikes);
        startActivity(cIntent);
    }
}
