package christian.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import christian.network.fragment.ChurchDialogFragment;
import christian.network.interfaces.APIServiceInterface;
import christian.network.adapters.ChurchListAdapter;
import christian.network.entity.Church;
import christian.network.entity.User;
import christian.network.responses.AllChurchResponse;
import christian.network.responses.CommonResponse;
import christian.network.utils.ApplicationUtility;
import christian.network.utils.UserNChurchUtils;
import christian.network.utils.StaticData;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class SelectchurchActivity extends AppCompatActivity implements ChurchListAdapter.OnFollowChangedListener, ChurchListAdapter.OnChurchDetailsPressedListener, ChurchDialogFragment.OnChurchFollowedListener {

    EditText etSearch;
    ListView lvChurches;
    Button btnProceed;

    //Web Service
    Retrofit retrofit;
    APIServiceInterface apiService;

    ChurchListAdapter churchListAdapter;

    String user_id, church_id;
    Church followedChurch;
    boolean hasFollowed = false;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectchurch);
        context = this;
        initUI();
        addEditTextChangeListener();
        setUserAndChurchId();
        initWebService();
        getAllChurches();
    }

    private void initUI() {

        etSearch = (EditText) findViewById(R.id.etSearch);
        lvChurches = (ListView) findViewById(R.id.lvChurches);
        btnProceed = (Button) findViewById(R.id.btnProceed);

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followChurchAndProceed();
            }
        });

    }

    private void addEditTextChangeListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count < before) {
                    // We're deleting char so we need to reset the adapter data
                    churchListAdapter.resetData();
                }

                churchListAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setUserAndChurchId() {
        user_id = getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE).getString(StaticData.USER_ID, "");
        church_id = getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE).getString(StaticData.USER_ID, "");
    }

    private void initWebService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(StaticData.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIServiceInterface.class);
    }

    private void getAllChurches() {
        if (ApplicationUtility.checkInternet(context)) {
            Call<AllChurchResponse> call = apiService.getAllChurch(StaticData.AUTH_TOKEN);
            call.enqueue(allChurchResponseCallback);
        } else {
            ApplicationUtility.openNetworkDialog(SelectchurchActivity.this);
        }
    }

    Callback<AllChurchResponse> allChurchResponseCallback = new Callback<AllChurchResponse>() {

        @Override
        public void onResponse(Response<AllChurchResponse> response, Retrofit retrofit) {
            if (response.body().isSuccess()) {
                setChurchList(response.body().getChurch());
            } else {
                Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Throwable t) {

        }
    };

    private void setChurchList(ArrayList<Church> churches) {
        churches = addFollowAttribute(churches);
        churchListAdapter = new ChurchListAdapter(context, R.layout.church_row, churches, user_id);
        churchListAdapter.notifyDataSetChanged();
        lvChurches.setAdapter(churchListAdapter);
    }

    private ArrayList<Church> addFollowAttribute(ArrayList<Church> churches) {

        for (Church church : churches
                ) {

//            if(church.getChurch_id().equalsIgnoreCase(church_id)){
            church.setIsFollowed(false);
//            }else{
//                church.setIsFollowed(false);
//            }
        }

        return churches;
    }

    private void followChurchAndProceed() {
        if (hasFollowed) {
            String jsonParam = UserNChurchUtils.prepareChurchJsonParam(user_id, followedChurch.getChurch_id());
            Log.e("JSONParam", jsonParam);
            APIServiceInterface apiService = retrofit.create(APIServiceInterface.class);
            if (ApplicationUtility.checkInternet(context)) {
                Call<CommonResponse> call = apiService.followChurch(jsonParam, StaticData.AUTH_TOKEN);
                call.enqueue(followChurchResponse);
            } else {
                ApplicationUtility.openNetworkDialog((SelectchurchActivity) context);
            }
        } else {
            Toast.makeText(context, "Follow a church first", Toast.LENGTH_SHORT).show();
        }
    }

    Callback<CommonResponse> followChurchResponse = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Response<CommonResponse> response, Retrofit retrofit) {
            Log.e("Response", response.body().getMessage());
            if (response.body().isSuccess()) {
                saveChurchInfoInSession();
                Intent mIntent = new Intent(context, FollowUsersActivity.class);
                User user = getIntent().getParcelableExtra(StaticData.USER);
                mIntent.putExtra(StaticData.USER, user);
                startActivity(mIntent);
                finish();
            } else {
                Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            Toast.makeText(context, "Failed adding church", Toast.LENGTH_SHORT).show();
        }
    };

    private void saveChurchInfoInSession() {
        SharedPreferences sharedPreferences = getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(StaticData.CHURCH_ID, followedChurch.getChurch_id());
        editor.putString(StaticData.CHURCH_NAME, followedChurch.getChurch_name());
        editor.putString(StaticData.CHURCH_IMAGE, followedChurch.getImage_pp());
        editor.putString(StaticData.CHURCH_CP, followedChurch.getImage_cover());
        editor.putString(StaticData.CHURCH_DES, followedChurch.getDescription());
        editor.commit();
    }

    @Override
    public void onFollowChanged(Church church, boolean hasFollowed) {
        followedChurch = church;
        this.hasFollowed = hasFollowed;
    }

    @Override
    public void onChurchDetailsPressed(Church church) {
        FragmentManager fm = getSupportFragmentManager();
        ChurchDialogFragment churchDialogFragment = ChurchDialogFragment.newInstance(church);
        churchDialogFragment.show(fm, "fragment_edit_name");
    }

    @Override
    public void onChurchFollowed(Church church) {
        followedChurch = church;
        hasFollowed = true;
        followChurchAndProceed();
    }
}
