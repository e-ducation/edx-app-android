package org.edx.mobile.eliteu.wight;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class SpaceOrientationItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public static final int HORIZONTAL = 1;

    public static final int VERTICAL = 2;

    private int orientation;

    public SpaceOrientationItemDecoration(int space, int orientation) {
        this.space = space;
        this.orientation = orientation;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if (orientation == HORIZONTAL) {

            outRect.right = space;

            if (parent.getChildLayoutPosition(view) == 0) {

                outRect.left = 0;

            } else {

                outRect.left = space;

            }


        } else if (orientation == VERTICAL) {

            outRect.bottom = space;
            outRect.top = 0;

        }

    }
}
