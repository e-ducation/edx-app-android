package org.edx.mobile.eliteu.util;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.edx.mobile.eliteu.wight.WebViewFabBehavior;

/**
 * FAB点击动画
 */
public class FabAnimatorUtil {

    private static AccelerateDecelerateInterpolator LINEAR_INTERRPLATOR = new AccelerateDecelerateInterpolator();

    public static void showFab(View view, WebViewFabBehavior.AnimateListener... listener) {
        if (listener.length != 0) {
            view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(600)
                    .setInterpolator(LINEAR_INTERRPLATOR)
                    .setListener(listener[0])
                    .start();
        } else {
            view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(600)
                    .setInterpolator(LINEAR_INTERRPLATOR)
                    .start();
        }

    }

    public static void hideFab(View view, WebViewFabBehavior.AnimateListener listener) {
        view.animate()
                .scaleX(0f)
                .scaleY(0f)
                .alpha(0f)
                .setDuration(600)
                .setInterpolator(LINEAR_INTERRPLATOR)
                .setListener(listener)
                .start();
    }

}
