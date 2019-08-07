package org.edx.mobile.eliteu.bottomnavigation.course;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.inject.Inject;
import com.jakewharton.rxbinding3.view.RxView;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragment;
import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.eliteu.wight.EqualizationTabLayout;
import org.edx.mobile.http.notifications.FullScreenErrorNotification;
import org.edx.mobile.util.NetworkUtil;
import org.edx.mobile.view.custom.IconProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class EliteuFindCourseFragment extends BaseFragment {

    private EqualizationTabLayout mTabLayout;
    private ViewPager mViewPager;
    private LinearLayout searchLayout;
    private FullScreenErrorNotification errorNotification;
    private IconProgressBar mIconProgressBar;
    private CompositeDisposable mCompositeDisposable;
    @Inject
    private EliteApi eliteApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eliteu_find_course, container, false);
        initView(view);
        return view;
    }

    public EliteuFindCourseFragment(){

    }

    private void initView(View view) {
        searchLayout = view.findViewById(R.id.search_layout);
        RxView.clicks(searchLayout)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> {
                    getActivity().startActivity(new Intent(getActivity(), CourseSearchActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });
        mTabLayout = view.findViewById(R.id.tabs);

        setupTablayout(mTabLayout);

        mViewPager = view.findViewById(R.id.view_pager);

        mCompositeDisposable = new CompositeDisposable();
        mIconProgressBar = view.findViewById(R.id.loading_indicator);

        errorNotification = new FullScreenErrorNotification(mViewPager);
        loadData();
    }

    private void setupTablayout(EqualizationTabLayout mTabLayout) {

        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.eliteu_findcourse_tablayout_item, null);

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

    private void loadData() {
        if (!NetworkUtil.isConnected(getActivity())) {
            showNetwordisNotConnected();
            return;
        }
        mIconProgressBar.setVisibility(View.VISIBLE);
        Disposable disposable = eliteApi.getCourseSubject()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    errorNotification.hideError();
                    setSubjectTags(result.getResults());
                }, throwable -> {
                    throwable.printStackTrace();
                    mIconProgressBar.setVisibility(View.GONE);
                    showError(throwable);
                });
        addDisposable(disposable);
    }

    private void setSubjectTags(List<CourseSubjectBean> courseSubjectBeans) {

        EliteuFindCourseFragment.SectionsPagerAdapter adapter = new EliteuFindCourseFragment.SectionsPagerAdapter(getActivity().getSupportFragmentManager());

        adapter.addFragment(FindCourseListFragment.newInstance(0), getResources().getString(R.string.all));

        for (CourseSubjectBean subjectBean : courseSubjectBeans) {
            adapter.addFragment(FindCourseListFragment.newInstance(subjectBean.getId()), subjectBean.getSubject_name());
        }

        showContent();
        mViewPager.setAdapter(adapter);
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
        errorNotification.showError(getActivity(), throwable, R.string.lbl_reload,
                v -> {
                    if (NetworkUtil.isConnected(getActivity())) {
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
        mIconProgressBar.setVisibility(View.GONE);
    }

}
