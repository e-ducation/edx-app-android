package org.edx.mobile.view;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.inject.Inject;
import com.jakewharton.rxbinding3.view.RxView;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragment;
import org.edx.mobile.core.IEdxEnvironment;
import org.edx.mobile.event.MediaStatusChangeEvent;
import org.edx.mobile.logger.Logger;
import org.edx.mobile.module.analytics.Analytics;
import org.edx.mobile.module.prefs.PrefManager;
import org.edx.mobile.util.FileUtil;
import org.edx.mobile.view.dialog.IDialogCallback;
import org.edx.mobile.view.dialog.NetworkCheckDialogFragment;
import org.edx.mobile.view.dialog.WebViewActivity;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import io.reactivex.functions.Consumer;
import kotlin.Unit;


public class SettingsFragment extends BaseFragment {

    public static final String TAG = SettingsFragment.class.getCanonicalName();

    private final Logger logger = new Logger(SettingsFragment.class);

    @Inject
    protected IEdxEnvironment environment;

    @Inject
    ExtensionRegistry extensionRegistry;

    private Switch wifiSwitch;
    private Switch sdCardSwitch;
    private LinearLayout sdCardSettingsLayout;
    private TextView logoutTextView;

    private RelativeLayout user_agreement_item,privacy_policy,disclaimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        environment.getAnalyticsRegistry().trackScreenView(Analytics.Screens.SETTINGS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        final View layout = inflater.inflate(R.layout.fragment_settings, container, false);
        final View layout = inflater.inflate(R.layout.fragment_eliteu_settings, container, false);
        wifiSwitch = (Switch) layout.findViewById(R.id.wifi_setting);
        sdCardSwitch = (Switch) layout.findViewById(R.id.download_location_switch);
        sdCardSettingsLayout = (LinearLayout) layout.findViewById(R.id.sd_card_setting_layout);
        user_agreement_item = layout.findViewById(R.id.user_agreement_item);
        privacy_policy = layout.findViewById(R.id.privacy_policy);
        disclaimer = layout.findViewById(R.id.disclaimer);
        updateWifiSwitch();
        updateSDCardSwitch();
        logoutTextView = layout.findViewById(R.id.logout_btn);
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                environment.getRouter().performManualLogout(getActivity(),
                        environment.getAnalyticsRegistry(), environment.getNotificationDelegate());
            }
        });
        final LinearLayout settingsLayout = (LinearLayout) layout.findViewById(R.id.settings_layout);
        for (SettingsExtension extension : extensionRegistry.forType(SettingsExtension.class)) {
            extension.onCreateSettingsView(settingsLayout);
        }

        RxView.clicks(user_agreement_item).
                throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                      getActivity().startActivity(WebViewActivity.newIntent(getActivity(),getString(R.string.eula_file_link),getString(R.string.end_user_title)));
                    }
                });

        RxView.clicks(privacy_policy).
                throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        getActivity().startActivity(WebViewActivity.newIntent(getActivity(),getString(R.string.privacy_file_link),getString(R.string.privacy_policy)));
                    }
                });

        RxView.clicks(disclaimer).
                throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        getActivity().startActivity(WebViewActivity.newIntent(getActivity(),getString(R.string.terms_file_link),getString(R.string.terms_of_service_title)));
                    }
                });


        return layout;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void updateWifiSwitch() {
        final PrefManager wifiPrefManager = new PrefManager(
                getActivity().getBaseContext(), PrefManager.Pref.WIFI);

        wifiSwitch.setOnCheckedChangeListener(null);
        wifiSwitch.setChecked(wifiPrefManager.getBoolean(PrefManager.Key.DOWNLOAD_ONLY_ON_WIFI, true));
        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wifiPrefManager.put(PrefManager.Key.DOWNLOAD_ONLY_ON_WIFI, true);
                    wifiPrefManager.put(PrefManager.Key.DOWNLOAD_OFF_WIFI_SHOW_DIALOG_FLAG, true);
                } else {
                    showWifiDialog();
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(MediaStatusChangeEvent event) {
        sdCardSwitch.setEnabled(event.isSdCardAvailable());
    }

    private void updateSDCardSwitch() {
        final PrefManager prefManager =
                new PrefManager(getActivity().getBaseContext(), PrefManager.Pref.USER_PREF);
        if (!environment.getConfig().isDownloadToSDCardEnabled() || Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            sdCardSettingsLayout.setVisibility(View.GONE);
        } else {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().registerSticky(this);
            }
            sdCardSwitch.setOnCheckedChangeListener(null);
            sdCardSwitch.setChecked(prefManager.getBoolean(PrefManager.Key.DOWNLOAD_TO_SDCARD, false));
            sdCardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    prefManager.put(PrefManager.Key.DOWNLOAD_TO_SDCARD, isChecked);
                    // Send analytics
                    if (isChecked)
                        environment.getAnalyticsRegistry().trackDownloadToSdCardSwitchOn();
                    else
                        environment.getAnalyticsRegistry().trackDownloadToSdCardSwitchOff();
                }
            });
            sdCardSwitch.setEnabled(FileUtil.isRemovableStorageAvailable(getActivity()));
        }
    }

    protected void showWifiDialog() {
        final NetworkCheckDialogFragment newFragment = NetworkCheckDialogFragment.newInstance(getString(R.string.wifi_dialog_title_help),
                getString(R.string.wifi_dialog_message_help),
                new IDialogCallback() {
                    @Override
                    public void onPositiveClicked() {
                        try {
                            PrefManager wifiPrefManager = new PrefManager
                                    (getActivity().getBaseContext(), PrefManager.Pref.WIFI);
                            wifiPrefManager.put(PrefManager.Key.DOWNLOAD_ONLY_ON_WIFI, false);
                            updateWifiSwitch();
                        } catch (Exception ex) {
                            logger.error(ex);
                        }
                    }

                    @Override
                    public void onNegativeClicked() {
                        try {
                            PrefManager wifiPrefManager = new PrefManager(
                                    getActivity().getBaseContext(), PrefManager.Pref.WIFI);
                            wifiPrefManager.put(PrefManager.Key.DOWNLOAD_ONLY_ON_WIFI, true);
                            wifiPrefManager.put(PrefManager.Key.DOWNLOAD_OFF_WIFI_SHOW_DIALOG_FLAG, true);

                            updateWifiSwitch();
                        } catch (Exception ex) {
                            logger.error(ex);
                        }
                    }
                });

        newFragment.setCancelable(false);
        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }
}
