package me.tatarka.bindingcollectionadapter2.recv;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * @another 江祖赟
 * @date 2017/9/27 0027.
 */
public class JLinearLayoutManager extends LinearLayoutManager {

    public JLinearLayoutManager(Context context){
        super(context);
    }

    public JLinearLayoutManager(Context context, int orientation, boolean reverseLayout){
        super(context, orientation, reverseLayout);
    }

    public JLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state){
        try {
            super.onLayoutChildren(recycler, state);
        }catch(IndexOutOfBoundsException e) {
            Log.e("JLinearLayoutManager", Log.getStackTraceString(e));
        }
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state){
        try {
            return super.scrollVerticallyBy(dy, recycler, state);
        }catch(IndexOutOfBoundsException e) {
            Log.e("JLinearLayoutManager", Log.getStackTraceString(e));
            return 0;
        }
    }
}
