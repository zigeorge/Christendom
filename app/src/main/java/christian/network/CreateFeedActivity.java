package christian.network;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
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
    RelativeLayout rlImageLayout, rlProgress;
    Toolbar toolbar;
    ImageView ivPost;
    ProgressBar pbPostFeed;

    Context context;

    //Web Service
    Retrofit retrofit;
    APIServiceInterface apiService;
    String user_id, image="";

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

    private void showSoftKeyboard() {
        etWritePost.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etWritePost, InputMethodManager.SHOW_IMPLICIT);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    private void initActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private void initWebService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(StaticData.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIServiceInterface.class);
    }

    private void initUI() {
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ivPostImage = (ImageView) findViewById(R.id.ivPostImage);
        ivDeleteImage = (ImageView) findViewById(R.id.ivDeleteImage);
        ivGetPhoto = (ImageView) findViewById(R.id.ivGetPhoto);
        etWritePost = (EditText) findViewById(R.id.etWritePost);
        rlImageLayout = (RelativeLayout) findViewById(R.id.rlImageLayout);
        rlProgress = (RelativeLayout) findViewById(R.id.rlProgress);
        toolbar = (Toolbar) findViewById(R.id.inc_topbar);
        ivPost = (ImageView) toolbar.findViewById(R.id.toolbar_menu);
        pbPostFeed = (ProgressBar) toolbar.findViewById(R.id.progressBar);
        pbPostFeed.setVisibility(View.GONE);
    }

    private void setProfileImage() {
//        String imgUrl = getSharedPreferences(StaticData.APP_PREFERENCE,Context.MODE_PRIVATE).getString(StaticData.USER_IMAGE,"");
        String imgUrl = StaticData.FACEBOOK_IMAGE_URL + user_id + StaticData.FACEBOOK_IMAGE_SIZE;
        Picasso.with(context).load(imgUrl).placeholder(R.drawable.profile_thumb).error(R.drawable.profile_thumb).into(ivProfileImage);
    }

    private void getDataFromIntent() {
        user_id = getIntent().getStringExtra(StaticData.USER_ID);
//        church_id = getIntent().getStringExtra(StaticData.CHURCH_ID);
    }

    private void addClickListenersForImageView() {
//        ivProfileImage.setOncli
        ivGetPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Gallery and Camera Handling Function Permission
                ApplicationUtility.hideKeyboard(context,etWritePost);
                getImage();
            }
        });
        ivDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image = "";
                rlImageLayout.setVisibility(View.GONE);
            }
        });

    }

    private void getImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //SEE NEXT STEP
            if (ApplicationUtility.checkPermissions(this)) {
                ApplicationUtility.getImageFromDevice(this);
            } else {
                // Request Permission
                ApplicationUtility.requestPermission(this);
            }
        } else {
            ApplicationUtility.getImageFromDevice(this);
        }
    }

    private void addTextClickListener() {
        ivPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Post Feed
                if (TextUtils.isEmpty(etWritePost.getText())) {
                    etWritePost.setError("You need to write something before post");
                } else {
                    etWritePost.setClickable(false);
                    etWritePost.setTextColor(getResources().getColor(R.color.grey_555));
                    pbPostFeed.setVisibility(View.VISIBLE);
                    ivPost.setVisibility(View.GONE);
                    rlProgress.setVisibility(View.VISIBLE);
                    postFeed();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StaticData.SELECT_PICTURE && resultCode == RESULT_OK) {
            try {
                Uri selectedImageUri = data.getData();
                String selectedImagePath = ApplicationUtility.getPath(selectedImageUri, this);
                System.out.println(selectedImagePath);
                ivPostImage.setImageURI(Uri.parse(selectedImagePath));
                setImageString();
                rlImageLayout.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setImageString(){
        BitmapDrawable drawable = (BitmapDrawable) ivPostImage
                .getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        image = ApplicationUtility.getImageInBase64(bitmap);
    }

    private void postFeed() {
        if (ApplicationUtility.checkInternet(context)) {
            HashMap<String, String> hmParams = new HashMap<String, String>();
            hmParams.put(StaticData.USER_ID, user_id);
            hmParams.put(StaticData.TYPE, "member");
            hmParams.put(StaticData.POST, etWritePost.getText().toString());
            hmParams.put(StaticData.IMG_URL, getImageUrl());
            String jsonParam = UserNChurchUtils.prepareJsonParam(hmParams);
//            try {
//                jsonParam = URLEncoder.encode(jsonParam, "UTF-8");
//                Log.e("JSONPARAM", jsonParam);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            Log.e("JsonParam", jsonParam);
            Call<CommonResponse> call = apiService.postUserFeed(jsonParam, StaticData.AUTH_TOKEN, image);
            call.enqueue(userPostResponse);
        } else {
            etWritePost.setClickable(true);
            etWritePost.setTextColor(getResources().getColor(R.color.black));
            pbPostFeed.setVisibility(View.GONE);
            ivPost.setVisibility(View.VISIBLE);
            rlProgress.setVisibility(View.GONE);
            ApplicationUtility.openNetworkDialog(this);
        }
    }

    private String getImageUrl(){
        String url = StaticData.IMG_BASE_URL+StaticData.IMG_POST_EXTNSN+Calendar.getInstance().getTimeInMillis();
        return url;
    }

    Callback<CommonResponse> userPostResponse = new Callback<CommonResponse>() {
        @Override
        public void onResponse(Response<CommonResponse> response, Retrofit retrofit) {
            pbPostFeed.setVisibility(View.GONE);
            ivPost.setVisibility(View.VISIBLE);
            rlProgress.setVisibility(View.GONE);
            if (response.body().isSuccess()) {
                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                etWritePost.setText("");
                finish();
            } else {
                etWritePost.setClickable(true);
                etWritePost.setTextColor(getResources().getColor(R.color.black));
                Toast.makeText(context, "Try Again!!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            pbPostFeed.setVisibility(View.GONE);
            ivPost.setVisibility(View.VISIBLE);
            etWritePost.setClickable(true);
            rlProgress.setVisibility(View.GONE);
            etWritePost.setTextColor(getResources().getColor(R.color.black));
            Toast.makeText(context, "Problem updating post. Please try again.", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case StaticData.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    /**
                     * We are good, turn on monitoring
                     */
                    if (ApplicationUtility.checkPermissions(this)) {
                        ApplicationUtility.getImageFromDevice(this);
                    } else {
                        ApplicationUtility.requestPermission(this);
                    }
                } else {
                    /**
                     * No permissions, block out all activities that require a location to function
                     */
                    Toast.makeText(this, "Permission Not Granted.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

}
