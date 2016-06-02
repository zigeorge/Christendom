package christian.network;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import christian.network.adapters.PagerAdapter;
import christian.network.entity.User;
import christian.network.interfaces.APIServiceInterface;
import christian.network.responses.AllUsersResponse;
import christian.network.utils.ApplicationUtility;
import christian.network.fragment.UserFragment;
import christian.network.utils.StaticData;
import christian.network.utils.UserNChurchUtils;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class FollowUsersActivity extends AppCompatActivity {

    //    ListView lvPastores;
    Button btnContinue;
    ViewPager vpUsers;
    PagerTabStrip ptsUserPager;
    Toolbar toolbar;
    RelativeLayout rlProgress;

    //Web Service
    Retrofit retrofit;
    APIServiceInterface apiService;
    ArrayList<User> members, pastors;
    String user_id;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_follow_pastore);
        context = this;

        initUI();
        initActionBar();
        initWebService();
        setUserId();
        getAllUsers();
    }

    private void initUI() {
        btnContinue = (Button) findViewById(R.id.btnContinue);
        vpUsers = (ViewPager) findViewById(R.id.vpUsers);
        ptsUserPager = (PagerTabStrip) findViewById(R.id.pager_header);
        toolbar = (Toolbar) findViewById(R.id.tbFollow);
        rlProgress = (RelativeLayout) findViewById(R.id.rlProgress);
        rlProgress.setVisibility(View.VISIBLE);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(context, MainActivity.class);
                startActivity(mIntent);
                finish();
            }
        });
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private void initWebService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(StaticData.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIServiceInterface.class);
    }

    private void setUserId(){
        user_id = getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE).getString(StaticData.USER_ID,"");
    }

    private void getAllUsers() {
        if (ApplicationUtility.checkInternet(context)) {
            String jsonParam = UserNChurchUtils.prepareUserJsonParam(user_id);
            Log.e("PastorJsonParam", jsonParam);
            Call<AllUsersResponse> call = apiService.getAllUsers(jsonParam, StaticData.AUTH_TOKEN);
            call.enqueue(allUserResponseCallback);
        } else {
            rlProgress.setVisibility(View.GONE);
            ApplicationUtility.openNetworkDialog(FollowUsersActivity.this);
        }
    }

    private void setUserPager() {
        vpUsers.setAdapter(new PagerAdapter(getSupportFragmentManager(), getFragments(), false));
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        Log.e("MEMBER", members.size() + "");
        Log.e("PASTOR", pastors.size() + "");
        fragments.add(UserFragment.newInstance(pastors, "Pastors"));
        fragments.add(UserFragment.newInstance(members, "Members"));

        return fragments;
    }

    Callback<AllUsersResponse> allUserResponseCallback = new Callback<AllUsersResponse>() {
        @Override
        public void onResponse(Response<AllUsersResponse> response, Retrofit retrofit) {
            rlProgress.setVisibility(View.GONE);
            Log.e("Response", response.body().getMessage());
            if (response.body().getMessage().equalsIgnoreCase("success")) {
                members = response.body().getAll_member_not_follow();
                pastors = response.body().getAll_pastor_not_follow();
                addFollowingStatus();
                Log.e("MEMBER", members.size() + "");
                Log.e("PASTOR", pastors.size() + "");
                setUserPager();
            } else {
                Toast.makeText(context, "Something is wrong. Please restart application", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            rlProgress.setVisibility(View.GONE);
            Toast.makeText(context, "Failed to get all users, please try again by restarting from bgining", Toast.LENGTH_SHORT).show();
        }
    };

    private void addFollowingStatus() {
        for (User member : members
                ) {
            member.setIsFollowed(false);
        }
        for (User pastor :
                pastors) {
            pastor.setIsFollowed(false);
        }
    }

}
