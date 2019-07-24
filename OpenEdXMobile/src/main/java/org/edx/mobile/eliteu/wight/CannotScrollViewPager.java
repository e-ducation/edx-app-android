package org.edx.mobile.eliteu.wight;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by elitemc on 2017/11/24.
 */

public class CannotScrollViewPager extends ViewPager {

    private boolean isScroll;

    public CannotScrollViewPager(Context context) {
        super(context);
    }

    public CannotScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);   // return true;不行
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (isScroll) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isScroll) {
            return super.onTouchEvent(ev);
        } else {
            return true;// 可行,消费,拦截事件
        }
    }

    public void setScroll(boolean scroll) {
        isScroll = scroll;
    }

    /**
     * 解决切换需要经过中间页
     */
    @Override
    public void setCurrentItem(int item) {
        //super.setCurrentItem(item);源码
        super.setCurrentItem(item, false);//false表示切换的时候,不经过两个页面的中间页
    }

}
