package christian.network.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import christian.network.ProfileActivity;
import christian.network.R;
import christian.network.entity.Friend;
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

/**
 * Created by User on 25-Apr-16.
 */
public class FriendListAdapter extends ArrayAdapter<Friend> {

    Context context;
    AppCompatActivity activity;
    int resId;
    ArrayList<Friend> friends;

    //Web service components
    Retrofit retrofit;
    APIServiceInterface apiService;
    String user_id;

    public FriendListAdapter(Context context, int resource, ArrayList<Friend> friends, AppCompatActivity activity) {
        super(context, resource, friends);
        this.context = context;
        this.resId = resource;
        this.friends = friends;
        this.activity = activity;
    }

    private class ViewHolder {
        ImageView ivUserPhoto;
        TextView tvUserName;
        ImageView ivFollow;
        LinearLayout llUserInfoContent;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(resId, null);
            holder = new ViewHolder();
            holder.ivUserPhoto = (ImageView) convertView.findViewById(R.id.ivUserPhoto);
            holder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            holder.ivFollow = (ImageView) convertView.findViewById(R.id.ivEditComment);
            holder.llUserInfoContent = (LinearLayout) convertView.findViewById(R.id.llUserInfoContent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ivFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unFollowUser(position, holder.ivFollow);
            }
        });

        holder.llUserInfoContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserProfile(friends.get(position));
            }
        });

        if (position < friends.size()) {
            setDataInRow(position, holder);
        }
        return convertView;
    }

    private void setDataInRow(int position, ViewHolder holder) {
        Friend friend = friends.get(position);
        holder.tvUserName.setText(friend.getFirst_name() + " " + friend.getLast_name());
        Picasso.with(context).load(friend.getProfileImage(friend.getFollowee_id())).placeholder(R.drawable.profile_thumb).error(R.drawable.profile_thumb).resize(80, 80).centerCrop().into(holder.ivUserPhoto);
        holder.ivFollow.setImageResource(R.drawable.ic_unfollow_selector);
    }

    private void openUserProfile(Friend friend) {
        Intent pIntent = new Intent(context, ProfileActivity.class);
        pIntent.putExtra(StaticData.USER_ID, friend.getFollowee_id());
        pIntent.putExtra(StaticData.USER_FULL_NAME, friend.getFollowee_id());
        pIntent.putExtra(StaticData.USER_IS_FOLLOWED, true);
        pIntent.putExtra(StaticData.USER_PROFILE, false);
        context.startActivity(pIntent);
    }

    private void unFollowUser(final int position, final ImageView ivFollow) {
        final Friend friend = friends.get(position);
        initWebService();
        setUserID();
        if (ApplicationUtility.checkInternet(context)) {
            HashMap<String, String> hmParams = new HashMap<String, String>();
            hmParams.put(StaticData.USER_ID, user_id);
            hmParams.put(StaticData.FOLLOW_ID, friend.getFollowee_id());
            String jsonParam = UserNChurchUtils.prepareJsonParam(hmParams);
            Log.e("PastorJsonParam", jsonParam);
            Call<CommonResponse> call = apiService.unFollowUser(jsonParam, StaticData.AUTH_TOKEN);
            call.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Response<CommonResponse> response, Retrofit retrofit) {
                    if (response.body().isSuccess()) {
                        friends.remove(position);
                        ivFollow.setImageResource(R.drawable.ic_follow_selector);
                        notifyDataSetChanged();
                    }
                }
                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(context, "There is a problem following user", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ApplicationUtility.openNetworkDialog(activity);
        }
    }

    private void initWebService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(StaticData.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIServiceInterface.class);
    }

    private void setUserID() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString(StaticData.USER_ID, "");
    }

}
