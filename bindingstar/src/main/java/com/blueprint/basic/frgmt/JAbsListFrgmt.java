package com.blueprint.basic.frgmt;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.blueprint.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [推荐]
 */
public abstract class JAbsListFrgmt<IT, SD> extends JBaseTitleStateFrgmt<List<SD>>{
    public RecyclerView mCommonRecv;
    public List<IT> mListData = new ArrayList();
    protected static final int NO_CUSTOM_LAYOUT = -10;

    @Override
    protected boolean requestNoTitleBar(){
        return true;
    }

    @Override
    protected void onCreateContent(LayoutInflater inflater, RelativeLayout container){
        View rootView = container;
        if(setCustomLayout() == NO_CUSTOM_LAYOUT) {
            if(forceNoSwipeRefresh() || mSwipeRefreshLayout != null) {
                //外层父布局 已经支持下拉刷新/或者都不支持下拉刷新
                rootView = inflater.inflate(R.layout.jbasic_abslist_layout, container);
            }else {
                //不允许同时开启两个swiperefreshlayout
                rootView = inflater.inflate(R.layout.jbasic_abslist_swipe_layout, container);
                mSwipeRefreshLayout = rootView.findViewById(R.id.jbase_swipe);
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
     * <h1>复写{@link JBaseTitleStateFrgmt#forceNoSwipeRefresh()} 可以关闭所有下拉刷新 </h1>
     *
     * @return <p>true 开放 包裹多状态布局的下拉刷新</p>
     * <p>false 开放 包裹recycleview的下拉刷新</p>
     */
    public boolean setEnableOuterSwipeRefresh(){
        return true;
    }

    @Override
    public boolean forceNoSwipeRefresh(){
        return super.forceNoSwipeRefresh();
    }


    /**
     * 必须包含 ID为 jbase_recv的 recycleview
     * <p>不需要的时候 重新 从布局寻找新的recycleview</p>
     *
     * @return
     */
    protected int setCustomLayout(){
        return NO_CUSTOM_LAYOUT;
    }

    protected void initRecView(){
    }

    public void reConfigRecView(){
    }

}
