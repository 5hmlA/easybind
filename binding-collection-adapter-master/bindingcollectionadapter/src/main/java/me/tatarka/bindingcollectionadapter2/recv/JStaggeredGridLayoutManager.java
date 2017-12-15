package me.tatarka.bindingcollectionadapter2.recv;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;

/**
 * @another 江祖赟
 * @date 2017/12/14.
 */
public class JStaggeredGridLayoutManager extends StaggeredGridLayoutManager {
    public JStaggeredGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public JStaggeredGridLayoutManager(int spanCount, int orientation){
        super(spanCount, orientation);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state){
        try {
            super.onLayoutChildren(recycler, state);
        }catch(IndexOutOfBoundsException e) {
            Log.e("JLinearLayoutManager", Log.getStackTraceString(e));
        }
    }
}
