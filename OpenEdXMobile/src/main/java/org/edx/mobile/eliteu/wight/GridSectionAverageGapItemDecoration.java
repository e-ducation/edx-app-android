package org.edx.mobile.eliteu.wight;

import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public class GridSectionAverageGapItemDecoration extends RecyclerView.ItemDecoration {


    private float gapHorizontalDp;
    private float gapVerticalDp;
    private float sectionEdgeHPaddingDp;
    private float sectionEdgeVPaddingDp;
    private int gapHSizePx = -1;
    private int gapVSizePx = -1;
    private int sectionEdgeHPaddingPx;
    private int eachItemHPaddingPx; //每个条目应该在水平方向上加的padding 总大小，即=paddingLeft+paddingRight
    private int sectionEdgeVPaddingPx;
    private Rect preRect = new Rect();
    private boolean isPreItemHeader = false;
    private int sectionStartItemPos = 0;
    private int sectionEndItemPos = 0;
    private int sectionItemCount = 0;

    /**
     * @param gapHorizontalDp       水平间距
     * @param gapVerticalDp         垂直间距
     * @param sectionEdgeHPaddingDp 左右两端的padding大小
     * @param sectionEdgeVPaddingDp 上下两端的padding大小
     */
    public GridSectionAverageGapItemDecoration(float gapHorizontalDp, float gapVerticalDp, float sectionEdgeHPaddingDp, float sectionEdgeVPaddingDp) {
        this.gapHorizontalDp = gapHorizontalDp;
        this.gapVerticalDp = gapVerticalDp;
        this.sectionEdgeHPaddingDp = sectionEdgeHPaddingDp;
        this.sectionEdgeVPaddingDp = sectionEdgeVPaddingDp;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();

            int spanCount = layoutManager.getSpanCount();
            int count = parent.getAdapter().getItemCount();
            int position = parent.getChildAdapterPosition(view);

            if (gapHSizePx < 0 || gapVSizePx < 0) {
                transformGapDefinition(parent, spanCount);
            }
            outRect.top = gapVSizePx;
            outRect.bottom = 0;

            //下面的visualPos为单个Section内的视觉Pos
            int visualPos = position + 1 - sectionStartItemPos;
            if (visualPos % spanCount == 1) {
                //第一列
                outRect.left = sectionEdgeHPaddingPx;
                outRect.right = eachItemHPaddingPx - sectionEdgeHPaddingPx;
            } else if (visualPos % spanCount == 0) {
                //最后一列
                outRect.left = eachItemHPaddingPx - sectionEdgeHPaddingPx;
                outRect.right = sectionEdgeHPaddingPx;
            } else {
                outRect.left = gapHSizePx - preRect.right;
                outRect.right = eachItemHPaddingPx - outRect.left;
            }

            if (visualPos - spanCount <= 0) {
                //每个section的第一行
                outRect.top = sectionEdgeVPaddingPx;
            }
            //存在即是第一行，又是最后一行的情况，故不用elseif
            if (isLastRow(visualPos, spanCount, sectionItemCount)) {
                //每个section的最后一行
                outRect.bottom = sectionEdgeVPaddingPx;
            }
            preRect = new Rect(outRect);
            isPreItemHeader = false;

        }else {
            super.getItemOffsets(outRect, view, parent, state);
        }
    }

    private void transformGapDefinition(RecyclerView parent, int spanCount) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            parent.getDisplay().getMetrics(displayMetrics);
        }
        gapHSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, gapHorizontalDp, displayMetrics);
        gapVSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, gapVerticalDp, displayMetrics);
        sectionEdgeHPaddingPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sectionEdgeHPaddingDp, displayMetrics);
        sectionEdgeVPaddingPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sectionEdgeVPaddingDp, displayMetrics);
        eachItemHPaddingPx = (sectionEdgeHPaddingPx * 2 + gapHSizePx * (spanCount - 1)) / spanCount;
    }

    private boolean isLastRow(int visualPos, int spanCount, int sectionItemCount) {
        int lastRowCount = sectionItemCount % spanCount;
        lastRowCount = lastRowCount == 0 ? spanCount : lastRowCount;
        return visualPos > sectionItemCount - lastRowCount;
    }
}
