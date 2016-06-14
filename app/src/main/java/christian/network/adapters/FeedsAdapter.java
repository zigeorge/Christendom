package christian.network.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import christian.network.ProfileActivity;
import christian.network.entity.User;
import de.hdodenhof.circleimageview.CircleImageView;
import christian.network.R;
import christian.network.entity.Feed;
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
 * Created by User on 27-Apr-16.
 */
public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.FeedViewHolder> {

    private ArrayList<Feed> feeds;

    Retrofit retrofit;
    APIServiceInterface apiService;
    String user_id, type;
    Context context;
    int like_position;
    private int delPosition;
    private boolean isDeleteEnabled;
    private AppCompatActivity activity;

    OnCommentClickedListener onCommentClickedListener;

    public FeedsAdapter(ArrayList<Feed> feeds, Context context, AppCompatActivity activity) {
        this.feeds = feeds;
        this.context = context;
        this.activity = activity;
        initWebService();
        setUserInfo();

        try {
            onCommentClickedListener = (OnCommentClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnCommentClickedListener");
        }
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.feeds_row, parent, false);
        return new FeedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FeedViewHolder holder, final int position) {
        final Feed feed = feeds.get(position);
//        Log.e("Name", feed.getFirst_name() + " " + feed.getLast_name());
        Picasso.with(context).load(feed.getProfileImage(feed.getPoster_id())).placeholder(R.drawable.profile_thumb).into(holder.civUserImage);
        if (!TextUtils.isEmpty(feed.getPost_image())) {
            holder.ivPostedImage.setVisibility(View.VISIBLE);
            Picasso.with(context).load(feed.getPost_image()).placeholder(R.drawable.progress_loading).into(holder.ivPostedImage); // Add error image here
        } else {
            holder.ivPostedImage.setVisibility(View.GONE);
        }
        holder.tvUsername.setText(feed.getFirst_name() + " " + feed.getLast_name());
        if (feed.getPoster_type().equalsIgnoreCase("pastor")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.tvUsername.setCompoundDrawables(null, null, context.getDrawable(R.drawable.ic_pastor), null);
            } else {
                holder.tvUsername.setCompoundDrawables(null, null, context.getResources().getDrawable(R.drawable.ic_pastor), null);
            }
        } else if (feed.getPoster_type().equalsIgnoreCase("church")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.tvUsername.setCompoundDrawables(null, null, context.getDrawable(R.drawable.ic_church), null);
            } else {
                holder.tvUsername.setCompoundDrawables(null, null, context.getResources().getDrawable(R.drawable.ic_church), null);
            }
        } else {
            holder.tvUsername.setCompoundDrawables(null, null, null, null);
        }
        holder.tvStatus.setText(feed.getDescription());
        holder.tvChurchName.setText(feed.getChurch_name());
        if (feed.getNo_of_likes() == 0) {
            holder.tvNoOfLoves.setVisibility(View.INVISIBLE);
        } else {
            holder.tvNoOfLoves.setVisibility(View.VISIBLE);
            holder.tvNoOfLoves.setText(feed.getNo_of_likes() + " people loved this");
        }
        if (feed.getNo_of_comments() == 0) {
            holder.tvNoOfComments.setVisibility(View.GONE);
        } else {
            holder.tvNoOfComments.setVisibility(View.VISIBLE);
            if (feed.getNo_of_comments() == 1) {
                holder.tvNoOfComments.setText(feed.getNo_of_comments() + " comment");
            } else {
                holder.tvNoOfComments.setText(feed.getNo_of_comments() + " comments");
            }
        }
        setTimeStamp(holder.tvTimeStamp, feed.getPost_created_date());
        if (feed.is_like()) {
            holder.ivLove.setImageResource(R.drawable.ic_like_sel);
            holder.tvLove.setText("Loved");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.tvLove.setTextColor(context.getColor(R.color.color_primary));
            } else {
                holder.tvLove.setTextColor(context.getResources().getColor(R.color.color_primary));
            }
        } else {
            holder.ivLove.setImageResource(R.drawable.ic_love);
            holder.tvLove.setText("Love");
            holder.tvLove.setTextColor(context.getResources().getColor(R.color.grey_e8e));
        }
        holder.ivEditFeed.setImageResource(R.drawable.ic_down);
        holder.llLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Loved", feeds.get(position).getDescription());
                processLove(position);
            }
        });

        holder.llComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCommentClickedListener.onCommentClicked(feed.getPost_id(), feed.getNo_of_likes());
            }
        });

        holder.ivEditFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = context.getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE).getString(StaticData.USER_ID, "");
                Feed feed = feeds.get(position);
                Log.e("User_id", user_id + " " + feed.getPoster_id());
                if (feed.getPoster_id().equalsIgnoreCase(user_id)) {
                    processDelete(position, feed, holder);
                } else {
                    isDeleteEnabled = false;
                    notifyDataSetChanged();
                }
            }
        });

        holder.ivPostedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationUtility.openScreenshotDialog(feed.getPost_image(), context);
            }
        });

        holder.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserProfile(feed);
            }
        });

//        addListeners(holder, position);
    }

    private void openUserProfile(Feed feed) {
        SharedPreferences preferences = context.getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE);
        String user_id = preferences.getString(StaticData.USER_ID, "");
        boolean isMyProfile = false;
        boolean isFollowed = true;
        if (user_id.equalsIgnoreCase(feed.getUser_id())) {
            isFollowed = false;
            isMyProfile = true;
        }
        Intent pIntent = new Intent(context, ProfileActivity.class);
        pIntent.putExtra(StaticData.USER_ID, feed.getUser_id());
        pIntent.putExtra(StaticData.USER_FULL_NAME, feed.getFirst_name() + " " + feed.getLast_name());
        pIntent.putExtra(StaticData.USER_IS_FOLLOWED, isFollowed);
        pIntent.putExtra(StaticData.USER_PROFILE, isMyProfile);
        context.startActivity(pIntent);
    }

    @Override
    public int getItemCount() {
        return feeds.size();
    }

    private void setTimeStamp(TextView tvTime, String date) {
        Calendar cal = Calendar.getInstance();
        long currentTimeMs = cal.getTimeInMillis();
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        dtFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Date commentDate = ApplicationUtility.formatDate(date, dtFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        calendar.setTime(commentDate);
        long commentTimeMs = calendar.getTimeInMillis();
        String timeStamp = UserNChurchUtils.getPostTime(commentTimeMs, currentTimeMs);

        tvTime.setText(timeStamp);
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {

        protected CircleImageView civUserImage;
        protected LinearLayout llFeedContent;
        protected TextView tvUsername;
        protected TextView tvStatus;
        protected TextView tvChurchName;
        protected TextView tvNoOfLoves;
        protected TextView tvNoOfComments;
        protected ImageView ivPostedImage;
        protected TextView tvLove;
        protected TextView tvComment;
        protected ImageView ivLove;
        protected ImageView ivComment;
        protected TextView tvTimeStamp;
        protected LinearLayout llLove;
        protected LinearLayout llComment;
        protected ImageView ivEditFeed;

        public FeedViewHolder(View itemView) {
            super(itemView);
            civUserImage = (CircleImageView) itemView.findViewById(R.id.civUserImage);
            llFeedContent = (LinearLayout) itemView.findViewById(R.id.llFeedContent);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            tvChurchName = (TextView) itemView.findViewById(R.id.tvChurchName);
            tvNoOfLoves = (TextView) itemView.findViewById(R.id.tvNoOfLikes);
            tvNoOfComments = (TextView) itemView.findViewById(R.id.tvNoOfComments);
            ivPostedImage = (ImageView) itemView.findViewById(R.id.ivPostedImage);
            tvLove = (TextView) itemView.findViewById(R.id.tvLove);
            tvComment = (TextView) itemView.findViewById(R.id.tvComment);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            llComment = (LinearLayout) itemView.findViewById(R.id.llComment);
            llLove = (LinearLayout) itemView.findViewById(R.id.llLove);
            ivLove = (ImageView) itemView.findViewById(R.id.ivLove);
            ivComment = (ImageView) itemView.findViewById(R.id.ivComment);
            ivEditFeed = (ImageView) itemView.findViewById(R.id.ivEditFeed);
        }
    }

    private void processLove(int position) {
        if (feeds.get(position).is_like()) {
            //Unlike
            feeds.get(position).setIs_like(false);
            notifyDataSetChanged();
            likePost(feeds.get(position).getPost_id(), position, false);
        } else {
            //like
            feeds.get(position).setIs_like(true);
            notifyDataSetChanged();
            likePost(feeds.get(position).getPost_id(), position, true);
        }
    }

    private void initWebService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(StaticData.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIServiceInterface.class);
    }

    private void setUserInfo() {
        user_id = context.getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE).getString(StaticData.USER_ID, "");
        type = context.getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE).getString(StaticData.TYPE, "");
    }

    private void likePost(String post_id, int position, boolean liked) {
        like_position = position;
        if (ApplicationUtility.checkInternet(context)) {
            HashMap<String, String> hmParams = new HashMap<String, String>();
            hmParams.put(StaticData.USER_ID, user_id);
            hmParams.put(StaticData.POST_ID, post_id);
            if (liked) {
                hmParams.put(StaticData.TYPE, type);
                String jsonParam = UserNChurchUtils.prepareJsonParam(hmParams);
                Call<CommonResponse> call = apiService.likeAPost(jsonParam, StaticData.AUTH_TOKEN);
                call.enqueue(likeApostCallback);
            } else {
                String jsonParam = UserNChurchUtils.prepareJsonParam(hmParams);
                Call<CommonResponse> call = apiService.deleteLike(jsonParam, StaticData.AUTH_TOKEN);
                call.enqueue(deleteLikeCallback);
            }
        } else {
            ApplicationUtility.openNetworkDialog(activity);
        }
    }

    private void processDelete(int position, Feed feed, FeedViewHolder holder) {
        if (isDeleteEnabled) {
            Log.e("Positons", delPosition + " " + position);
            if (delPosition == position) {
                deleteFeed(feed.getPost_id());
            } else {
                isDeleteEnabled = false;
                notifyDataSetChanged();
            }
        } else {
            setDeleteEnable(position, holder.ivEditFeed);
        }
    }

    private void setDeleteEnable(int position, ImageView ivEditComment) {
        delPosition = position;
        isDeleteEnabled = true;
        ivEditComment.setImageResource(R.drawable.ic_cross);
    }

    private void deleteFeed(String post_id) {
        initWebService();
        executeDelete(post_id);
    }

    private void executeDelete(String post_id) {
        if (ApplicationUtility.checkInternet(context)) {
            HashMap<String, String> hmParams = new HashMap<String, String>();
            hmParams.put(StaticData.POST_ID, post_id);
            String jsonParam = UserNChurchUtils.prepareJsonParam(hmParams);
            Log.e("JsonParam-all", jsonParam);
            Call<CommonResponse> call = apiService.deleteUserFeed(jsonParam, StaticData.AUTH_TOKEN);
            call.enqueue(deleteFeedResponse);
        } else {
            ApplicationUtility.openNetworkDialog(activity);
        }
    }

    Callback<CommonResponse> likeApostCallback = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Response<CommonResponse> response, Retrofit retrofit) {
            if (response.body().isSuccess()) {
                isDeleteEnabled = false;
                feeds.get(like_position).setIs_like(true);
                int count = feeds.get(like_position).getNo_of_likes();
                feeds.get(like_position).setNo_of_likes(++count);
                notifyDataSetChanged();
            } else {
                isDeleteEnabled = false;
                feeds.get(like_position).setIs_like(false);
                notifyDataSetChanged();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            feeds.get(like_position).setIs_like(false);
            notifyDataSetChanged();
            Toast.makeText(context, "Failed to like the post. Try again.", Toast.LENGTH_SHORT).show();
        }
    };

    Callback<CommonResponse> deleteLikeCallback = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Response<CommonResponse> response, Retrofit retrofit) {
            if (response.body().isSuccess()) {
                feeds.get(like_position).setIs_like(false);
                int count = feeds.get(like_position).getNo_of_likes();
                feeds.get(like_position).setNo_of_likes(--count);
                notifyDataSetChanged();
            } else {
                feeds.get(like_position).setIs_like(true);
                notifyDataSetChanged();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            feeds.get(like_position).setIs_like(true);
            notifyDataSetChanged();
            Toast.makeText(context, "Failed to like the post. Try again.", Toast.LENGTH_SHORT).show();
        }
    };

    Callback<CommonResponse> deleteFeedResponse = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Response<CommonResponse> response, Retrofit retrofit) {
            if (response.body().isSuccess()) {
                Log.e("Message", response.body().getMessage());
                feeds.remove(delPosition);
                notifyDataSetChanged();
                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT);
            } else {
                notifyDataSetChanged();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            notifyDataSetChanged();
        }
    };

    public interface OnCommentClickedListener {
        public void onCommentClicked(String post_id, int noOfLikes);
    }
}
