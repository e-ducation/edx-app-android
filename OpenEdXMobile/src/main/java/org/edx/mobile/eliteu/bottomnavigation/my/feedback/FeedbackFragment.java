package org.edx.mobile.eliteu.bottomnavigation.my.feedback;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.inject.Inject;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragment;
import org.edx.mobile.base.BaseFragmentActivity;
import org.edx.mobile.eliteu.aliyun_oss.AliyunOssApi;
import org.edx.mobile.eliteu.aliyun_oss.OssUploadUtil;
import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.eliteu.util.CannotCancelDialogFragment;
import org.edx.mobile.eliteu.util.UriTool;
import org.edx.mobile.eliteu.wight.FullyGridLayoutManager;
import org.edx.mobile.util.NetworkUtil;
import org.edx.mobile.util.SoftKeyboardUtil;
import org.edx.mobile.util.images.ImageCaptureHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import roboguice.inject.InjectExtra;

public class FeedbackFragment extends BaseFragment implements TextWatcher {

    private CompositeDisposable mCompositeDisposable;

    private EditText feedbackEt;
    private TextView textLengthTv;
    private RecyclerView recyclerView;
    private TextView imageSizeTv;
    private TextView submitBtn;
    private EditText contactWayEt;
    private View progress;

    private List<Uri> selectList = new ArrayList<>();
    @NonNull
    private final ImageCaptureHelper helper = new ImageCaptureHelper();
    RxPermissions rxPermissions;
    FeedbackImageAdapter feedbackImageAdapter;
    private static final int CHOOSE_PHOTO_REQUEST = 3;
    @InjectExtra(FeedbackActivity.EXTRA_USERNAME)
    private String username;
    @Inject
    EliteApi eliteApi;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        rxPermissions = new RxPermissions(getActivity());
        mCompositeDisposable = new CompositeDisposable();
        progress = view.findViewById(R.id.progress);

        submitBtn = view.findViewById(R.id.submit_btn);
        imageSizeTv = view.findViewById(R.id.image_size);
        feedbackEt = view.findViewById(R.id.feedback_edittext);
        textLengthTv = view.findViewById(R.id.text_length);
        contactWayEt = view.findViewById(R.id.contact_way);
        feedbackEt.addTextChangedListener(this);
        recyclerView = view.findViewById(R.id.recycler_view);

        initSubmitBtn();

        FullyGridLayoutManager manager = new FullyGridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        feedbackImageAdapter = new FeedbackImageAdapter(getActivity(), onAddPicClickListener);
        feedbackImageAdapter.setList(selectList);
        feedbackImageAdapter.setSelectMax(4);
        recyclerView.setAdapter(feedbackImageAdapter);
    }

    private void initSubmitBtn() {
        RxTextView.textChanges(feedbackEt).map(feedback -> feedback.length() > 0).subscribe(aBoolean -> {
            submitBtn.setEnabled(aBoolean);
            submitOnClick(feedbackEt.getText().toString());
        });
    }

    private void submitOnClick(String feedback) {
        RxView.clicks(submitBtn).
                throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(unit -> requestServerSubmit(feedback));
    }

    private void requestServerSubmit(String feedback) {
        if (!NetworkUtil.isConnected(getActivity())) {
            showAlertDialog(getString(R.string.reset_no_network_message));
            return;
        }

        showSubmitProgress();
        SoftKeyboardUtil.hide(getActivity());

        String contactWay = contactWayEt.getText().toString();

        if (selectList.size() > 0) {
            List<String> urls = new ArrayList<>();

            for (Uri uri : selectList) {
                urls.add(UriTool.getFilePathByUri(getActivity(), uri));
            }
            addDisposable(AliyunOssApi.getIstance().getAliyunOss()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aliyunStsBean -> {
                        final StringBuilder stringBuilder = new StringBuilder();

                        new OssUploadUtil(getActivity(), aliyunStsBean, username).setDatas(urls, new OssUploadUtil.UploadListener() {
                            @Override
                            public void onUploadComplete(Map<Integer, Object> success, List<String> failure) {

                                List<Integer> image_sequence = new ArrayList<>(success.keySet());
                                Collections.sort(image_sequence);
                                for (Integer integer : image_sequence) {
                                    if (stringBuilder.length() == 0) {
                                        stringBuilder.append(success.get(integer));
                                    } else {
                                        stringBuilder.append("," + success.get(integer));
                                    }
                                }
                                if (getActivity().isDestroyed()) {
                                    return;
                                }
                                submitResuest(stringBuilder.toString(), username, feedback, contactWay);
                            }
                        });

                    }, throwable -> getActivity().runOnUiThread(() -> {
                        hideSubmitProgress();
                        showAlertDialog(getString(R.string.error_unknown));
                    })));

        } else {
            submitResuest("", username, feedback, contactWay);
        }
    }

    private void submitResuest(String imageurl, String username, String feedback, String contactWay) {
        addDisposable(eliteApi.submitFeedback(imageurl, username, feedback, contactWay)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(httpResponseBean -> {
                    hideSubmitProgress();

                    if (httpResponseBean.isSuccess() && httpResponseBean.getCode() == 200) {

                        showAlertDialog(getString(R.string.submit_feedback_success), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getActivity().finish();
                            }
                        });
                    } else {
                        showAlertDialog(getString(R.string.submit_feedback_failed));
                    }
                }, throwable -> {
                    hideSubmitProgress();
                    showAlertDialog(getString(R.string.submit_feedback_failed));

                }));

    }

    public void addDisposable(Disposable disposable) {
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.add(disposable);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.clear();
        }
        helper.deleteTemporaryFile();

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString();
        if (text.length() <= 100) {
            textLengthTv.setText(text.length() + "/100");

        }
    }

    private FeedbackImageAdapter.onAddPicClickListener onAddPicClickListener = new FeedbackImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            rxPermissions
                    .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {
                            final Intent galleryIntent = new Intent()
                                    .setType("image/*")
                                    .setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(galleryIntent, CHOOSE_PHOTO_REQUEST);
                        }
                    });
        }

        @Override
        public void subImageSize() {
            imageSizeTv.setText(selectList == null ? "0/4" : selectList.size() + "/4");
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CHOOSE_PHOTO_REQUEST: {
                final Uri imageUri = data.getData();
                if (null != imageUri) {
                    selectList.add(imageUri);
                    feedbackImageAdapter.setList(selectList);
                    imageSizeTv.setText(selectList == null ? "0/4" : selectList.size() + "/4");
                    feedbackImageAdapter.notifyDataSetChanged();
                }
                break;
            }

        }
    }


    private void showSubmitProgress() {
        progress.setVisibility(View.VISIBLE);
        submitBtn.setText(getString(R.string.label_submit) + "...");
        submitBtn.setClickable(false);
    }

    private void hideSubmitProgress() {
        progress.setVisibility(View.GONE);
        submitBtn.setText(R.string.label_submit);
        submitBtn.setClickable(true);
    }

    private void showAlertDialog(@NonNull String message) {
        BaseFragmentActivity baseFragmentActivity = (BaseFragmentActivity) getActivity();
        baseFragmentActivity.showAlertDialog("", message);
    }

    private void showAlertDialog(@NonNull String message, @Nullable DialogInterface.OnClickListener onPositiveClick) {
        BaseFragmentActivity baseFragmentActivity = (BaseFragmentActivity) getActivity();
        if (baseFragmentActivity.isInForeground()) {
            //使用点击返回键不消失的对话框
            CannotCancelDialogFragment.newInstance("", message, getString(R.string.label_ok), onPositiveClick, null, null)
                    .show(getActivity().getSupportFragmentManager(), null);
        }
    }

}
