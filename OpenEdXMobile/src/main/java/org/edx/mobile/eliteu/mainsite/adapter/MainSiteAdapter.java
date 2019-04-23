package org.edx.mobile.eliteu.mainsite.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.eliteu.article.ArticleActivity;
import org.edx.mobile.eliteu.mainsite.bean.BlockCourseCategory.CategorieslistBean;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding3.view.RxView;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;
import com.youth.banner.Banner;

import org.edx.mobile.R;
import org.edx.mobile.eliteu.mainsite.bean.BlockCourseCategory;
import org.edx.mobile.eliteu.mainsite.bean.BlockOtherImg;
import org.edx.mobile.eliteu.mainsite.bean.BlockProfessor;
import org.edx.mobile.eliteu.mainsite.bean.BlockRecommendCourse;
import org.edx.mobile.eliteu.mainsite.bean.BlockSeriesCourse;
import org.edx.mobile.eliteu.mainsite.bean.BlockStory;
import org.edx.mobile.eliteu.mainsite.bean.MainSiteCourseBean;
import org.edx.mobile.eliteu.mainsite.util.GlideImageLoader;
import org.edx.mobile.eliteu.mainsite.bean.BaseMainSiteBlockBean;
import org.edx.mobile.eliteu.mainsite.bean.BlockBanner;
import org.edx.mobile.eliteu.professor.ProfessorListActivity;
import org.edx.mobile.eliteu.util.AndroidSizeUtil;
import org.edx.mobile.eliteu.wight.CourseGridLayoutManager;
import org.edx.mobile.eliteu.wight.GridSectionAverageGapItemDecoration;
import org.edx.mobile.eliteu.wight.SpaceOrientationItemDecoration;
import org.edx.mobile.util.Config;
import org.edx.mobile.view.Router;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class MainSiteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_BANNER = 1;
    public static final int TYPE_COURSE_CATEGORY = 2;
    public static final int TYPE_SUITABLE_COURSE = 3;
    public static final int TYPE_RECOMMEND_COURSE = 4;
    public static final int TYPE_RECOMMEND_PROFESSOR = 5;
    public static final int TYPE_USER_STORY = 6;
    public static final int TYPE_IMAGE = 7;

    Context mContext;
    FragmentActivity fragmentActivity;
    Router router;
    Config config;
    Gson gson;
    EliteApi eliteApi;
    String username;
    LayoutInflater mLayoutInflater;
    List<BaseMainSiteBlockBean> mBodyList;


    public MainSiteAdapter(Context context, Router router, Config config, EliteApi eliteApi, String username) {
        this.mContext = context;
        fragmentActivity = (FragmentActivity) context;
        this.router = router;
        this.config = config;
        this.username = username;
        this.eliteApi = eliteApi;
        this.gson = new Gson();
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public void setData(List<BaseMainSiteBlockBean> bodyBeans) {
        this.mBodyList = bodyBeans;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_BANNER) {
            return new BannerViewHolder(mLayoutInflater.inflate(R.layout.template_banner, parent, false));
        } else if (viewType == TYPE_COURSE_CATEGORY) {
            return new RecyclerViewHolder(mLayoutInflater.inflate(R.layout.main_site_template_recycler_view_with_title, null, false), LinearLayoutManager.HORIZONTAL, 10, SpaceOrientationItemDecoration.HORIZONTAL);
        } else if (viewType == TYPE_SUITABLE_COURSE) {
            return new RecyclerViewHolder(mLayoutInflater.inflate(R.layout.main_site_template_recycler_view_with_title, null, false), LinearLayoutManager.HORIZONTAL, 30, SpaceOrientationItemDecoration.HORIZONTAL);
        } else if (viewType == TYPE_RECOMMEND_COURSE) {
            return new GlidViewViewHolder(mLayoutInflater.inflate(R.layout.main_site_template_recycler_view_with_title, null, false));
        } else if (viewType == TYPE_RECOMMEND_PROFESSOR) {
            return new RecyclerViewHolder(mLayoutInflater.inflate(R.layout.main_site_template_recycler_view_with_title, null, false), LinearLayoutManager.VERTICAL, 50, SpaceOrientationItemDecoration.VERTICAL);
        } else if (viewType == TYPE_USER_STORY) {
            return new StoryRecyclerViewHolder(mLayoutInflater.inflate(R.layout.main_site_template_recycler_view_with_title, null, false));
        } else if (viewType == TYPE_IMAGE) {
            return new ImageViewHolder(mLayoutInflater.inflate(R.layout.main_site_img_layout, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            BaseMainSiteBlockBean baseMainSiteBlockBean = mBodyList.get(0);
            if (baseMainSiteBlockBean.getType().equals("Banner")) {
                BlockBanner bodyBean = gson.fromJson(JSONObject.wrap(baseMainSiteBlockBean.getValue()).toString(), new TypeToken<BlockBanner>() {
                }.getType());
                List<BlockBanner.BannersBean> bannersList = bodyBean.getBanners();
                List<String> images = new ArrayList<>();
                for (BlockBanner.BannersBean banner : bannersList) {
                    images.add(config.getApiHostURL() + banner.getMobile_image());
                }
                BannerViewHolder bannerViewHolder = (BannerViewHolder) holder;
                setBanner(bannerViewHolder.banner);
                bannerViewHolder.banner.setImages(images)
                        .setDelayTime(bodyBean.getLoop_time())
                        .setImageLoader(new GlideImageLoader())
                        .setOnBannerListener(position1 -> {
                            router.showCustomWebviewActivity(fragmentActivity, bannersList.get(position1).getLink(), mContext.getString(R.string.webview_title));

                        })
                        .start();
            }
        } else if (position == 1) {
            BaseMainSiteBlockBean baseMainSiteBlockBean = mBodyList.get(1);
            if (baseMainSiteBlockBean.getType().equals("CategoriesListBlock")) {
                BlockCourseCategory bodyBean = gson.fromJson(JSONObject.wrap(baseMainSiteBlockBean.getValue()).toString(), new TypeToken<BlockCourseCategory>() {
                }.getType());
                List<CategorieslistBean> mCategoryList = bodyBean.getCategorieslist();
                RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;
                CourseCategoryAdapter courseCategoryAdapter = new CourseCategoryAdapter(mCategoryList, mContext, router);
                viewHolder.mText.setText(R.string.course_category);
                viewHolder.mRecyclerView.setAdapter(courseCategoryAdapter);
            }
        } else if (position == 2) {
            BaseMainSiteBlockBean baseMainSiteBlockBean = mBodyList.get(2);
            if (baseMainSiteBlockBean.getType().equals("SeriesCourse")) {
                BlockSeriesCourse bodyBean = gson.fromJson(JSONObject.wrap(baseMainSiteBlockBean.getValue()).toString(), new TypeToken<BlockSeriesCourse>() {
                }.getType());
                List<MainSiteCourseBean> seriesList = bodyBean.getSeries();
                RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;
                MainSiteCourseAdapter mainSiteCourseAdapter = MainSiteCourseAdapter.builder().conetxt(mContext).config(config).router(router).type(MainSiteCourseAdapter.TYPE_URL).build();
                viewHolder.mText.setText(R.string.suitable_course);
                mainSiteCourseAdapter.setData(seriesList);
                viewHolder.mRecyclerView.setAdapter(mainSiteCourseAdapter);
                viewHolder.divider.setVisibility(View.VISIBLE);
            }
        } else if (position == 3) {
            BaseMainSiteBlockBean baseMainSiteBlockBean = mBodyList.get(3);
            if (baseMainSiteBlockBean.getType().equals("RecommendCourse")) {
                BlockRecommendCourse bodyBean = gson.fromJson(JSONObject.wrap(baseMainSiteBlockBean.getValue()).toString(), new TypeToken<BlockRecommendCourse>() {
                }.getType());
                List<MainSiteCourseBean> list = bodyBean.getCourses();
                GlidViewViewHolder viewViewHolder = (GlidViewViewHolder) holder;
                MainSiteCourseAdapter mainSiteCourseAdapter = MainSiteCourseAdapter.builder().layout(R.layout.main_site_course_griview_vertical_item).conetxt(mContext).config(config).type(MainSiteCourseAdapter.TYPE_COURSE).router(router).eliteapi(eliteApi).username(username).build();
                viewViewHolder.mText.setText(R.string.tuijian_course);
                viewViewHolder.mRightLayout.setVisibility(View.VISIBLE);
                viewViewHolder.mText2.setText(R.string.all_course);
                mainSiteCourseAdapter.setData(list);
                viewViewHolder.mRecyclerView.setAdapter(mainSiteCourseAdapter);
                viewViewHolder.divider.setVisibility(View.VISIBLE);
            }
        } else if (position == 4) {
            BaseMainSiteBlockBean baseMainSiteBlockBean = mBodyList.get(4);
            if (baseMainSiteBlockBean.getType().equals("ProfessorBlock")) {
                BlockProfessor bodyBean = gson.fromJson(JSONObject.wrap(baseMainSiteBlockBean.getValue()).toString(), new TypeToken<BlockProfessor>() {
                }.getType());
                List<BlockProfessor.ProfessorBean> list = bodyBean.getProfessor();

                RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;
                MainSiteProfessorAdapter professorAdapter = new MainSiteProfessorAdapter(mContext, config, router);
                viewHolder.mText.setText(R.string.tuijian_professor);
                viewHolder.mRightLayout.setVisibility(View.VISIBLE);
                viewHolder.mText2.setText(R.string.all_professor);
                professorAdapter.setData(list);
                viewHolder.mRecyclerView.setAdapter(professorAdapter);
                viewHolder.divider.setVisibility(View.VISIBLE);
                RxView.clicks(viewHolder.mRightLayout).throttleFirst(1, TimeUnit.SECONDS)
                        .subscribe(unit -> mContext.startActivity(new Intent(mContext, ProfessorListActivity.class)));
            }
        } else if (position == 5) {
            BaseMainSiteBlockBean baseMainSiteBlockBean = mBodyList.get(5);
            if (baseMainSiteBlockBean.getType().equals("StoryBlock")) {
                BlockStory bodyBean = gson.fromJson(JSONObject.wrap(baseMainSiteBlockBean.getValue()).toString(), new TypeToken<BlockStory>() {
                }.getType());
                List<BlockStory.StoryBean> list = bodyBean.getStory();
                StoryRecyclerViewHolder viewHolder = (StoryRecyclerViewHolder) holder;
                MainSiteStoryAdapter mainSiteStoryAdapter = new MainSiteStoryAdapter(mContext, config, router);
                viewHolder.mText.setText(R.string.user_story);
                viewHolder.mRightLayout.setVisibility(View.VISIBLE);
                viewHolder.mText2.setText(R.string.all_story);
                mainSiteStoryAdapter.setData(list);
                viewHolder.mRecyclerView.setAdapter(mainSiteStoryAdapter);
                RxView.clicks(viewHolder.mRightLayout).throttleFirst(1, TimeUnit.SECONDS)
                        .subscribe(unit -> mContext.startActivity(new Intent(mContext, ArticleActivity.class)));
            }
        } else if (position == 6) {
            BaseMainSiteBlockBean baseMainSiteBlockBean = mBodyList.get(6);
            BlockOtherImg bodyBean = gson.fromJson(JSONObject.wrap(baseMainSiteBlockBean.getValue()).toString(), new TypeToken<BlockOtherImg>() {
            }.getType());
            ImageViewHolder viewHolder = (ImageViewHolder) holder;
            Glide.with(mContext).load(config.getApiHostURL() + bodyBean.getImg_for_MOBILE()).placeholder(R.drawable.main_site_block_img_default).error(R.drawable.main_site_block_img_default).into(viewHolder.imageView);
            RxView.clicks(viewHolder.imageView).throttleFirst(1, TimeUnit.SECONDS).subscribe(unit -> router.showCustomWebviewActivity(fragmentActivity, bodyBean.getLink(), mContext.getString(R.string.webview_title)));
        }
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_BANNER;
        } else if (position == 1) {
            return TYPE_COURSE_CATEGORY;
        } else if (position == 2) {
            return TYPE_SUITABLE_COURSE;
        } else if (position == 3) {
            return TYPE_RECOMMEND_COURSE;
        } else if (position == 4) {
            return TYPE_RECOMMEND_PROFESSOR;
        } else if (position == 5) {
            return TYPE_USER_STORY;
        } else if (position == 6) {
            return TYPE_IMAGE;
        }
        return 0;
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {

        Banner banner;

        public BannerViewHolder(View itemView) {
            super(itemView);
            banner = itemView.findViewById(R.id.banner);
        }
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView mText;
        TextView mText2;
        LinearLayout mRightLayout;
        RecyclerView mRecyclerView;
        int decoration;
        int linearLayoutManagerOrientation;
        int space;
        View divider;

        public RecyclerViewHolder(View itemView, int linearLayoutManagerOrientation, int space, int decoration) {
            super(itemView);
            this.linearLayoutManagerOrientation = linearLayoutManagerOrientation;
            this.space = space;
            this.decoration = decoration;
            mText = itemView.findViewById(R.id.title);
            mText2 = itemView.findViewById(R.id.text2);
            divider = itemView.findViewById(R.id.divider);
            this.mRightLayout = itemView.findViewById(R.id.title2);
            mRecyclerView = itemView.findViewById(R.id.recycler_view);
            initRecyclerView();
        }

        private void initRecyclerView() {

            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, this.linearLayoutManagerOrientation, false);

            mRecyclerView.setLayoutManager(layoutManager);

            mRecyclerView.addItemDecoration(new SpaceOrientationItemDecoration(this.space, this.decoration));

            mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        }
    }

    class StoryRecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView mText;
        TextView mText2;
        LinearLayout mRightLayout;
        RecyclerView mRecyclerView;

        public StoryRecyclerViewHolder(View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.title);
            mText2 = itemView.findViewById(R.id.text2);
            this.mRightLayout = itemView.findViewById(R.id.title2);
            mRecyclerView = itemView.findViewById(R.id.recycler_view);
            initRecyclerView();

        }

        private void initRecyclerView() {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(mContext, R.color.mian_site_divider_color)));
            mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }

    class GlidViewViewHolder extends RecyclerView.ViewHolder {

        TextView mText;
        TextView mText2;
        LinearLayout mRightLayout;
        RecyclerView mRecyclerView;
        View divider;

        public GlidViewViewHolder(View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.title);
            mText2 = itemView.findViewById(R.id.text2);
            this.mRightLayout = itemView.findViewById(R.id.title2);
            mRecyclerView = itemView.findViewById(R.id.recycler_view);
            divider = itemView.findViewById(R.id.divider);
            initRecyclerView();
        }

        private void initRecyclerView() {
            CourseGridLayoutManager layoutManager = new CourseGridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
            int leftRight = AndroidSizeUtil.dp2Px(mContext, 5);
            int topBottom = AndroidSizeUtil.dp2Px(mContext, 5);
            mRecyclerView.addItemDecoration(new GridSectionAverageGapItemDecoration(leftRight, topBottom, 0, 0));
            mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
        }
    }


    public Banner getBanner() {
        return this.mBanner;
    }

    private Banner mBanner;

    public void setBanner(Banner banner) {
        this.mBanner = banner;
    }

}
