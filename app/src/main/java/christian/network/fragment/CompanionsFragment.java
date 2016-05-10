package christian.network.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import christian.network.R;
import christian.network.adapters.PagerAdapter;
import christian.network.entity.User;
import christian.network.interfaces.APIServiceInterface;
import christian.network.responses.AllUsersResponse;
import christian.network.utils.ApplicationUtility;
import christian.network.utils.UserNChurchUtils;
import christian.network.utils.StaticData;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompanionsFragment extends Fragment {

    private static final String ARG_PARAM1 = "user_list";
    private static final String ARG_PARAM2 = "title_text";

    ViewPager vpCompanions;

    Retrofit retrofit;
    APIServiceInterface apiService;
    ArrayList<User> members, pastors;
    String user_id;

    Context context;

    public CompanionsFragment() {
        // Required empty public constructor
    }

    public static CompanionsFragment newInstance(String user_id, String title) {
        CompanionsFragment fragment = new CompanionsFragment();
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
        getAllUsers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_companions, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
    }

    private void initUI(View view) {
        vpCompanions = (ViewPager) view.findViewById(R.id.vpCompanions);
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
            String jsonParam = UserNChurchUtils.prepareUserJsonParam(user_id);
            Log.e("PastorJsonParam", jsonParam);
            Call<AllUsersResponse> call = apiService.getAllUsers(jsonParam, StaticData.AUTH_TOKEN);
            call.enqueue(allUserResponseCallback);
        } else {
            ApplicationUtility.openNetworkDialog(getActivity());
        }
    }

    Callback<AllUsersResponse> allUserResponseCallback = new Callback<AllUsersResponse>() {
        @Override
        public void onResponse(Response<AllUsersResponse> response, Retrofit retrofit) {
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

    private void setUserPager() {
        vpCompanions.setAdapter(new PagerAdapter(getActivity().getSupportFragmentManager(), getFragments(), true));
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(UserFragment.newInstance(members, "Members"));
        fragments.add(UserFragment.newInstance(pastors, "Pastors"));

        return fragments;
    }

}
