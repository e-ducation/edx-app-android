package org.edx.mobile.eliteu.wight;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

public class EqualizationTabLayout extends TabLayout {
    public EqualizationTabLayout(Context context) {
        super(context);
    }

    public EqualizationTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EqualizationTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void addTab(@NonNull Tab tab, int position, boolean setSelected) {
        super.addTab(tab, position, setSelected);
        setTab(tab);
    }

    private void setTab(Tab tab) {
        try {
            Field mViewF = Tab.class.getDeclaredField("mView");
            mViewF.setAccessible(true);
            LinearLayout mView = (LinearLayout) mViewF.get(tab);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;
            mView.setLayoutParams(layoutParams);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
