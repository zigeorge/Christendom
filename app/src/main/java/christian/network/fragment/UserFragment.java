package christian.network.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import christian.network.R;
import christian.network.adapters.UserListAdapter;
import christian.network.entity.User;

public class UserFragment extends Fragment {

    private static final String ARG_PARAM1 = "user_list";
    private static final String ARG_PARAM2 = "title_text";

    private ArrayList<User> users;
    private ListView lvUsers;

//    public static boolean isCompanion = false;
    Context context;

    public UserFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(ArrayList<User> users, String title) {
        UserFragment fragment = new UserFragment();Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, users);
        args.putString(ARG_PARAM2, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            users = getArguments().getParcelableArrayList(ARG_PARAM1);
//            getActivity().setTitle(getArguments().getString(ARG_PARAM2));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
//        isCompanion = true;
    }

    private void initUI(View view) {
        context = getActivity();
        lvUsers = (ListView) view.findViewById(R.id.lvUsers);
        Log.e("Members", users.size()+"");
        UserListAdapter userListAdapter = new UserListAdapter(context, R.layout.user_row, users, getActivity());
        lvUsers.setAdapter(userListAdapter);
    }



}
