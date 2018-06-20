package org.edx.mobile.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.edx.mobile.BuildConfig;
import org.edx.mobile.R;
import org.edx.mobile.databinding.FragmentWebviewCourseDiscoveryBinding;
import org.edx.mobile.databinding.SubjectItemBinding;
import org.edx.mobile.event.MainDashboardRefreshEvent;
import org.edx.mobile.event.NetworkConnectivityChangeEvent;
import org.edx.mobile.http.notifications.FullScreenErrorNotification;
import org.edx.mobile.logger.Logger;
import org.edx.mobile.model.SubjectModel;
import org.edx.mobile.module.analytics.Analytics;
import org.edx.mobile.util.FileUtil;
import org.edx.mobile.util.UiUtil;
import org.edx.mobile.util.ViewAnimationUtil;
import org.edx.mobile.util.images.ImageUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class WebViewDiscoverCoursesFragment extends BaseWebViewDiscoverFragment {
    private static final String QUERY_PARAM_SEARCH = "search_query";
    private static final String QUERY_PARAM_SUBJECT = "subject";
    private static final int VIEW_SUBJECTS_REQUEST_CODE = 999;

    private FragmentWebviewCourseDiscoveryBinding binding;
    private SearchView searchView;
    private MainDashboardToolbarCallbacks toolbarCallbacks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_webview_course_discovery, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        errorNotification = new FullScreenErrorNotification(binding.llContent);

        loadUrl(getInitialUrl());
        if (environment.getConfig().getCourseDiscoveryConfig().isSubectDiscoveryEnabled()) {
            initSubjects();
        }
        EventBus.getDefault().register(this);
    }

    private void initSubjects() {
        final String subjectItemsJson;
        try {
            subjectItemsJson = FileUtil.loadTextFileFromResources(getContext(), R.raw.subjects);
            final Type type = new TypeToken<List<SubjectModel>>() {
            }.getType();
            final List<SubjectModel> subjectModels = new Gson().fromJson(subjectItemsJson, type);
            final List<SubjectModel> popularSubjects = new ArrayList<>();
            for (SubjectModel subject : subjectModels) {
                if (subject.type == SubjectModel.Type.POPULAR) {
                    popularSubjects.add(subject);
                }
            }

            final RecyclerView.Adapter adapter = new RecyclerView.Adapter() {
                private SubjectItemBinding subjectItemBinding;

                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    switch (viewType) {
                        case 1:
                            final View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_subjects_item, parent, false);

                            return new RecyclerView.ViewHolder(viewItem) {
                                @Override
                                public String toString() {
                                    return super.toString();
                                }
                            };
                        default:
                            final View subjectItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_item, parent, false);

                            return new RecyclerView.ViewHolder(subjectItem) {
                                @Override
                                public String toString() {
                                    return super.toString();
                                }
                            };
                    }
                }

                @Override
                public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                    switch (getItemViewType(position)) {
                        case 1:
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    environment.getRouter().showSubjectsActivityForResult(WebViewDiscoverCoursesFragment.this,
                                            VIEW_SUBJECTS_REQUEST_CODE);
                                    environment.getAnalyticsRegistry().trackSubjectClicked(Analytics.Values.VIEW_ALL_SUBJECTS);
                                }
                            });
                            break;
                        default:
                            final SubjectModel model = popularSubjects.get(position);
                            subjectItemBinding = DataBindingUtil.bind(holder.itemView);
                            subjectItemBinding.tvSubjectName.setText(model.name);
                            @DrawableRes final int imageRes = UiUtil.getDrawable(getContext(), model.imageName);
                            ImageUtils.setRoundedCornerImage(subjectItemBinding.ivSubjectLogo, imageRes);

                            subjectItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String baseUrl = getInitialUrl();
                                    final String subjectFilter = popularSubjects.get(position).filter;
                                    final Map<String, String> queryParams = new HashMap<>();
                                    queryParams.put(QUERY_PARAM_SUBJECT, subjectFilter);
                                    String subjectFilterUrl = buildQuery(baseUrl, logger, queryParams);
                                    loadUrl(subjectFilterUrl);
                                    ViewAnimationUtil.animateViewFading(binding.llSubjectContent, View.GONE);
                                    environment.getAnalyticsRegistry().trackSubjectClicked(subjectFilter);
                                }
                            });
                            break;
                    }
                }

                @Override
                public int getItemCount() {
                    return popularSubjects.size() + 1;
                }

                @Override
                public int getItemViewType(int position) {
                    return position == getItemCount() - 1 ? 1 : 0;
                }
            };

            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false);
            binding.rvSubjects.setLayoutManager(linearLayoutManager);
            binding.rvSubjects.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbarCallbacks = (MainDashboardToolbarCallbacks) getActivity();
        initSearchView();
    }

    private void initSearchView() {
        searchView = toolbarCallbacks.getSearchView();
        searchView.setQueryHint(getResources().getString(R.string.search_for_courses));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                final String baseUrl = getInitialUrl();
                final Map<String, String> queryParams = new HashMap<>();
                queryParams.put(QUERY_PARAM_SEARCH, query);
                String searchUrl = buildQuery(baseUrl, logger, queryParams);
                searchView.onActionViewCollapsed();
                loadUrl(searchUrl);
                final boolean isLoggedIn = environment.getLoginPrefs().getUsername() != null;
                environment.getAnalyticsRegistry().trackCoursesSearch(query, isLoggedIn, BuildConfig.VERSION_NAME);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if (!queryTextFocused) {
                    toolbarCallbacks.getTitleView().setVisibility(View.VISIBLE);
                    searchView.onActionViewCollapsed();
                } else {
                    toolbarCallbacks.getTitleView().setVisibility(View.GONE);
                }
            }
        });
    }

    @NonNull
    protected String getInitialUrl() {
        return URLUtil.isValidUrl(binding.webview.getUrl()) ?
                binding.webview.getUrl() :
                environment.getConfig().getCourseDiscoveryConfig().getCourseSearchUrl();
    }

    public static String buildQuery(@NonNull String baseUrl, @NonNull Logger logger,
                                    @NonNull Map<String, String> queryParams) {
        StringBuilder finalUrl = new StringBuilder(baseUrl);
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String encodedParamValue = null;
            try {
                encodedParamValue = URLEncoder.encode(entry.getValue(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                logger.error(e);
            }
            final String queryParam = entry.getKey() + "=" + encodedParamValue;

            if (finalUrl.toString().contains("?")) {
                finalUrl.append("&").append(queryParam);
            } else {
                finalUrl.append("?").append(queryParam);
            }
        }
        return finalUrl.toString();
    }

    @Override
    public void onWebViewPartiallyUpdated() {
        if (binding.webview.getUrl().contains(QUERY_PARAM_SUBJECT)) {
            // It means that WebView just loaded subject related content
            ViewAnimationUtil.animateViewFading(binding.llSubjectContent, View.GONE);
        } else {
            ViewAnimationUtil.animateViewFading(binding.llSubjectContent, View.VISIBLE);
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(MainDashboardRefreshEvent event) {
        loadUrl(getInitialUrl());
    }

    @Override
    public void onRefresh() {
        EventBus.getDefault().post(new MainDashboardRefreshEvent());
    }

    @SuppressWarnings("unused")
    public void onEvent(NetworkConnectivityChangeEvent event) {
        onNetworkConnectivityChangeEvent(event);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (searchView != null) {
            searchView.setVisibility(isVisibleToUser ? View.VISIBLE : View.GONE);
            searchView.setIconified(!isVisibleToUser);
        }
    }

    @Override
    protected boolean isShowingFullScreenError() {
        return errorNotification != null && errorNotification.isShowing();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case VIEW_SUBJECTS_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    final String subjectFilter = data.getStringExtra("SUBJECT_FILTER");
                    final String baseUrl = getInitialUrl();
                    final Map<String, String> queryParams = new HashMap<>();
                    queryParams.put(QUERY_PARAM_SUBJECT, subjectFilter);
                    String subjectFilterUrl = buildQuery(baseUrl, logger, queryParams);
                    loadUrl(subjectFilterUrl);
                    ViewAnimationUtil.animateViewFading(binding.llSubjectContent, View.GONE);
                }
                break;
        }
    }
}
