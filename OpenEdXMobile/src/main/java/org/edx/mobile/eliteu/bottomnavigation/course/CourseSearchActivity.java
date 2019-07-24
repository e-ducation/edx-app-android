package org.edx.mobile.eliteu.bottomnavigation.course;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.gyf.immersionbar.ImmersionBar;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragmentActivity;
import org.edx.mobile.course.CourseDetail;
import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.eliteu.wight.DeleteEditText;
import org.edx.mobile.http.callback.Callback;
import org.edx.mobile.model.Page;
import org.edx.mobile.module.prefs.LoginPrefs;
import org.edx.mobile.util.SoftKeyboardUtil;
import org.edx.mobile.view.Router;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class CourseSearchActivity extends BaseFragmentActivity {


    DeleteEditText et_search;
    TextView tv_cancle;
    RecyclerView recyclerView;
    LinearLayout noDataLayout;
    CourseSearchResultAdapter mCourseSearchResultAdapter;
    private CompositeDisposable mCompositeDisposable;

    @Inject
    private EliteApi eliteApi;
    private ImmersionBar mImmersionBar;
    @Inject
    private LoginPrefs loginPrefs;
    @Inject
    private Router router;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_search);
        mCompositeDisposable = new CompositeDisposable();
        initUi();
        initStatusBar();
    }

    private void initStatusBar() {
        mImmersionBar = ImmersionBar.with(this)
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true)
                .fitsSystemWindows(true);  //透明状态栏，不写默认透明色
        mImmersionBar.init();
    }

    private void initUi() {
        et_search = findViewById(R.id.et_search);
        initEditTextEditorAction(et_search);

        noDataLayout = findViewById(R.id.no_data_layout);

        tv_cancle = findViewById(R.id.tv_cancle);
        initCancleListener(tv_cancle);

        recyclerView = findViewById(R.id.search_result);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(this, R.color.mian_site_divider_color)));
        mCourseSearchResultAdapter = new CourseSearchResultAdapter(this);
        mCourseSearchResultAdapter.setOnItemClickListener(result -> goToCourseDetail(result));
        recyclerView.setAdapter(mCourseSearchResultAdapter);

        loadData();
    }

    /**
     * EditText输入监听
     */
    private void loadData() {
        addDisposable(
                RxTextView.textChanges(et_search).skip(1)
                        .debounce(200, TimeUnit.MILLISECONDS)
                        .map(charSequence -> charSequence.toString())
                        .observeOn(AndroidSchedulers.mainThread())
                        .filter(s -> {
                            if (s.length() > 0) {
                                return true;
                            } else {
                                List<CourseDetail> list = new ArrayList<>();
                                mCourseSearchResultAdapter.notifyDataSetChanged(list);
                                noDataLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                return false;
                            }
                        })
                        .observeOn(Schedulers.io())
                        .switchMap((Function<String, ObservableSource<Page<CourseDetail>>>) s -> eliteApi.getCourseSearchByKeyWord(s))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(getConsumerResult(), getConsumerThrowable())
        );
    }


    /**
     * 取消按钮监听事件
     */
    private void initCancleListener(TextView tv_cancle) {
        RxView.clicks(tv_cancle)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> {
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                });
    }

    /**
     * 软键盘搜索按钮监听
     */
    private void initEditTextEditorAction(DeleteEditText et_search) {
        RxTextView.editorActions(et_search)
                .throttleFirst(1, TimeUnit.SECONDS)
                .map(integer -> et_search.getText().toString())
                .filter(s -> s.length() > 0 ? true : false)
                .subscribe(s -> requestSearchByKeyWord(s));
    }

    private void requestSearchByKeyWord(String keyword) {
        addDisposable(
                eliteApi.getCourseSearchByKeyWord(keyword)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(getConsumerResult(), getConsumerThrowable())
        );
    }

    private void goToCourseDetail(String course_id) {

        eliteApi.getCourseDetail(course_id, loginPrefs.getUsername()).enqueue(new Callback<CourseDetail>() {
            @Override
            protected void onResponse(@NonNull CourseDetail responseBody) {
                if (isDestroyed()) {
                    return;
                }
                if (responseBody != null) {
                    router.showCourseDetail(CourseSearchActivity.this, responseBody);
                } else {
                    Toast.makeText(CourseSearchActivity.this, R.string.error_unknown, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onFailure(@NonNull Throwable error) {
                super.onFailure(error);
                if (isDestroyed()) {
                    return;
                }
                error.printStackTrace();
                Toast.makeText(CourseSearchActivity.this, R.string.error_unknown, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    private Consumer<Page<CourseDetail>> getConsumerResult() {
        return courseDetailPage -> {
            List<CourseDetail> list = courseDetailPage.getResults();

            if (list.size() == 0) {
                recyclerView.setVisibility(View.GONE);
                noDataLayout.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                noDataLayout.setVisibility(View.GONE);
                mCourseSearchResultAdapter.notifyDataSetChanged(list);
            }
        };
    }

    private Consumer<Throwable> getConsumerThrowable() {
        return throwable -> {
            throwable.printStackTrace();
            recyclerView.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.GONE);
        };

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SoftKeyboardUtil.hide(CourseSearchActivity.this);
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.clear();
        }
    }

    public void addDisposable(Disposable disposable) {
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.add(disposable);
        }
    }

}
