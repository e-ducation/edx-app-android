package org.edx.mobile.eliteu.harvard;

import android.text.TextUtils;
import android.view.View;

import org.edx.mobile.R;
import org.edx.mobile.databinding.PanelFindCourseBinding;
import org.edx.mobile.eliteu.util.AccountPrefs;
import org.edx.mobile.user.Account;
import org.edx.mobile.util.Config;
import org.edx.mobile.view.MyCoursesListFragment;
import org.edx.mobile.view.Router;


public class HarvardStatusUtil {

    public static void initButton(final MyCoursesListFragment fragment, final PanelFindCourseBinding footer, final Config config) {
        AccountPrefs accountPrefs = new AccountPrefs(fragment.getContext());
        Account account = accountPrefs.getAccount();
        int hmm_remaining_days = account.getHmm_remaining_days();
        if (hmm_remaining_days > 0) {
            //哈佛有效期
            footer.courseBtn.setVisibility(View.GONE);
            footer.harvardBtn.setVisibility(View.VISIBLE);
            footer.harvardBtn.setText(String.format(fragment.getResources().getString(R.string.harvard_btn_string), account.getHmm_expiry_date()));
            footer.harvardBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(account.getHmm_entry_url())) {
                        String url = config.getApiHostURL() + account.getHmm_entry_url();
                        Router router = new Router(config);
                        router.showAuthenticatedWebviewActivity(fragment.getActivity(), url, fragment.getString(R.string.harvard));
                    }
                }
            });
        } else {
            //不是哈佛有效期
            footer.courseBtn.setVisibility(View.VISIBLE);
            footer.harvardBtn.setVisibility(View.GONE);
        }
    }

    public static boolean isHarvardUrl(String url) {
        if (url.contains("myhbp.org.cn")) {
            return true;
        } else {
            return false;
        }
    }
}
