package org.edx.mobile.eliteu.harvard;

import android.text.TextUtils;
import android.view.View;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragmentActivity;
import org.edx.mobile.databinding.PanelFindCourseBinding;
import org.edx.mobile.eliteu.util.AccountPrefs;
import org.edx.mobile.eliteu.util.CannotCancelDialogFragment;
import org.edx.mobile.module.prefs.PrefManager;
import org.edx.mobile.user.Account;
import org.edx.mobile.util.Config;
import org.edx.mobile.view.MyCoursesListFragment;
import org.edx.mobile.view.Router;


public class HarvardStatusUtil {

    public static final String HARVAR_TIME_7_30_SP_KEY = "harvar_time_7_30_show_dialog_flag";
    public static final String HARVAR_TIME_LESS_THAN_7_SP_KEY = "harvar_time_less_than_7_show_dialog_flag";


    public static void initButton(final MyCoursesListFragment fragment, final PanelFindCourseBinding footer, final Config config) {
        AccountPrefs accountPrefs = new AccountPrefs(fragment.getContext());
        Account account = accountPrefs.getAccount();
        int hmm_remaining_days = account.getHmm_remaining_days();
        if (hmm_remaining_days > 0) {
            //哈佛有效期
            footer.courseBtn.setVisibility(View.GONE);
            footer.harvardBtn.setVisibility(View.VISIBLE);
            footer.harvardBtn.setText(String.format(fragment.getResources().getString(R.string.harvard_btn_string), account.getHmm_expiry_date()));
            footer.harvardBtn.setOnClickListener(view -> {
                if (!TextUtils.isEmpty(account.getHmm_entry_url())) {
                    String url = config.getApiHostURL() + account.getHmm_entry_url();
                    Router router = new Router(config);
                    router.showAuthenticatedWebviewActivity(fragment.getActivity(), url, fragment.getString(R.string.harvard));
                }
            });
            final PrefManager prefManager = new PrefManager(
                    fragment.getContext(), PrefManager.Pref.LOGIN);
            if (hmm_remaining_days > 7 && hmm_remaining_days <= 30 && prefManager.getBoolean(HARVAR_TIME_7_30_SP_KEY, false) == false) {
                showDialog1(prefManager, (BaseFragmentActivity) fragment.getActivity());
            } else if (hmm_remaining_days <= 7 && prefManager.getBoolean(HARVAR_TIME_LESS_THAN_7_SP_KEY, false) == false) {
                showDialog2(prefManager, (BaseFragmentActivity) fragment.getActivity());
            }
        } else {
            //不是哈佛有效期
            footer.courseBtn.setVisibility(View.VISIBLE);
            footer.harvardBtn.setVisibility(View.GONE);
        }
    }

    private static void showDialog1(PrefManager prefManager, final BaseFragmentActivity activity) {
        CannotCancelDialogFragment.newInstance(activity.getString(R.string.bind_mobie_dialog_title), activity.getString(R.string.harvard_time_7_30), activity.getString(R.string.harvard_dialog_ok), (dialogInterface, i) -> prefManager.put(HARVAR_TIME_7_30_SP_KEY, true), null, null).show(activity.getSupportFragmentManager(), null);
    }

    private static void showDialog2(PrefManager prefManager, final BaseFragmentActivity activity) {
        CannotCancelDialogFragment.newInstance(activity.getString(R.string.bind_mobie_dialog_title), activity.getString(R.string.harvard_time_less_than_7), activity.getString(R.string.harvard_dialog_ok), (dialogInterface, i) -> prefManager.put(HARVAR_TIME_LESS_THAN_7_SP_KEY, true), null, null).show(activity.getSupportFragmentManager(), null);
    }


    public static boolean isHarvardUrl(String url) {
        if (url.contains("myhbp.org.cn")) {
            return true;
        } else {
            return false;
        }
    }
}
