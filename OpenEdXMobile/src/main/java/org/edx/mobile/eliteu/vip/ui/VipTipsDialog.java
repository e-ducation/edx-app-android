package org.edx.mobile.eliteu.vip.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.edx.mobile.R;
import org.edx.mobile.view.dialog.IDialogCallback;

@SuppressLint("ValidFragment")
public class VipTipsDialog extends DialogFragment implements View.OnClickListener {

    @SuppressLint("ValidFragment")
    public VipTipsDialog(IDialogCallback callback) {
        this.callback = callback;
    }

    private IDialogCallback callback;

    private TextView mTipsText;
    private TextView mNotYetTv;
    private TextView mBecomeVip;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
        View v = inflater.inflate(R.layout.fragment_vip_tips, container, false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        mTipsText = v.findViewById(R.id.tips_text);
        mNotYetTv = v.findViewById(R.id.not_yet_btn);
        mBecomeVip = v.findViewById(R.id.become_vip);
        mTipsText.setText(Html.fromHtml(getString(R.string.vip_tips_text)));
        mNotYetTv.setOnClickListener(this);
        mBecomeVip.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.not_yet_btn:
                callback.onNegativeClicked();
                break;
            case R.id.become_vip:
                callback.onPositiveClicked();
                break;
        }
    }
}
