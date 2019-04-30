package org.edx.mobile.eliteu.util;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.edx.mobile.view.OfflineSupportBaseFragment;

public abstract class BaseLazyLoadFragment  extends OfflineSupportBaseFragment {


    protected boolean bIsViewCreated;
    protected boolean bIsDataLoaded;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResId(), container, false);
        initView(view);

        bIsViewCreated = true;

        if (getUserVisibleHint() && !bIsDataLoaded) {
            loadData();
            bIsDataLoaded = true;
        }
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        bIsViewCreated = false;
        bIsDataLoaded = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && bIsViewCreated && !bIsDataLoaded) {
            loadData();
            bIsDataLoaded = true;
        }
    }

    /**
     * @return 布局资源id
     */
    protected abstract int getLayoutResId();

    /**
     * 初始化View
     */
    protected abstract void initView(View view);

    /**
     * 加载数据
     */
    protected abstract void loadData();

}
