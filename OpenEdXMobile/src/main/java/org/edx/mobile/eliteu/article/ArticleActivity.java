package org.edx.mobile.eliteu.article;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.inject.Inject;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragmentActivity;
import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.eliteu.wight.EqualizationTabLayout;
import org.edx.mobile.http.notifications.FullScreenErrorNotification;
import org.edx.mobile.util.NetworkUtil;
import org.edx.mobile.view.custom.IconProgressBar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ArticleActivity extends BaseFragmentActivity {

    private EqualizationTabLayout mTabLayout;
    private ViewPager mViewPager;
    private FullScreenErrorNotification errorNotification;
    private IconProgressBar mIconProgressBar;
    private Toolbar mToolbar;
    private AppBarLayout mAppBarLayout;
    private View line;
    private CompositeDisposable mCompositeDisposable;

    @Inject
    private EliteApi eliteApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
        super.setToolbarAsActionBar();
        setTitle(R.string.article_title);
        initView();
    }

    private void initView() {
        mCompositeDisposable = new CompositeDisposable();
        mToolbar = findViewById(R.id.toolbar);
        mAppBarLayout = findViewById(R.id.appbar);
        line = findViewById(R.id.line);
        mIconProgressBar = findViewById(R.id.loading_indicator);
        mTabLayout = findViewById(R.id.tabs);
        setupTablayout(mTabLayout);
        mAppBarLayout.setTargetElevation(0);

        mViewPager = findViewById(R.id.view_pager);
        errorNotification = new FullScreenErrorNotification(mViewPager);
        loadData();
    }

    private void loadData() {
        if (!NetworkUtil.isConnected(this)) {
            showNetwordisNotConnected();
            return;
        }
        mIconProgressBar.setVisibility(View.VISIBLE);
        Disposable disposable = eliteApi.getArticleTags()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(articleTagBean -> {
                    errorNotification.hideError();
                    setUpArticleTag(articleTagBean);
                }, throwable -> {
                    throwable.printStackTrace();
                    mIconProgressBar.setVisibility(View.GONE);
                    showError(throwable);
                });
        addDisposable(disposable);
    }

    private void setUpArticleTag(ArticleTagBean articleTagBean) {
        List<ArticleTagBean.ItemsBean> tagsList = articleTagBean.getItems();


        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ArticleListFragment.newInstance("-article_datetime", ""), getString(R.string.newest));
        adapter.addFragment(ArticleListFragment.newInstance("-liked_count", ""), getString(R.string.hotest));

        for (ArticleTagBean.ItemsBean tag : tagsList) {
            adapter.addFragment(ArticleListFragment.newInstance("", tag.getName()), tag.getName());
        }

        AppBarLayout.LayoutParams mLayoutParams = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();

        mLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

        mToolbar.setLayoutParams(mLayoutParams);
        showContent();

        mViewPager.setAdapter(adapter);
//        mViewPager.setOffscreenPageLimit(adapter.mFragmentList.size() - 1);
        mTabLayout.setupWithViewPager(mViewPager);


    }

    public void addDisposable(Disposable disposable) {
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.add(disposable);
        }
    }

    private void showNetwordisNotConnected() {
        errorNotification.showError(getResources().getString(R.string.reset_no_network_message),
                FontAwesomeIcons.fa_wifi, R.string.lbl_reload, v -> loadData());
    }

    private void showError(Throwable throwable) {
        errorNotification.showError(ArticleActivity.this, throwable, R.string.lbl_reload,
                v -> {
                    if (NetworkUtil.isConnected(ArticleActivity.this)) {
                        loadData();
                    }
                });
    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.clear();
        }
    }

    private void showContent() {
        mTabLayout.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        line.setVisibility(View.VISIBLE);
        mIconProgressBar.setVisibility(View.GONE);
    }

    private void setupTablayout(EqualizationTabLayout mTabLayout) {

        final View view = LayoutInflater.from(this).inflate(R.layout.eliteu_findcourse_tablayout_item, null);

        TextView txt_title = view.findViewById(R.id.txt_title);
        View tabIndicatorView = view.findViewById(R.id.tabIndicatorView);

        mTabLayout.getTabAt(0);
        mTabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                txt_title.setText(tab.getText());
                txt_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                tabIndicatorView.setVisibility(View.VISIBLE);
                tab.setCustomView(view);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(null);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}
