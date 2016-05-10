package christian.network.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import christian.network.entity.User;
import christian.network.interfaces.APIServiceInterface;
import christian.network.responses.CommonResponse;
import christian.network.utils.ApplicationUtility;
import christian.network.utils.UserNChurchUtils;
import christian.network.utils.StaticData;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by User on 25-Apr-16.
 */
public class UserListAdapter extends ArrayAdapter<User> {

    Context context;
    Activity activity;
    int resId;
    ArrayList<User> users;

    //Web service components
    Retrofit retrofit;
    APIServiceInterface apiService;
    String user_id;

    public UserListAdapter(Context context, int resource, ArrayList<User> users, Activity activity) {
        super(context, resource, users);
        this.context = context;
        this.resId = resource;
        this.users = users;
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
                followUser(position, holder.ivFollow);
            }
        });

        holder.llUserInfoContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserProfile(users.get(position));
            }
        });

        if (position < users.size()) {
            setDataInRow(position, holder);
        }
        return convertView;
    }

    private void setDataInRow(int position, ViewHolder holder) {
        User user = users.get(position);
        holder.tvUserName.setText(user.getFirst_name() + " " + user.getLast_name());
        Picasso.with(context).load(user.getImage()).placeholder(R.drawable.profile_thumb).error(R.drawable.profile_thumb).resize(55, 55).into(holder.ivUserPhoto);
        if (user.isFollowed()) {
            holder.ivFollow.setImageResource(R.drawable.ic_unfollow_selector);
        } else {
            holder.ivFollow.setImageResource(R.drawable.ic_follow_selector);
        }
    }

    private void openUserProfile(User user){
        Intent pIntent = new Intent(context, ProfileActivity.class);
        pIntent.putExtra(StaticData.USER_ID, user.getUser_id());
        pIntent.putExtra(StaticData.USER_FULL_NAME, user.getFull_name());
        pIntent.putExtra(StaticData.USER_IS_FOLLOWED, user.isFollowed());
        pIntent.putExtra(StaticData.USER_PROFILE,false);
        context.startActivity(pIntent);
    }

    private void followUser(final int position, final ImageView ivFollow) {
        final User user = users.get(position);
        initWebService();
        setUserID();
        if (ApplicationUtility.checkInternet(context)) {
            HashMap<String, String> hmParams = new HashMap<String, String>();
            hmParams.put(StaticData.USER_ID, user_id);
            hmParams.put(StaticData.FOLLOW_ID, user.getUser_id());
            String jsonParam = UserNChurchUtils.prepareJsonParam(hmParams);
            Log.e("PastorJsonParam", jsonParam);
            Call<CommonResponse> call;
            if (user.isFollowed()) {
                call = apiService.unFollowUser(jsonParam, StaticData.AUTH_TOKEN);
            } else {
                call = apiService.followUser(jsonParam, StaticData.AUTH_TOKEN);
            }
            call.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Response<CommonResponse> response, Retrofit retrofit) {
                    if (response.body().isSuccess()) {
                        if (user.isFollowed()) {
                            users.get(position).setIsFollowed(false);
                            ivFollow.setImageResource(R.drawable.ic_follow_selector);
                        } else {
                            users.get(position).setIsFollowed(true);
                            ivFollow.setImageResource(R.drawable.ic_unfollow_selector);
                        }
                    }else{
                        users.get(position).setIsFollowed(true);
                        ivFollow.setImageResource(R.drawable.ic_unfollow_selector);
                        Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_SHORT).show();
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
