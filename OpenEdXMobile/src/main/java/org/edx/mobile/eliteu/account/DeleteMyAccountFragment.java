package org.edx.mobile.eliteu.account;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.inject.Inject;
import com.jakewharton.rxbinding3.view.RxView;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragment;
import org.edx.mobile.core.IEdxEnvironment;
import org.edx.mobile.eliteu.api.EliteApi;

import java.util.concurrent.TimeUnit;

/**
 * @author: laibinzhi
 * @date: 2020-03-12 17:57
 * @github: https://github.com/laibinzhi
 * @blog: https://www.laibinzhi.top/
 */
public class DeleteMyAccountFragment extends BaseFragment {

    TextView deleteAccountTv;
    TextView deleteAccountBtn;
    @Inject
    EliteApi eliteApi;
    @Inject
    protected IEdxEnvironment environment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_delete_my_account, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        deleteAccountTv = view.findViewById(R.id.delete_account_content);
        deleteAccountTv.setText(Html.fromHtml(getString(R.string.delete_account_content)));
        deleteAccountBtn = view.findViewById(R.id.delete_account_btn);

        RxView.clicks(deleteAccountBtn).throttleFirst(1, TimeUnit.SECONDS).subscribe(unit -> {
            DeleteAccountConfirmDialogFragment deleteAccountConfirmDialogFragment = new DeleteAccountConfirmDialogFragment(eliteApi,environment);
            deleteAccountConfirmDialogFragment.show(getActivity().getSupportFragmentManager(), "show");
        });
    }

}
