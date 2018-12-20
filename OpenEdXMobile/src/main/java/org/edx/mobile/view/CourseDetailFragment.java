/*
 * CourseDetailFragment
 *
 * Main fragment that populates the course detail screen. The course card fragment is created first
 * and then the additional items are added if given.
 */

package org.edx.mobile.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.inject.Inject;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.widget.IconImageView;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragment;
import org.edx.mobile.core.IEdxEnvironment;
import org.edx.mobile.course.CourseAPI;
import org.edx.mobile.course.CourseDetail;
import org.edx.mobile.course.CourseService;
import org.edx.mobile.eliteu.event.BuyVipSuccessEvent;
import org.edx.mobile.eliteu.util.CourseUtil;
import org.edx.mobile.eliteu.util.RxBus;
import org.edx.mobile.eliteu.vip.ui.VipActivity;
import org.edx.mobile.eliteu.vip.ui.VipTipsDialog;
import org.edx.mobile.http.HttpStatus;
import org.edx.mobile.http.HttpStatusException;
import org.edx.mobile.http.callback.CallTrigger;
import org.edx.mobile.http.callback.Callback;
import org.edx.mobile.http.callback.ErrorHandlingCallback;
import org.edx.mobile.logger.Logger;
import org.edx.mobile.model.api.EnrolledCoursesResponse;
import org.edx.mobile.util.StandardCharsets;
import org.edx.mobile.util.WebViewUtil;
import org.edx.mobile.util.images.CourseCardUtils;
import org.edx.mobile.util.images.TopAnchorFillWidthTransformation;
import org.edx.mobile.view.common.TaskMessageCallback;
import org.edx.mobile.view.common.TaskProgressCallback;
import org.edx.mobile.view.custom.EdxWebView;
import org.edx.mobile.view.custom.URLInterceptorWebViewClient;
import org.edx.mobile.view.dialog.IDialogCallback;

import java.util.List;

import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;
import retrofit2.Call;
import roboguice.inject.InjectExtra;

import static org.edx.mobile.http.util.CallUtil.executeStrict;

public class CourseDetailFragment extends BaseFragment {

    public static final int LOG_IN_REQUEST_CODE = 42;

    @Nullable
    private Call<CourseDetail> getCourseDetailCall;

    private TextView mCourseTextName;
    private TextView mCourseTextDetails;
    private ImageView mHeaderImageView;
    private ImageView mHeaderPlayIcon;

    private LinearLayout mCourseDetailLayout;

    private TextView mShortDescription;

    private LinearLayout courseDetailFieldLayout;
    private FrameLayout courseAbout;
    private EdxWebView courseAboutWebView;

    private Button mEnrollButton;
    private boolean mEnrolled = false;

    boolean emailOptIn = true;

    static public final String COURSE_DETAIL = "course_detail";

    @InjectExtra(COURSE_DETAIL)
    CourseDetail courseDetail;


    protected final Logger logger = new Logger(getClass().getName());

    @Inject
    private CourseService courseService;

    @Inject
    private CourseAPI courseApi;

    @Inject
    IEdxEnvironment environment;

    private VipTipsDialog mVipTipsDialog;

    @Inject
    Router router;

    public static final String RETRY_EXCEPTION_MESSAGE = "Unsatisfiable Request (only-if-cached)";
    private TextView mVipInfoTv;
    private TextView mViewVipDetailTv;
    private RelativeLayout mVipLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            courseDetail = bundle.getParcelable(Router.EXTRA_COURSE_DETAIL);
        }
    }

    /**
     * Sets the view for the Course Card and play button if there is an intro video.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Course Card View
        View view;
        view = inflater.inflate(R.layout.fragment_course_dashboard, container, false);
        mCourseTextName = (TextView) view.findViewById(R.id.course_detail_name);
        mCourseTextDetails = (TextView) view.findViewById(R.id.course_detail_extras);
        mHeaderImageView = (ImageView) view.findViewById(R.id.header_image_view);
        mHeaderPlayIcon = (ImageView) view.findViewById(R.id.header_play_icon);
        mCourseDetailLayout = (LinearLayout) view.findViewById(R.id.dashboard_detail);

        mHeaderPlayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(courseDetail.media.course_video.uri);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        return view;
    }

    /**
     * Populates the course details.
     * Creates and populates the short description if given,
     * Creates and populates fields such as "effort", "duration" if any. This is handled on a case
     * by case basis rather than using a list view.
     * Sets the view for About this Course which is retrieved in a later api call.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Short Description
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View child = inflater.inflate(R.layout.fragment_course_detail, mCourseDetailLayout, false);
        mShortDescription = (TextView) child.findViewById(R.id.course_detail_short_description);
        if (courseDetail.short_description == null || courseDetail.short_description.isEmpty()) {
            ((ViewGroup) mShortDescription.getParent()).removeView(mShortDescription);
        }
        mCourseDetailLayout.addView(child);

        // Enrollment Button
        mEnrollButton = (Button) child.findViewById(R.id.button_enroll_now);
//        configureEnrollButton();

        // Course Detail Fields - Each field will be created manually.

        courseDetailFieldLayout = (LinearLayout) view.findViewById(R.id.course_detail_fields);
        if (courseDetail.effort != null && !courseDetail.effort.isEmpty()) {
            ViewHolder holder = createCourseDetailFieldViewHolder(inflater, mCourseDetailLayout);
            holder.rowIcon.setIcon(FontAwesomeIcons.fa_dashboard);
            holder.rowFieldName.setText(R.string.effort_field_name);
            holder.rowFieldText.setText(courseDetail.effort);
        }

        //  About this Course
        courseAbout = (FrameLayout) view.findViewById(R.id.course_detail_course_about);
        courseAboutWebView = (EdxWebView) courseAbout.findViewById(R.id.course_detail_course_about_webview);

        //vip Layout
        mVipLayout = view.findViewById(R.id.vip_layout);
        mVipInfoTv = mVipLayout.findViewById(R.id.vip_info_tv);
        mViewVipDetailTv = mVipLayout.findViewById(R.id.view_vip_detail_tv);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setCourseImage();
        setCourseVideoButton();
        setCourseCardText();
        mShortDescription.setText(courseDetail.short_description);
        populateAboutThisCourse();
        initBecomeVipResultCallBack();
    }

    private void setCourseCardText() {
        String formattedDate = CourseCardUtils.getFormattedDate(
                getActivity(),
                courseDetail.start,
                courseDetail.end,
                courseDetail.start_type,
                courseDetail.start_display);
        mCourseTextDetails.setText(CourseCardUtils.getDescription(courseDetail.org, courseDetail.number, formattedDate));
        mCourseTextName.setText(courseDetail.name);
    }

    private void setCourseImage() {
        final String headerImageUrl = courseDetail.media.course_image.getUri(environment.getConfig().getApiHostURL());
        Glide.with(CourseDetailFragment.this)
                .load(headerImageUrl)
                .placeholder(R.drawable.placeholder_course_card_image)
                .transform(new TopAnchorFillWidthTransformation(getActivity()))
                .into(mHeaderImageView);
    }

    /**
     * Shows and enables the play button if the video url was provided.
     */
    private void setCourseVideoButton() {
        if (courseDetail.media.course_video.uri == null || courseDetail.media.course_video.uri.isEmpty()) {
            mHeaderPlayIcon.setEnabled(false);
        } else {
            mHeaderPlayIcon.setVisibility(mHeaderPlayIcon.VISIBLE);
        }
    }

    /**
     * Makes a call the the course details api and sets the overview if given. If there is no
     * overview, remove the courseAbout view.
     */
    private void populateAboutThisCourse() {
        getCourseDetailCall = courseApi.getCourseDetail(courseDetail.course_id);
        final Activity activity = getActivity();
        final TaskProgressCallback pCallback = activity instanceof TaskProgressCallback ? (TaskProgressCallback) activity : null;
        final TaskMessageCallback mCallback = activity instanceof TaskMessageCallback ? (TaskMessageCallback) activity : null;
        getCourseDetailCall.enqueue(new ErrorHandlingCallback<CourseDetail>(getActivity(),
                pCallback, mCallback, CallTrigger.LOADING_CACHED) {
            @Override
            protected void onResponse(@NonNull final CourseDetail courseDetail) {
                CourseDetailFragment.this.courseDetail = courseDetail;
                configureEnrollButtonWithVip();
                initVipLayout(courseDetail);
                if (courseDetail.overview != null && !courseDetail.overview.isEmpty()) {
                    populateAboutThisCourse(courseDetail.overview);
                } else {
                    courseAbout.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Takes a string which can include html and then renders it into the courseAbout webview.
     *
     * @param overview A string that can contain html tags
     */
    private void populateAboutThisCourse(String overview) {
        courseAbout.setVisibility(View.VISIBLE);
        URLInterceptorWebViewClient client = new URLInterceptorWebViewClient(
                getActivity(), courseAboutWebView);
        client.setAllLinksAsExternal(true);

        StringBuilder buff = WebViewUtil.getIntialWebviewBuffer(getActivity(), logger);

        buff.append("<body>");
        buff.append("<div class=\"header\">");
        buff.append(overview);
        buff.append("</div>");
        buff.append("</body>");

        courseAboutWebView.clearCache(true);
        courseAboutWebView.loadDataWithBaseURL(environment.getConfig().getApiHostURL(), buff.toString(), "text/html", StandardCharsets.UTF_8.name(), null);
    }

    /**
     * Creates a ViewHolder for a course detail field such as "effort" or "duration" and then adds
     * it to the top of the list.
     */
    private ViewHolder createCourseDetailFieldViewHolder(LayoutInflater inflater, LinearLayout parent) {
        ViewHolder holder = new ViewHolder();
        holder.rowView = inflater.inflate(R.layout.course_detail_field, parent, false);

        holder.rowIcon = (IconImageView) holder.rowView.findViewById(R.id.course_detail_field_icon);
        holder.rowFieldName = (TextView) holder.rowView.findViewById(R.id.course_detail_field_name);
        holder.rowFieldText = (TextView) holder.rowView.findViewById(R.id.course_detail_field_text);

        courseDetailFieldLayout.addView(holder.rowView, 0);
        return holder;
    }

    private class ViewHolder {
        View rowView;
        IconImageView rowIcon;
        TextView rowFieldName;
        TextView rowFieldText;
    }


    /**
     * Sets the onClickListener and the text for the enrollment button.
     *
     * If the current course is found in the list of cached course enrollment list, the button will
     * be for viewing a course, otherwise, it will be used to enroll in a course. One clicked, user
     * is then taken to the dashboard for target course.
     */
    private void configureEnrollButton() {
        // This call should already be cached, if not, set button as if not enrolled.
        try {
            List<EnrolledCoursesResponse> enrolledCoursesResponse =
                    executeStrict(courseApi.getEnrolledCoursesFromCache());
            for (EnrolledCoursesResponse course : enrolledCoursesResponse) {
                if (course.getCourse().getId().equals(courseDetail.course_id)) {
                    mEnrolled = true;
                }
            }
        } catch (Exception ex) {
            logger.debug("Unable to get cached enrollments list");
        }

        if (mEnrolled) {
            mEnrollButton.setText(R.string.view_course_button_text);
        } else if (courseDetail.invitation_only != null && courseDetail.invitation_only) {
            mEnrollButton.setText(R.string.invitation_only_button_text);
            mEnrollButton.setEnabled(false);
        } else {
            mEnrollButton.setText(R.string.enroll_now_button_text);
        }

        mEnrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mEnrolled) {
                    enrollInCourse();
                } else {
                    openCourseDashboard();
                }
            }
        });
    }

    private void configureEnrollButtonWithVip() {
        mEnrollButton.setText(CourseUtil.getEnrollButtonString(courseDetail, getActivity(), environment.getLoginPrefs()));
        mEnrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == environment.getLoginPrefs().getUsername()) {
                    startActivityForResult(environment.getRouter().getRegisterIntent(), LOG_IN_REQUEST_CODE);
                    return;
                }
                int event = CourseUtil.getEnrollButtonOnClickEvent(courseDetail);
                switch (event) {
                    case CourseUtil.CLICK_OPEN_COURSE:
                        openCourseDashboard();
                        break;
                    case CourseUtil.CLICK_NORMAL_ENROLL:
                        normalEnroll();
                        break;
                    case CourseUtil.CLICK_VIP_ENROLL:
                        vipEnroll();
                        break;
                    case CourseUtil.CLICK_VIP_DIALOG:
                        showVipTipsDialog();
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOG_IN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            enrollInCourse();
            populateAboutThisCourse();
        }
    }

    /**
     * Enroll in a course, Then open the course dashboard of the enrolled course.
     */
    public void enrollInCourse() {
        if (null == environment.getLoginPrefs().getUsername()) {
            startActivityForResult(environment.getRouter().getRegisterIntent(), LOG_IN_REQUEST_CODE);
            return;
        }
        environment.getAnalyticsRegistry().trackEnrollClicked(courseDetail.course_id, emailOptIn);
        courseService.enrollInACourse(new CourseService.EnrollBody(courseDetail.course_id, emailOptIn))
                .enqueue(new CourseService.EnrollCallback(getActivity()) {
                    @Override
                    protected void onResponse(@NonNull final ResponseBody responseBody) {
                        super.onResponse(responseBody);
                        mEnrolled = true;
                        logger.debug("Enrollment successful: " + courseDetail.course_id);
                        mEnrollButton.setText(R.string.view_course_button_text);
                        Toast.makeText(getActivity(), R.string.you_are_now_enrolled, Toast.LENGTH_SHORT).show();

                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                courseApi.getEnrolledCourses().enqueue(new CourseAPI.GetCourseByIdCallback(
                                        getActivity(),
                                        courseDetail.course_id) {
                                    @Override
                                    protected void onResponse(@NonNull EnrolledCoursesResponse course) {
                                        environment.getRouter().showMainDashboard(getActivity());
                                        environment.getRouter().showCourseDashboardTabs(getActivity(), course, false);
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    protected void onFailure(@NonNull final Throwable error) {
                        Toast.makeText(getActivity(), R.string.enrollment_failure, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Open course dashboard for given course from the enrollments list cache.
     */
    private void openCourseDashboard() {
        try {
            List<EnrolledCoursesResponse> enrolledCoursesResponse =
                    executeStrict(courseApi.getEnrolledCoursesFromCache());
            for (EnrolledCoursesResponse course : enrolledCoursesResponse) {
                if (course.getCourse().getId().equals(courseDetail.course_id)) {
                    environment.getRouter().showMainDashboard(getActivity());
                    environment.getRouter().showCourseDashboardTabs(getActivity(), course, false);
                }
            }
        } catch (HttpStatusException exception) {
            int code = exception.getResponse().code();
            String message = exception.getResponse().message();
            if (code == HttpStatus.GATEWAY_TIMEOUT && TextUtils.equals(RETRY_EXCEPTION_MESSAGE, message)) {
                openCourseDashboardReTry();
            }
        } catch (Exception exception) {
            logger.debug(exception.toString());
            Toast.makeText(getContext(), R.string.cannot_show_dashboard, Toast.LENGTH_SHORT).show();
        }

    }

    private void showVipTipsDialog() {
        mVipTipsDialog = new VipTipsDialog(new IDialogCallback() {
            @Override
            public void onPositiveClicked() {
                router.showVip(getActivity(), VipActivity.VIP_SELECT_ID);
                hideVipTipsDialog();
            }

            @Override
            public void onNegativeClicked() {
                normalEnroll();
                hideVipTipsDialog();
            }
        });
        mVipTipsDialog.show(getFragmentManager(), "show");
    }

    private void hideVipTipsDialog() {
        if (mVipTipsDialog != null) {
            mVipTipsDialog.dismiss();
        }
    }

    /**
     * 单课购买流程
     */
    private void normalEnroll() {
        Toast.makeText(getActivity(), R.string.single_class_buy_not_supported, Toast.LENGTH_SHORT).show();
    }

    /**
     * vip购买流程
     */
    private void vipEnroll() {
        enrollInCourse();
    }

    private void initBecomeVipResultCallBack() {
        RxBus.getDefault().toObservable(BuyVipSuccessEvent.class)
                .subscribe(new Consumer<BuyVipSuccessEvent>() {
                    @Override
                    public void accept(BuyVipSuccessEvent buyVipSuccessEvent) throws Exception {
                        courseDetail.is_vip = true;
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        AuthPanelUtils.reConfigureAuthPanel(getActivity().findViewById(R.id.auth_panel), environment, this);
    }

    private void openCourseDashboardReTry() {
        courseApi.getEnrolledCourses().enqueue(new Callback<List<EnrolledCoursesResponse>>() {
            @Override
            protected void onResponse(@NonNull List<EnrolledCoursesResponse> enrolledCoursesResponse) {
                for (EnrolledCoursesResponse course : enrolledCoursesResponse) {
                    if (course.getCourse().getId().equals(courseDetail.course_id)) {
                        environment.getRouter().showMainDashboard(getActivity());
                        environment.getRouter().showCourseDashboardTabs(getActivity(), course, false);
                    }
                }
            }

            @Override
            protected void onFailure(@NonNull Throwable error) {
                super.onFailure(error);
                logger.debug(error.toString());
                Toast.makeText(getContext(), R.string.cannot_show_dashboard, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initVipLayout(final CourseDetail courseDetail){
        if (courseDetail.recommended_package != null) {
            mVipLayout.setVisibility(View.VISIBLE);
            mVipInfoTv.setText(CourseUtil.getCourseDetailVipInfo(courseDetail,getActivity()));
            mViewVipDetailTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null == environment.getLoginPrefs().getUsername()) {
                        startActivityForResult(environment.getRouter().getRegisterIntent(), LOG_IN_REQUEST_CODE);
                        return;
                    }
                    router.showVip(getActivity(),String.valueOf(courseDetail.recommended_package.id));
                }
            });
        } else {
            mVipLayout.setVisibility(View.GONE);
        }
    }
}
