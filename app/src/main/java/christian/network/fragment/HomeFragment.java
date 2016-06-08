package christian.network.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import christian.network.CreateFeedActivity;
import christian.network.R;
import christian.network.adapters.FeedsAdapter;
import christian.network.entity.Feed;
import christian.network.interfaces.APIServiceInterface;
import christian.network.responses.AllFeedsResponse;
import christian.network.utils.ApplicationUtility;
import christian.network.utils.StaticData;
import christian.network.utils.UserNChurchUtils;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class HomeFragment extends Fragment {

    TextView tvWritePost;
    RecyclerView rvFeeds;
    CircleImageView ivProfileImage;
    RelativeLayout rlProgress;

    Retrofit retrofit;
    APIServiceInterface apiService;
    ArrayList<Feed> feeds;
    String user_id, church_id;

    FeedsAdapter feedsAdapter;

    OnHomeFragmentResumed onHomeFragmentResumed;

    Context context;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Home");
        context = getActivity();
        setUserId();
        initWebService();

//        UserFragment.isCompanion = false;
        try {
            onHomeFragmentResumed = (OnHomeFragmentResumed) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFollowChangedListener");
        }
    }

    private void setUserId() {
        user_id = context.getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE).getString(StaticData.USER_ID, "");
        church_id = context.getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE).getString(StaticData.CHURCH_ID, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        setProfileImage();
        addTextOnClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();

        getAllFeeds();
    }

    private void initUI(View view) {
        tvWritePost = (TextView) view.findViewById(R.id.tvWritePost);
        ivProfileImage = (CircleImageView) view.findViewById(R.id.ivProfileImage);
        rvFeeds = (RecyclerView) view.findViewById(R.id.rvFeeds);
        rvFeeds.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvFeeds.setLayoutManager(llm);
        rlProgress = (RelativeLayout)view.findViewById(R.id.rlProgress);
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void setProfileImage() {
//        String img_url = getActivity().getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE).getString(StaticData.USER_IMAGE, "");
        String img_url = StaticData.FACEBOOK_IMAGE_URL+user_id+StaticData.FACEBOOK_IMAGE_SIZE;
        Picasso.with(context).load(img_url).error(R.drawable.profile_thumb).placeholder(R.drawable.profile_thumb).into(ivProfileImage);
    }

    private void addTextOnClickListeners() {
        tvWritePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWritePostActivity();
            }
        });
    }

    private void openWritePostActivity() {
        Intent cIntent = new Intent(context, CreateFeedActivity.class);
        cIntent.putExtra(StaticData.USER_ID, user_id);
        cIntent.putExtra(StaticData.CHURCH_ID, church_id);
        startActivity(cIntent);
    }

    private void initWebService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(StaticData.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIServiceInterface.class);
    }

    private void getAllFeeds() {
        if (ApplicationUtility.checkInternet(context)) {
            HashMap<String, String> hmParams = new HashMap<String, String>();
            hmParams.put(StaticData.USER_ID, user_id);
            hmParams.put(StaticData.CHURCH_ID, church_id);
            String jsonParam = UserNChurchUtils.prepareJsonParam(hmParams);
            Log.e("JsonParam", jsonParam);
            Call<AllFeedsResponse> call = apiService.getAllFeed(jsonParam, StaticData.AUTH_TOKEN);
            call.enqueue(allFeedsResponseCallback);
        } else {
            rlProgress.setVisibility(View.GONE);
            ApplicationUtility.openNetworkDialog((AppCompatActivity)getActivity());
        }
    }

    Callback<AllFeedsResponse> allFeedsResponseCallback = new Callback<AllFeedsResponse>() {
        @Override
        public void onResponse(Response<AllFeedsResponse> response, Retrofit retrofit) {
//            Log.e("Response Size",response.body().getPost().size()+"");/
            rlProgress.setVisibility(View.GONE);
            if (response.body().isSuccess()) {
                feeds = response.body().getPost();
                if (feeds != null) {
                    setFeedsAdapter();
                }
            } else {
                Toast.makeText(context, "You have no feed to display. Follow your friends and share your thoughts. You'll be able to see all the posts that your friends are sharing in this page.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            rlProgress.setVisibility(View.GONE);
            Toast.makeText(context, "There is a problem connecting to server, please restart application", Toast.LENGTH_SHORT).show();
        }
    };

    private void setFeedsAdapter() {
        feedsAdapter = new FeedsAdapter(feeds, context, (AppCompatActivity)getActivity());
        rvFeeds.setAdapter(feedsAdapter);
    }

    public interface OnHomeFragmentResumed {
        public void homeFragmentResumed();
    }

}
