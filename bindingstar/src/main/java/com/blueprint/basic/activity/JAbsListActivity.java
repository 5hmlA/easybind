package com.blueprint.basic.activity;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.blueprint.R;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.bindingcollectionadapter2.recv.JDividerItemDecoration;
import me.tatarka.bindingcollectionadapter2.recv.JLinearLayoutManager;
import me.tatarka.bindingcollectionadapter2.view_adapter.LoadMoreWrapperAdapter;

import static com.blueprint.Consistent.PageState.STATE_FIRST_LOAD;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [推荐]
 * <android.support.v7.widget.RecyclerView
 * android:id="@+id/common_recv"
 * android:layout_width="match_parent"
 * android:layout_height="match_parent"
 * android:clipToPadding="false"
 * android:paddingLeft="14dp"
 * android:paddingRight="14dp"
 * android:paddingTop="8dp">
 */
public abstract class JAbsListActivity<IT, SD> extends JBaseTitleStateActivity<List<SD>> {
    public RecyclerView mCommonRecv;
    public List<IT> mListData = new ArrayList<IT>();
    public LoadMoreWrapperAdapter mRecvAdapter;
    protected static final int NO_CUSTOM_LAYOUT = -10;
    protected int mCurrentPageState = STATE_FIRST_LOAD;

    //activity中默认显示titlebar
    @Override
    protected boolean requestNoTitleBar(){
        return false;
    }

    @Override
    protected void onCreateContent(LayoutInflater inflater, RelativeLayout container){
        View rootView = container;
        if(setCustomLayout() == NO_CUSTOM_LAYOUT) {
            if(forceNoSwipeRefresh() || mSwipeRefreshLayout != null) {
                //外层父布局 已经支持下拉刷新
                rootView = inflater.inflate(R.layout.jbasic_abslist_layout, container);
                mSwipeRefreshLayout = rootView.findViewById(R.id.jbase_swipe);
            }else {
                //不允许同时开启两个swiperefreshlayout
                rootView = inflater.inflate(R.layout.jbasic_abslist_swipe_layout, container);
            }
        }else {
            rootView = inflater.inflate(setCustomLayout(), container);
        }
        mCommonRecv = rootView.findViewById(R.id.jbase_recv);
        initRecView();
    }

    /**
     * 支持多状态必然需要联网，默认支持下拉刷新
     * <h1>默认开启 最外层下拉刷新</h1>
     * <h1>复写{@link JBaseTitleStateActivity#forceNoSwipeRefresh()} 可以关闭所有下拉刷新 </h1>
     *
     * @return <p>true 开放 包裹多状态布局的下拉刷新</p>
     * <p>false 开放 包裹recycleview的下拉刷新</p>
     */
    public boolean setEnableOuterSwipeRefresh(){
        return true;
    }

    @Override
    protected void toSubscribeData(Object from){
        mCurrentPageState = STATE_FIRST_LOAD;
        super.toSubscribeData(from);
    }

    /**
     * 必须包含 ID为 jbase_recv的 recycleview
     * 使用自定义recycleview布局 需要注意 是否需要关闭外层下拉刷新
     * <p>不需要的时候 重新 从布局寻找新的recycleview</p>
     *
     * @return
     */
    public int setCustomLayout(){
        return NO_CUSTOM_LAYOUT;
    }

    protected void initRecView(){
    }

    public RecyclerView.ItemDecoration setItemDecoration(){
        return new JDividerItemDecoration(0);
    }


    protected RecyclerView.LayoutManager setLayoutManager(){
        return new JLinearLayoutManager(getApplicationContext());
    }

    public void dispatchUpdates(){

    }
}
