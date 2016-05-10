package christian.network.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import christian.network.R;
import christian.network.entity.Comment;
import christian.network.interfaces.APIServiceInterface;
import christian.network.responses.CommonResponse;
import christian.network.utils.ApplicationUtility;
import christian.network.utils.StaticData;
import christian.network.utils.UserNChurchUtils;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by User on 10-Apr-16.
 */
public class CommentsAdapter extends ArrayAdapter<Comment> {

    private Context context;
    private int resId;
    private ArrayList<Comment> comments;//, origChurches;
    private int delPosition;
    private boolean isDeleteEnabled;
    private Activity activity;
//    private String user_id;
//    ChurchFilter churchFilter;

    //Web Service
    Retrofit retrofit;
    APIServiceInterface apiService;

    public CommentsAdapter(Context context, int resource, ArrayList<Comment> comments, Activity activity) {
        super(context, resource, comments);
        this.context = context;
        this.resId = resource;
        this.comments = comments;
        this.activity = activity;
    }

    private class ViewHolder {
        ImageView ivPoster;
        TextView tvPosterName;
        TextView tvPost;
        TextView tvPostTime;
        ImageView ivEditComment;
        LinearLayout llCommentsDetails;
        ProgressBar pbComment;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(resId, null);
            holder = new ViewHolder();
            holder.ivPoster = (ImageView) convertView.findViewById(R.id.ivPoster);
            holder.tvPost = (TextView) convertView.findViewById(R.id.tvPost);
            holder.tvPosterName = (TextView) convertView.findViewById(R.id.tvPosterName);
            holder.tvPostTime = (TextView) convertView.findViewById(R.id.tvPostTime);
            holder.ivEditComment = (ImageView) convertView.findViewById(R.id.ivEditComment);
            holder.llCommentsDetails = (LinearLayout) convertView.findViewById(R.id.llCommentsDetails);
            holder.pbComment = (ProgressBar) convertView.findViewById(R.id.pbComments);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ivEditComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = context.getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE).getString(StaticData.USER_ID, "");
                Comment comment = comments.get(position);
                Log.e("User_id", user_id + " " + comment.getPoster_id());
                if (comment.getPoster_id().equalsIgnoreCase(user_id)) {
                    processDelete(position, comment, holder);
                } else {
                    notifyDataSetChanged();
                }
            }
        });

        holder.ivEditComment.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });

        holder.llCommentsDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Church Details on dialogue
            }
        });

        if (position < comments.size()) {
            setDataInRow(position, holder);
        }

        return convertView;
    }

    private void setDataInRow(int position, ViewHolder holder) {
        Comment comment = comments.get(position);
        holder.tvPosterName.setText(comment.getFirst_name() + " " + comment.getLast_name());
        holder.tvPost.setText(comment.getDescription());
        holder.ivEditComment.setImageResource(R.drawable.ic_down);
        Picasso.with(context).load(comment.getImage()).placeholder(R.drawable.profile_thumb).error(R.drawable.profile_thumb).into(holder.ivPoster);
        //NextRelease Timestamp
        holder.tvPostTime.setText(setTime(comment.getCreated_date()));
        if (comment.getCreated_date().equalsIgnoreCase("Posting...")) {
            holder.pbComment.setVisibility(View.VISIBLE);
            holder.ivEditComment.setVisibility(View.GONE);
        } else {
            holder.pbComment.setVisibility(View.GONE);
            holder.ivEditComment.setVisibility(View.VISIBLE);
        }
    }

    private String setTime(String timeStamp) {
        if (timeStamp.equalsIgnoreCase("Posting...")) {
            return timeStamp;
        } else if (timeStamp.equalsIgnoreCase("Just now")) {
            return timeStamp;
        } else {
            Calendar cal = Calendar.getInstance();
            long currentTimeMs = cal.getTimeInMillis();
            SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date commentDate = ApplicationUtility.formatDate(timeStamp, dtFormat);
            timeStamp = UserNChurchUtils.getPostTime(commentDate, currentTimeMs);
            return timeStamp;
        }
    }

    private void processDelete(int position, Comment comment, ViewHolder holder) {
        if (isDeleteEnabled) {
            Log.e("Positons", delPosition + " " + position);
            if (delPosition == position) {
                deleteComment(comment.getComment_id());
            } else {
//                comments.get(position).setType(comment.getPoster_type());
                isDeleteEnabled = false;
                notifyDataSetChanged();
            }
        } else {
            setDeleteEnable(position, holder.ivEditComment);
        }
    }

    private void setDeleteEnable(int position, ImageView ivEditComment) {
        delPosition = position;
        isDeleteEnabled = true;
        ivEditComment.setImageResource(R.drawable.ic_cross);
    }

    private void deleteComment(String comment_id) {
        initWebService();
        executeDelete(comment_id);
    }

    private void initWebService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(StaticData.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIServiceInterface.class);
    }

    private void executeDelete(String comment_id) {
        if (ApplicationUtility.checkInternet(context)) {
            HashMap<String, String> hmParams = new HashMap<String, String>();
            hmParams.put(StaticData.COMMENT_ID, comment_id);
            String jsonParam = UserNChurchUtils.prepareJsonParam(hmParams);
            Log.e("JsonParam-all", jsonParam);
            Call<CommonResponse> call = apiService.deleteComment(jsonParam, StaticData.AUTH_TOKEN);
            call.enqueue(deleteCommentResponse);
        } else {
            ApplicationUtility.openNetworkDialog(activity);
        }
    }

    retrofit.Callback<CommonResponse> deleteCommentResponse = new retrofit.Callback<CommonResponse>() {
        @Override
        public void onResponse(Response<CommonResponse> response, Retrofit retrofit) {
            if (response.body().isSuccess()) {
                Log.e("Message", response.body().getMessage());
                comments.remove(delPosition);
                notifyDataSetChanged();
            } else {
                notifyDataSetChanged();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            notifyDataSetChanged();
            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT);
        }
    };

}
