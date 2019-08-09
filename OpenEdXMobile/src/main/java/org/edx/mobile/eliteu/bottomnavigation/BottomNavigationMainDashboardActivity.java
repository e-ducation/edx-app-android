package org.edx.mobile.eliteu.bottomnavigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;


import android.view.ViewTreeObserver;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.inject.Inject;
import com.gyf.immersionbar.ImmersionBar;

import org.edx.mobile.BuildConfig;
import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragmentActivity;
import org.edx.mobile.deeplink.ScreenDef;
import org.edx.mobile.eliteu.bindmobile.BindMobileUtil;
import org.edx.mobile.eliteu.bottomnavigation.course.EliteuFindCourseFragment;
import org.edx.mobile.eliteu.bottomnavigation.my.MyUserCenterFragment;
import org.edx.mobile.eliteu.bottomnavigation.study.EliteuStudyFragment;
import org.edx.mobile.eliteu.mainsite.ui.MainSiteFragment;
import org.edx.mobile.eliteu.util.AccountPrefs;
import org.edx.mobile.eliteu.util.RxBus;
import org.edx.mobile.eliteu.wight.CannotScrollViewPager;
import org.edx.mobile.event.AccountDataLoadedEvent;
import org.edx.mobile.event.MoveToDiscoveryTabEvent;
import org.edx.mobile.event.NewVersionAvailableEvent;
import org.edx.mobile.interfaces.RefreshListener;
import org.edx.mobile.interfaces.SnackbarStatusListener;
import org.edx.mobile.model.api.ProfileModel;
import org.edx.mobile.module.notification.NotificationDelegate;
import org.edx.mobile.module.prefs.LoginPrefs;
import org.edx.mobile.module.prefs.PrefManager;
import org.edx.mobile.user.Account;
import org.edx.mobile.user.UserAPI;
import org.edx.mobile.user.UserService;
import org.edx.mobile.util.AppConstants;
import org.edx.mobile.util.AppStoreUtils;
import org.edx.mobile.util.IntentFactory;
import org.edx.mobile.util.Version;

import java.text.ParseException;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import retrofit2.Call;

import static org.edx.mobile.view.Router.EXTRA_PATH_ID;
import static org.edx.mobile.view.Router.EXTRA_SCREEN_NAME;

public class BottomNavigationMainDashboardActivity extends BaseFragmentActivity implements BottomNavigationBar.OnTabSelectedListener, SnackbarStatusListener, RefreshListener {

    @Inject
    NotificationDelegate notificationDelegate;

    CoordinatorLayout rootView;

    private ImmersionBar mImmersionBar;

    public BottomNavigationBar mBottomNavigationBar;

    CannotScrollViewPager mViewPager;

    private ArrayList<FragmentInfo> fragments;

    public boolean showWhatsNew = false;

    private Call<Account> getAccountCall;

    private ProfileModel profile;

    @javax.inject.Inject
    private UserService userService;

    @javax.inject.Inject
    private LoginPrefs loginPrefs;

    @javax.inject.Inject
    private AccountPrefs accountPrefs;

    public static Context mContext;

    public static Intent newIntent(@Nullable @ScreenDef String screenName, @Nullable String pathId) {
        // These flags will make it so we only have a single instance of this activity,
        // but that instance will not be restarted if it is already running
        return IntentFactory.newIntentForComponent(BottomNavigationMainDashboardActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .putExtra(EXTRA_SCREEN_NAME, screenName)
                .putExtra(EXTRA_PATH_ID, pathId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard_bottom_navigation);
        mContext = this;
        sendGetUpdatedAccountCall();
        initUi();
        initBottomNavigationBar();
        initFragment();
        initStatusBar();
        initWhatsNew();
        initDiscoveryTabEvent();
    }

    private void initUi() {
        rootView = findViewById(R.id.root_view);
        mBottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        mViewPager = findViewById(R.id.viewPager);
    }


    private void initBottomNavigationBar() {
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        mBottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.index_page_select, getString(R.string.index_page)).setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.index_page)))
                .addItem(new BottomNavigationItem(R.drawable.course_select, getString(R.string.course)).setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.course)))
                .addItem(new BottomNavigationItem(R.drawable.study_select, getString(R.string.study)).setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.study)))
                .addItem(new BottomNavigationItem(R.drawable.my_select, getString(R.string.mine)).setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.my)))
                .setFirstSelectedPosition(0)
                .initialise();
        mBottomNavigationBar.setTabSelectedListener(this);
        mBottomNavigationBar.setAutoHideEnabled(false);
        ViewTreeObserver vto1 = mBottomNavigationBar.getViewTreeObserver();
        vto1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBottomNavigationBar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int height = mBottomNavigationBar.getMeasuredHeight();
                mViewPager.setPadding(0,0,0,height);
            }
        });
    }


    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(new FragmentInfo(getString(R.string.index_page), MainSiteFragment.class));
        fragments.add(new FragmentInfo(getString(R.string.course), EliteuFindCourseFragment.class));
        fragments.add(new FragmentInfo(getString(R.string.study), EliteuStudyFragment.class));
        fragments.add(new FragmentInfo(getString(R.string.course), MyUserCenterFragment.class));

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setOffscreenPageLimit(adapter.getCount());
        mViewPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
        mViewPager.setAdapter(adapter);

    }


    private void initStatusBar() {
        mImmersionBar = ImmersionBar.with(this)
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true)
                .fitsSystemWindows(true);  //透明状态栏，不写默认透明色
        mImmersionBar.init();
    }


    private void initWhatsNew() {
        if (environment.getConfig().isWhatsNewEnabled()) {
            boolean shouldShowWhatsNew = false;
            final PrefManager.AppInfoPrefManager appPrefs = new PrefManager.AppInfoPrefManager(this);
            final String lastWhatsNewShownVersion = appPrefs.getWhatsNewShownVersion();
            if (lastWhatsNewShownVersion == null) {
                shouldShowWhatsNew = true;
            } else {
                try {
                    final Version oldVersion = new Version(lastWhatsNewShownVersion);
                    final Version newVersion = new Version(BuildConfig.VERSION_NAME);
                    if (oldVersion.isNMinorVersionsDiff(newVersion,
                            AppConstants.MINOR_VERSIONS_DIFF_REQUIRED_FOR_WHATS_NEW)) {
                        shouldShowWhatsNew = true;
                    }
                } catch (ParseException e) {
                    shouldShowWhatsNew = false;
                    logger.error(e);
                }
            }
            if (shouldShowWhatsNew) {
                environment.getRouter().showWhatsNewActivity(this);
                showWhatsNew = true;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            /* This is the main Activity, and is where the new version availability
             * notifications are being posted. These events are posted as sticky so
             * that they can be compared against new instances of them to be posted
             * in order to determine whether it has new information content. The
             * events have an intrinsic property to mark them as consumed, in order
             * to not have to remove the sticky events (and thus lose the last
             * posted event information). Finishing this Activity should be
             * considered as closing the current session, and the notifications
             * should be reposted on a new session. Therefore, we clear the session
             * information by removing the sticky new version availability events
             * from the event bus.
             */
            EventBus.getDefault().removeStickyEvent(NewVersionAvailableEvent.class);
        }
        if (null != getAccountCall) {
            getAccountCall.cancel();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        notificationDelegate.checkAppUpgrade();
    }


    /**
     * Event bus callback for new app version availability event.
     *
     * @param newVersionAvailableEvent The new app version availability event.
     */
    public void onEvent(@NonNull final NewVersionAvailableEvent newVersionAvailableEvent) {
        if (!newVersionAvailableEvent.isConsumed()) {
            final Snackbar snackbar = Snackbar.make(rootView,
                    newVersionAvailableEvent.getNotificationString(this),
                    Snackbar.LENGTH_INDEFINITE);
            if (AppStoreUtils.canUpdate(this)) {
                snackbar.setAction(R.string.label_update,
                        AppStoreUtils.OPEN_APP_IN_APP_STORE_CLICK_LISTENER);
            }
            snackbar.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    newVersionAvailableEvent.markAsConsumed();
                }
            });
            snackbar.show();
        }
    }

    @Override
    public void onTabSelected(int position) {
        mViewPager.setCurrentItem(position);

    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void hideSnackBar() {

    }

    @Override
    public void resetSnackbarVisibility(boolean fullScreenErrorVisibility) {

    }


    public void sendGetUpdatedAccountCall() {
        profile = loginPrefs.getCurrentUserProfile();
        getAccountCall = userService.getAccount(profile.username);
        getAccountCall.enqueue(new UserAPI.AccountDataUpdatedCallback(
                BottomNavigationMainDashboardActivity.this,
                profile.username,
                null, // Disable global loading indicator
                null)); // No place to show an error notification
    }


    @SuppressWarnings("unused")
    public void onEventMainThread(@NonNull AccountDataLoadedEvent event) {
        if (!environment.getConfig().isUserProfilesEnabled()) {
            return;
        }
        final Account account = event.getAccount();
        if (account.getUsername().equalsIgnoreCase(profile.username)) {
            //保存account到sp
            accountPrefs.storeAccount(account);
            BindMobileUtil.getInstance().checkAccountMobile(account,this);

        }
    }

    private void initDiscoveryTabEvent(){
        RxBus.getDefault().toObservable(MoveToDiscoveryTabEvent.class).subscribe(moveToDiscoveryTabEvent -> mBottomNavigationBar.selectTab(1));
    }
}
