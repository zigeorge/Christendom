package christian.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import christian.network.adapters.FeedsAdapter;
import christian.network.fragment.HomeFragment;
import christian.network.fragment.CompanionsFragment;
import christian.network.utils.StaticData;

public class MainActivity extends AppCompatActivity implements FeedsAdapter.OnCommentClickedListener, HomeFragment.OnHomeFragmentResumed {

    //Navigation Drawer
    DrawerLayout dlMain;
    ActionBarDrawerToggle mDrawerToggle;
    Toolbar toolbar;
    NavigationView nvDrawer;
    CircleImageView civNavHeaderImage;
    RelativeLayout rlNavHeaderContent;
    TextView tvHeaderProfileName, tvHeaderChurchName;

    //Floating Action Button
    FloatingActionButton fabMain;
    LinearLayout llWritePost, llFindFriends;  //llSendMessage,
    RelativeLayout rlFabLayer;

    boolean isFabLayerOn = false;
    String user_id, church_id;

    FragmentManager fragmentManager;
    Context context;

    public static boolean isCompanion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        initUI();
        initActionBar();
        initDrawer();
        initFragmentManager();
        setUserNChurchId();
        setupDrawerContent();
        setDrawerHeaderContent();
        addFabActions();
        disableFabLayer();
        Log.e("Christendom", "On Create");
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSoftKeyBoard();
        Log.e("Christendom", "On Resume");
    }

    private void hideSoftKeyBoard() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    private void initUI() {
        dlMain = (DrawerLayout) findViewById(R.id.dlMain);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        nvDrawer = (NavigationView) findViewById(R.id.nvDrawer);
        fabMain = (FloatingActionButton) findViewById(R.id.fabHome);
        llWritePost = (LinearLayout) findViewById(R.id.llWriteStatus);
        llFindFriends = (LinearLayout) findViewById(R.id.llFindFriends);
//        llSendMessage = (LinearLayout) findViewById(R.id.llSendMessage);
        rlFabLayer = (RelativeLayout) findViewById(R.id.rlFabLayer);
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private void initDrawer() {
        View headerView = nvDrawer.getHeaderView(0);
        civNavHeaderImage = (CircleImageView) headerView.findViewById(R.id.ivNavHeaderProfileImage);
        tvHeaderProfileName = (TextView) headerView.findViewById(R.id.tvNavHeaderProfileName);
        tvHeaderChurchName = (TextView) headerView.findViewById(R.id.tvNavHeaderChurchName);
        rlNavHeaderContent = (RelativeLayout) headerView.findViewById(R.id.rlNavHeaderContent);
        mDrawerToggle = setupDrawerToggle();
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        dlMain.setDrawerListener(mDrawerToggle);
    }

    private void setDrawerHeaderContent() {
        SharedPreferences sharedPreferences = getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE);
        String image = StaticData.FACEBOOK_IMAGE_URL + user_id + StaticData.FACEBOOK_IMAGE_SIZE;
        String name = sharedPreferences.getString(StaticData.USER_FIRST_NAME, "") + " " + sharedPreferences.getString(StaticData.USER_LAST_NAME, "");
        String church_name = sharedPreferences.getString(StaticData.CHURCH_NAME, "");
        Picasso.with(context).load(image).placeholder(R.drawable.profile_thumb).error(R.drawable.profile_thumb).into(civNavHeaderImage);
        tvHeaderProfileName.setText(name);
        tvHeaderChurchName.setText(church_name);
        setHeaderContentClickListener();
    }

    private void initFragmentManager() {
        fragmentManager = getSupportFragmentManager();
        openHomeFragment();
        if (!isCompanion) {
            openHomeFragment();
        } else {
            openCompanionFragment();
        }
    }

    private void openCompanionFragment() {
        fabMain.setVisibility(View.GONE);
        fragmentManager.beginTransaction().replace(R.id.flContent, CompanionsFragment.newInstance(user_id, "Follow Friends")).commit();
    }

    private void openHomeFragment() {
        fragmentManager.beginTransaction().replace(R.id.flContent, HomeFragment.newInstance()).commit();
    }

    private void setUserNChurchId() {
        user_id = getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE).getString(StaticData.USER_ID, "");
        church_id = getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE).getString(StaticData.CHURCH_ID, "");
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, dlMain, toolbar, R.string.txt_nav_open, R.string.txt_nav_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (isFabLayerOn) {
                    disableFabLayer();
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (isFabLayerOn) {
                    disableFabLayer();
                }
            }
        };
    }

    private void setupDrawerContent() {
        nvDrawer.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void setHeaderContentClickListener() {
        rlNavHeaderContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(StaticData.USER_ID, user_id);
                openProfile();
                dlMain.closeDrawers();
            }
        });
    }

    private void openProfile() {
        Intent pIntent = new Intent(context, ProfileActivity.class);
        pIntent.putExtra(StaticData.USER_ID, user_id);
        pIntent.putExtra(StaticData.USER_FULL_NAME, tvHeaderProfileName.getText());
        pIntent.putExtra(StaticData.USER_IS_FOLLOWED, false);
        pIntent.putExtra(StaticData.USER_PROFILE, true);
        startActivity(pIntent);
    }

    private void addFabActions() {
        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFabLayerOn) {
                    enableFabLayer();
                } else {
                    disableFabLayer();
                }
            }
        });
        rlFabLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableFabLayer();
            }
        });

        llFindFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMain.setVisibility(View.GONE);
                openCompanionFragment();
                disableFabLayer();
                isCompanion = true;
            }
        });

        llWritePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableFabLayer();
                openWritePostActivity();
            }
        });
    }

    private void openWritePostActivity() {
        Intent cIntent = new Intent(context, CreateFeedActivity.class);
        cIntent.putExtra(StaticData.USER_ID, user_id);
        cIntent.putExtra(StaticData.CHURCH_ID, church_id);
        startActivity(cIntent);
    }

    private void enableFabLayer() {
        isFabLayerOn = true;
        rlFabLayer.setVisibility(View.VISIBLE);
        llFindFriends.setVisibility(View.VISIBLE);
//        llSendMessage.setVisibility(View.VISIBLE);
        llWritePost.setVisibility(View.VISIBLE);
        fabMain.setImageResource(R.drawable.ic_fab_down);
    }

    private void disableFabLayer() {
        fabMain.setImageResource(R.drawable.ic_fab_up);
        isFabLayerOn = false;
        rlFabLayer.setVisibility(View.GONE);
        llFindFriends.setVisibility(View.GONE);
//        llSendMessage.setVisibility(View.GONE);
        llWritePost.setVisibility(View.GONE);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        boolean isLogOut = false;
        // Create a new fragment and specify the planet to show based on
        // position
        Fragment fragment = null;

        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                fragment = HomeFragment.newInstance();
                fabMain.setVisibility(View.VISIBLE);
                isCompanion = false;
                break;
            /*case R.id.nav_messages:
//                fragmentClass = SecondFragment.class;
                break;*/
            case R.id.nav_prayer_companion:
                isCompanion = true;
                fragment = CompanionsFragment.newInstance(user_id, menuItem.getTitle().toString());
                fabMain.setVisibility(View.GONE);
//                UserFragment.isCompanion = true;
                break;
            /*case R.id.nav_my_profile:
//                fragmentClass = ThirdFragment.class;
                break;
            case R.id.nav_settings:
//                fragmentClass = ThirdFragment.class;
                break;
            case R.id.nav_share_friends:
//                fragmentClass = ThirdFragment.class;
                break;*/
            case R.id.nav_logout:
                isCompanion = false;
                logout();
                isLogOut = true;
                break;
            default:
                fragment = HomeFragment.newInstance();
                break;
        }

        if (!isLogOut) {
            // Insert the fragment by replacing any existing fragment
//            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }
        // Highlight the selected item, update the title, and close the drawer
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        dlMain.closeDrawers();
    }


    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        SharedPreferences preferences = getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(StaticData.SESSION, false);
        editor.commit();
        startActivity(new Intent(context, WelcomeActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (isFabLayerOn) {
            disableFabLayer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onCommentClicked(String post_id, int noOfLike) {
//        startActivity();
        //Next release
//        Toast.makeText(context,"We are working on it.",Toast.LENGTH_SHORT).show();
        Intent cIntent = new Intent(context, CommentActivity.class);
        cIntent.putExtra(StaticData.POST_ID, post_id);
        cIntent.putExtra(StaticData.NO_OF_LIKES, noOfLike);
        startActivity(cIntent);
    }

    @Override
    public void homeFragmentResumed() {
        fabMain.setVisibility(View.VISIBLE);
    }
}
