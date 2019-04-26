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
import org.edx.mobile.event.MoveToDiscoveryTabEvent;
import org.edx.mobile.util.Config;
import org.edx.mobile.view.Router;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

public class MainSiteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_BANNER = 1;
    public static final int TYPE_COURSE_CATEGORY = 2;
    public static final int TYPE_SUITABLE_COURSE = 3;
    public static final int TYPE_RECOMMEND_COURSE = 4;
    public static final int TYPE_RECOMMEND_PROFESSOR = 5;
    public static final int TYPE_USER_STORY = 6;
    public static final int TYPE_IMAGE = 7;
    public static final int TYPE_UNKNOW = 8;

    public static final String BLOCK_TYPE_BANNER = "Banner";
    public static final String BLOCK_TYPE_COURSE_CATEGORY = "CategoriesListBlock";
    public static final String BLOCK_TYPE_SUITABLE_COURSE = "SeriesCourse";
    public static final String BLOCK_TYPE_RECOMMEND_COURSE = "RecommendCourse";
    public static final String BLOCK_TYPE_RECOMMEND_PROFESSOR = "ProfessorBlock";
    public static final String BLOCK_TYPE_USER_STORY = "StoryBlock";
    public static final String BLOCK_TYPE_IMAGE = "OtherImgBlock";

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
            return new StoryRecyclerViewHolder(mLayoutInflater.inflate(R.layout.main_site_template_recycler_view_with_title_no_margin, null, false));
        } else if (viewType == TYPE_IMAGE) {
            return new ImageViewHolder(mLayoutInflater.inflate(R.layout.main_site_img_layout, parent, false));
        } else if (viewType == TYPE_UNKNOW) {
            return new UnKnowBlockViewHolder(mLayoutInflater.inflate(R.layout.mait_site_unknow_block_type_layout, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        BaseMainSiteBlockBean baseMainSiteBlockBean = mBodyList.get(position);
        switch (baseMainSiteBlockBean.getType()) {
            case BLOCK_TYPE_BANNER:
                BlockBanner blockBanner = gson.fromJson(JSONObject.wrap(baseMainSiteBlockBean.getValue()).toString(), new TypeToken<BlockBanner>() {
                }.getType());
                List<BlockBanner.BannersBean> bannersList = blockBanner.getBanners();
                List<String> images = new ArrayList<>();
                for (BlockBanner.BannersBean banner : bannersList) {
                    images.add(config.getApiHostURL() + banner.getMobile_image());
                }
                BannerViewHolder bannerViewHolder = (BannerViewHolder) holder;
                setBanner(bannerViewHolder.banner);
                bannerViewHolder.banner.setImages(images)
                        .setDelayTime(blockBanner.getLoop_time())
                        .setImageLoader(new GlideImageLoader())
                        .setOnBannerListener(position1 -> {
                            router.showCustomWebviewActivity(fragmentActivity, bannersList.get(position1).getLink(), mContext.getString(R.string.webview_title));
                        })
                        .start();
                break;
            case BLOCK_TYPE_COURSE_CATEGORY:
                BlockCourseCategory blockCourseCategory = gson.fromJson(JSONObject.wrap(baseMainSiteBlockBean.getValue()).toString(), new TypeToken<BlockCourseCategory>() {
                }.getType());
                List<CategorieslistBean> mCategoryList = blockCourseCategory.getCategorieslist();
                RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
                CourseCategoryAdapter courseCategoryAdapter = new CourseCategoryAdapter(mCategoryList, mContext, router, config);
                recyclerViewHolder.mText.setText(blockCourseCategory.getTitle());
                recyclerViewHolder.mRecyclerView.setAdapter(courseCategoryAdapter);
                break;
            case BLOCK_TYPE_SUITABLE_COURSE:
                BlockSeriesCourse blockSeriesCourse = gson.fromJson(JSONObject.wrap(baseMainSiteBlockBean.getValue()).toString(), new TypeToken<BlockSeriesCourse>() {
                }.getType());
                List<MainSiteCourseBean> seriesList = blockSeriesCourse.getSeries();
                RecyclerViewHolder recyclerViewHolder1 = (RecyclerViewHolder) holder;
                MainSiteCourseAdapter mainSiteCourseAdapter = MainSiteCourseAdapter.builder().conetxt(mContext).config(config).router(router).type(MainSiteCourseAdapter.TYPE_URL).build();
                recyclerViewHolder1.mText.setText(blockSeriesCourse.getTitle());
                mainSiteCourseAdapter.setData(seriesList);
                recyclerViewHolder1.mRecyclerView.setAdapter(mainSiteCourseAdapter);
                recyclerViewHolder1.divider.setVisibility(View.VISIBLE);
                break;
            case BLOCK_TYPE_RECOMMEND_COURSE:
                BlockRecommendCourse blockRecommendCourse = gson.fromJson(JSONObject.wrap(baseMainSiteBlockBean.getValue()).toString(), new TypeToken<BlockRecommendCourse>() {
                }.getType());
                List<MainSiteCourseBean> list = blockRecommendCourse.getCourses();
                GlidViewViewHolder viewViewHolder = (GlidViewViewHolder) holder;
                MainSiteCourseAdapter mainSiteCourseAdapter1 = MainSiteCourseAdapter.builder().layout(R.layout.main_site_course_griview_vertical_item).conetxt(mContext).config(config).type(MainSiteCourseAdapter.TYPE_COURSE).router(router).eliteapi(eliteApi).username(username).build();
                viewViewHolder.mText.setText(blockRecommendCourse.getTitle());
                viewViewHolder.mRightLayout.setVisibility(View.VISIBLE);
                viewViewHolder.mText2.setText(R.string.all_course);
                mainSiteCourseAdapter1.setData(list);
                viewViewHolder.mRecyclerView.setAdapter(mainSiteCourseAdapter1);
                viewViewHolder.divider.setVisibility(View.VISIBLE);
                RxView.clicks(viewViewHolder.mRightLayout).throttleFirst(1, TimeUnit.SECONDS).subscribe(unit -> EventBus.getDefault().post(new MoveToDiscoveryTabEvent()));
                break;
            case BLOCK_TYPE_RECOMMEND_PROFESSOR:
                BlockProfessor blockProfessor = gson.fromJson(JSONObject.wrap(baseMainSiteBlockBean.getValue()).toString(), new TypeToken<BlockProfessor>() {
                }.getType());
                List<BlockProfessor.ProfessorBean> list1 = blockProfessor.getProfessor();

                RecyclerViewHolder recyclerViewHolder2 = (RecyclerViewHolder) holder;
                MainSiteProfessorAdapter professorAdapter = new MainSiteProfessorAdapter(mContext, config, router);
                recyclerViewHolder2.mText.setText(blockProfessor.getTitle());
                recyclerViewHolder2.mRightLayout.setVisibility(View.VISIBLE);
                recyclerViewHolder2.mText2.setText(R.string.all_professor);
                professorAdapter.setData(list1);
                recyclerViewHolder2.mRecyclerView.setAdapter(professorAdapter);
                recyclerViewHolder2.divider.setVisibility(View.VISIBLE);
                RxView.clicks(recyclerViewHolder2.mRightLayout).throttleFirst(1, TimeUnit.SECONDS)
                        .subscribe(unit -> mContext.startActivity(new Intent(mContext, ProfessorListActivity.class)));
                break;
            case BLOCK_TYPE_USER_STORY:
                BlockStory blockStory = gson.fromJson(JSONObject.wrap(baseMainSiteBlockBean.getValue()).toString(), new TypeToken<BlockStory>() {
                }.getType());
                List<BlockStory.StoryBean> list2 = blockStory.getStory();
                StoryRecyclerViewHolder viewHolder = (StoryRecyclerViewHolder) holder;
                MainSiteStoryAdapter mainSiteStoryAdapter = new MainSiteStoryAdapter(mContext, config, router);
                viewHolder.mText.setText(blockStory.getTitle());
                viewHolder.mRightLayout.setVisibility(View.VISIBLE);
                viewHolder.mText2.setText(R.string.all_story);
                mainSiteStoryAdapter.setData(list2);
                viewHolder.mRecyclerView.setAdapter(mainSiteStoryAdapter);
                RxView.clicks(viewHolder.mRightLayout).throttleFirst(1, TimeUnit.SECONDS)
                        .subscribe(unit -> mContext.startActivity(new Intent(mContext, ArticleActivity.class)));
                break;
            case BLOCK_TYPE_IMAGE:
                BlockOtherImg blockOtherImg = gson.fromJson(JSONObject.wrap(baseMainSiteBlockBean.getValue()).toString(), new TypeToken<BlockOtherImg>() {
                }.getType());
                ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                Glide.with(mContext).load(config.getApiHostURL() + blockOtherImg.getImg_for_MOBILE()).placeholder(R.drawable.main_site_block_img_default).error(R.drawable.main_site_block_img_default).into(imageViewHolder.imageView);
                RxView.clicks(imageViewHolder.imageView).throttleFirst(1, TimeUnit.SECONDS).subscribe(unit -> router.showCustomWebviewActivity(fragmentActivity, blockOtherImg.getLink(), mContext.getString(R.string.webview_title)));
                break;
            default:
        }


    }

    @Override
    public int getItemCount() {
        return mBodyList.size();
    }

    @Override
    public int getItemViewType(int position) {

        BaseMainSiteBlockBean baseMainSiteBlockBean = mBodyList.get(position);
        switch (baseMainSiteBlockBean.getType()) {
            case BLOCK_TYPE_BANNER:
                return TYPE_BANNER;
            case BLOCK_TYPE_COURSE_CATEGORY:
                return TYPE_COURSE_CATEGORY;
            case BLOCK_TYPE_SUITABLE_COURSE:
                return TYPE_SUITABLE_COURSE;
            case BLOCK_TYPE_RECOMMEND_COURSE:
                return TYPE_RECOMMEND_COURSE;
            case BLOCK_TYPE_RECOMMEND_PROFESSOR:
                return TYPE_RECOMMEND_PROFESSOR;
            case BLOCK_TYPE_USER_STORY:
                return TYPE_USER_STORY;
            case BLOCK_TYPE_IMAGE:
                return TYPE_IMAGE;
            default:
                return TYPE_UNKNOW;
        }

    }

    class BannerViewHolder extends RecyclerView.ViewHolder {

        Banner banner;

        public BannerViewHolder(View itemView) {
            super(itemView);
            banner = itemView.findViewById(R.id.banner);
        }
    }

    class UnKnowBlockViewHolder extends RecyclerView.ViewHolder {

        public UnKnowBlockViewHolder(View itemView) {
            super(itemView);
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
