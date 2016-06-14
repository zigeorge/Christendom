package christian.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import christian.network.adapters.CommentsAdapter;
import christian.network.entity.Comment;
import christian.network.interfaces.APIServiceInterface;
import christian.network.responses.CommentResponse;
import christian.network.responses.CommonResponse;
import christian.network.utils.ApplicationUtility;
import christian.network.utils.StaticData;
import christian.network.utils.UserNChurchUtils;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class CommentActivity extends AppCompatActivity {

    TextView tvLikeDetails;
    ImageView ivWrite;
    EditText etWriteComment;
    ListView lvComments;
    ProgressBar pbComments;

    ArrayList<Comment> comments = new ArrayList<>();
    CommentsAdapter commentsAdapter;

    String post_id, user_id, type, description, user_fName, user_lName, user_image;
    int noOfLikes;

    //Web Service
    Retrofit retrofit;
    APIServiceInterface apiService;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        context = this;
        initUI();
        initWebService();
        getDataFromIntentAndPreference();
        getAllComments();
        addTextChangeListener();
        addClickListeners();
//        setDataInUI();
    }

    private void initUI() {
        tvLikeDetails = (TextView) findViewById(R.id.tvLikeDetails);
        ivWrite = (ImageView) findViewById(R.id.ivWrite);
        etWriteComment = (EditText) findViewById(R.id.etWriteComment);
        lvComments = (ListView) findViewById(R.id.lvComments);
        pbComments = (ProgressBar) findViewById(R.id.pbComments);
        pbComments.setVisibility(View.VISIBLE);
    }

    private void initWebService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(StaticData.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIServiceInterface.class);
    }

    private void addTextChangeListener() {
        etWriteComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = etWriteComment.getText().toString();
                if (count == 0 || TextUtils.isEmpty(str)) {
                    ivWrite.setImageResource(R.drawable.ic_write_comment);
                    ivWrite.setClickable(false);
                } else {
                    ivWrite.setImageResource(R.drawable.ic_submit_comment_selector);
                    ivWrite.setClickable(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void addClickListeners() {
        ivWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Submit Comment
                if (TextUtils.isEmpty(etWriteComment.getText().toString())||ApplicationUtility.isOnlyBlankSpaces(etWriteComment.getText().toString())) {
                    etWriteComment.setError("You need to write something first");
                } else {
                    description = etWriteComment.getText().toString();
                    etWriteComment.setText("");
                    addCommentInList();
                    submitComment();
                    hideKeyboard();
                }
            }
        });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        ApplicationUtility.hideKeyboard(context, view);
    }

    private void getDataFromIntentAndPreference() {
        post_id = getIntent().getStringExtra(StaticData.POST_ID);
        noOfLikes = getIntent().getIntExtra(StaticData.NO_OF_LIKES, 0);
        SharedPreferences preferences = getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE);
        user_id = preferences.getString(StaticData.USER_ID, "");
        type = preferences.getString(StaticData.TYPE, "member");
        user_fName = preferences.getString(StaticData.USER_FIRST_NAME, "Unknown");
        user_lName = preferences.getString(StaticData.USER_LAST_NAME, "User");
        user_image = preferences.getString(StaticData.USER_IMAGE, "").substring(22);
        Log.e("DATA", post_id + " " + noOfLikes + " Name: " + user_fName + " " + user_lName + " Image: " + user_image);
    }

    private void getAllComments() {
        if (ApplicationUtility.checkInternet(context)) {
            HashMap<String, String> hmParams = new HashMap<String, String>();
            hmParams.put(StaticData.POST_ID, post_id);
            String jsonParam = UserNChurchUtils.prepareJsonParam(hmParams);
            Log.e("JsonParam-all", jsonParam);
            Call<CommentResponse> call = apiService.getAllComments(jsonParam, StaticData.AUTH_TOKEN);
            call.enqueue(commentResponseCallBack);
        } else {
            ApplicationUtility.openNetworkDialog(this);
        }
    }

    private void addCommentInList() {
        Comment aComment = new Comment();
        aComment.setDescription(description);
        aComment.setFirst_name(user_fName);
        aComment.setLast_name(user_lName);
        aComment.setImage(user_image);
        aComment.setCreated_date("Posting...");
        aComment.setPoster_id(user_id);
        Log.e("New Comment", aComment.getDescription() + "\n " + aComment.getLast_name() + "\n " + aComment.getImage());
        if (comments.isEmpty()) {
            comments.add(aComment);
            commentsAdapter = new CommentsAdapter(context, R.layout.comment_row, comments, this);
            lvComments.setAdapter(commentsAdapter);
        } else {
            comments.add(aComment);
            commentsAdapter.notifyDataSetChanged();
        }
    }

    private void setDataInUI() {
        if (noOfLikes == 0) {
            if (!comments.isEmpty()) {
                tvLikeDetails.setVisibility(View.VISIBLE);
                tvLikeDetails.setText(comments.size() + " comments");
            } else {
                tvLikeDetails.setVisibility(View.GONE);
            }
        } else {
            tvLikeDetails.setVisibility(View.VISIBLE);
            if (!comments.isEmpty()) {
                tvLikeDetails.setText(noOfLikes + " likes " + comments.size() + " comments");
            } else {
                tvLikeDetails.setText(noOfLikes + " people like this");
            }
        }
    }

    private void setAllComments(ArrayList<Comment> comments) {
        this.comments = comments;
        commentsAdapter = new CommentsAdapter(context, R.layout.comment_row, comments, this);
        lvComments.setAdapter(commentsAdapter);
    }

    private void setNoComments() {
        setDataInUI();
    }

    private void submitComment() {
        if (ApplicationUtility.checkInternet(context)) {
            HashMap<String, String> hmParams = new HashMap<String, String>();
            hmParams.put(StaticData.POST_ID, post_id);
            hmParams.put(StaticData.USER_ID, user_id);
            hmParams.put(StaticData.TYPE, type);
            hmParams.put(StaticData.DESCRIPTION, description);
            String jsonParam = UserNChurchUtils.prepareJsonParam(hmParams);
            Log.e("JsonParam", jsonParam);
            Call<CommonResponse> call = apiService.submitCommet(jsonParam, StaticData.AUTH_TOKEN);
            call.enqueue(commentSubmitResponse);
        } else {
            ApplicationUtility.openNetworkDialog(this);
        }
    }

    Callback<CommentResponse> commentResponseCallBack = new Callback<CommentResponse>() {
        @Override
        public void onResponse(Response<CommentResponse> response, Retrofit retrofit) {
            pbComments.setVisibility(View.GONE);
            if (response.body().isSuccess()) {
                Log.e("Comment", "" + response.body().getComments().get(0).getDescription());
                setAllComments(response.body().getComments());
                setDataInUI();
            } else {
                setNoComments();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            pbComments.setVisibility(View.GONE);
            Toast.makeText(context, "Failed Getting comment!", Toast.LENGTH_SHORT).show();
        }
    };

    Callback<CommonResponse> commentSubmitResponse = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Response<CommonResponse> response, Retrofit retrofit) {
            if (response.body().isSuccess()) {
//                getAllComments();
                Log.e("Comment", response.body().getMessage());
                modifyCommentList(true);
                setDataInUI();
            } else {
                modifyCommentList(false);
                etWriteComment.setText(description);
            }
        }

        @Override
        public void onFailure(Throwable t) {

        }
    };

    private void modifyCommentList(boolean hasChanged) {
        if (hasChanged) {
            comments.get(comments.size() - 1).setCreated_date("Just now");
            commentsAdapter.notifyDataSetChanged();
        } else {
            comments.remove(comments.size() - 1);
            commentsAdapter.notifyDataSetChanged();
        }
    }

}
