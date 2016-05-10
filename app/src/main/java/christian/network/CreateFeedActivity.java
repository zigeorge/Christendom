package christian.network;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

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

public class CreateFeedActivity extends AppCompatActivity {

    ImageView ivProfileImage, ivPostImage, ivDeleteImage, ivGetPhoto;
    EditText etWritePost;
    RelativeLayout rlImageLayout;
    Toolbar toolbar;
    ImageView ivPost;

    Context context;

    //Web Service
    Retrofit retrofit;
    APIServiceInterface apiService;
    String user_id, church_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_feed);
        context = this;

        initUI();
        initActionBar();
        initWebService();
        getDataFromIntent();
        addClickListenersForImageView();
        addTextClickListener();
        setProfileImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSoftKeyboard();
    }

    private void showSoftKeyboard(){
        etWritePost.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etWritePost, InputMethodManager.SHOW_IMPLICIT);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }



    private void initActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private void initWebService(){
        retrofit = new Retrofit.Builder()
                .baseUrl(StaticData.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIServiceInterface.class);
    }

    private void initUI(){
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ivPostImage = (ImageView) findViewById(R.id.ivPostImage);
        ivDeleteImage = (ImageView) findViewById(R.id.ivDeleteImage);
        ivGetPhoto = (ImageView) findViewById(R.id.ivGetPhoto);
        etWritePost = (EditText) findViewById(R.id.etWritePost);
        rlImageLayout = (RelativeLayout) findViewById(R.id.rlImageLayout);
        toolbar = (Toolbar) findViewById(R.id.inc_topbar);
        ivPost = (ImageView) toolbar.findViewById(R.id.toolbar_menu);
    }

    private void setProfileImage(){
        String imgUrl = getSharedPreferences(StaticData.APP_PREFERENCE,Context.MODE_PRIVATE).getString(StaticData.USER_IMAGE,"");
        Picasso.with(context).load(imgUrl).placeholder(R.drawable.profile_thumb).error(R.drawable.profile_thumb).into(ivProfileImage);
    }

    private void getDataFromIntent(){
        user_id = getIntent().getStringExtra(StaticData.USER_ID);
        church_id = getIntent().getStringExtra(StaticData.CHURCH_ID);
    }

    private void addClickListenersForImageView(){
//        ivProfileImage.setOncli
        ivGetPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Gallery and Camera Handling Function Permission
                Toast.makeText(context,"Work in progress", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addTextClickListener(){
        ivPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Post Feed
                if(TextUtils.isEmpty(etWritePost.getText())){
                    etWritePost.setError("You need to write something before post");
                }else {
                    etWritePost.setClickable(false);
                    etWritePost.setTextColor(getResources().getColor(R.color.grey_555));
                    postFeed();
                }
            }
        });
    }

    private void postFeed(){
        if (ApplicationUtility.checkInternet(context)) {
            HashMap<String, String> hmParams = new HashMap<String, String>();
            hmParams.put(StaticData.USER_ID, user_id);
            hmParams.put(StaticData.TYPE, "member");
            hmParams.put(StaticData.POST,etWritePost.getText().toString());
            hmParams.put(StaticData.IMG_URL,"");
            String jsonParam = UserNChurchUtils.prepareJsonParam(hmParams);
            Log.e("JsonParam", jsonParam);
            Call<CommonResponse> call = apiService.postUserFeed(jsonParam, StaticData.AUTH_TOKEN);
            call.enqueue(userPostResponse);
        } else {
            etWritePost.setClickable(true);
            etWritePost.setTextColor(getResources().getColor(R.color.black));
            ApplicationUtility.openNetworkDialog(this);
        }
    }

    Callback<CommonResponse> userPostResponse = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Response<CommonResponse> response, Retrofit retrofit) {
            if(response.body().isSuccess()){
                Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_SHORT).show();
                etWritePost.setText("");
                finish();
            }else{
                etWritePost.setClickable(true);
                etWritePost.setTextColor(getResources().getColor(R.color.black));
                Toast.makeText(context,"Try Again!!",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            etWritePost.setClickable(true);
            etWritePost.setTextColor(getResources().getColor(R.color.black));
            Toast.makeText(context,"Problem updating post. Please try again.",Toast.LENGTH_SHORT).show();
        }
    };
}
