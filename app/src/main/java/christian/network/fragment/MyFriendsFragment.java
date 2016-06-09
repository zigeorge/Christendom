package christian.network.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import christian.network.R;
import christian.network.adapters.FriendListAdapter;
import christian.network.adapters.PagerAdapter;
import christian.network.entity.Friend;
import christian.network.entity.User;
import christian.network.interfaces.APIServiceInterface;
import christian.network.responses.AllUsersResponse;
import christian.network.responses.FriendsResponse;
import christian.network.utils.ApplicationUtility;
import christian.network.utils.StaticData;
import christian.network.utils.UserNChurchUtils;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFriendsFragment extends Fragment {

    private static final String ARG_PARAM1 = "user_list";
    private static final String ARG_PARAM2 = "title_text";

    RelativeLayout rlProgress;
    ListView lvMyFriends;

    Retrofit retrofit;
    APIServiceInterface apiService;
    ArrayList<Friend> friends;
    String user_id;

    Context context;

    public MyFriendsFragment() {
        // Required empty public constructor
    }

    public static MyFriendsFragment newInstance(String user_id, String title) {
        MyFriendsFragment fragment = new MyFriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, user_id);
        args.putString(ARG_PARAM2, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        user_id = context.getSharedPreferences(StaticData.APP_PREFERENCE,Context.MODE_PRIVATE).getString(StaticData.USER_ID,"");
        getActivity().setTitle(getArguments().getString(ARG_PARAM2));
        initWebService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        getAllUsers();
    }

    private void initUI(View view) {
        rlProgress = (RelativeLayout) view.findViewById(R.id.rlProgress);
        rlProgress.setVisibility(View.VISIBLE);
        lvMyFriends = (ListView) view.findViewById(R.id.lvMyFriends);
    }

    private void initWebService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(StaticData.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIServiceInterface.class);
    }

    private void getAllUsers() {
        if (ApplicationUtility.checkInternet(context)) {
            String jsonParam = UserNChurchUtils.prepareUserJsonParam(user_id,"false");
            Log.e("PastorJsonParam", jsonParam);
            Call<FriendsResponse> call = apiService.getAllFollowingUsers(jsonParam, StaticData.AUTH_TOKEN);
            call.enqueue(allUserResponseCallback);
        } else {
            rlProgress.setVisibility(View.GONE);
            ApplicationUtility.openNetworkDialog((AppCompatActivity)getActivity());
        }
    }

    Callback<FriendsResponse> allUserResponseCallback = new Callback<FriendsResponse>() {
        @Override
        public void onResponse(Response<FriendsResponse> response, Retrofit retrofit) {
            rlProgress.setVisibility(View.GONE);
            Log.e("Response", response.body().getMessage());
            if (response.body().getMessage().equalsIgnoreCase("success")) {
                friends = response.body().getUser_following();
                Log.e("FRIEND", friends.size() + "");
                setFriendsList();
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

    private void setFriendsList() {
        FriendListAdapter friendListAdapter = new FriendListAdapter(context,R.layout.user_row,friends,(AppCompatActivity)getActivity());
        lvMyFriends.setAdapter(friendListAdapter);
    }

}
