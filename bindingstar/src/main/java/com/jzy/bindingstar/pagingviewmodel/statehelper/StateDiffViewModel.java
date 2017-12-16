package com.jzy.bindingstar.pagingviewmodel.statehelper;

import android.databinding.ObservableBoolean;
import android.support.v4.widget.SwipeRefreshLayout;

import me.tatarka.bindingcollectionadapter2.itembindings.ExtrasBindViewModel;

/**
 * @another 江祖赟
 * @date 2017/12/16.
 */
public abstract class StateDiffViewModel<SD> extends ExtrasBindViewModel {

    public CharSequence pageStateMsg;
    public ObservableBoolean showSucceed = new ObservableBoolean(false);
    public ObservableBoolean enableSwipeRefresh = new ObservableBoolean(true);

    protected SwipeRefreshLayout mSwipeRefreshLayout;
    /**
     * 显示 SwipeRefreshLayout 刷新状态
     */
    public ObservableBoolean mDown2Refreshing = new ObservableBoolean(false);

    public abstract void subscribeData(Object orignParam);

    public void onRefresh(SwipeRefreshLayout swipeRefreshLayout){ /* compiled code */
        mSwipeRefreshLayout = swipeRefreshLayout;
    }

    public void hideLoading(){
        //下拉刷新成功，网络错误重试(下拉不可用--转可用)
        switchSwipeRefresh(true);
        // 隐藏多状态 布局
        showSucceed.set(true);
    }

    /**
     * 开启下拉刷新  隐藏多状态布局
     *
     * @param data
     */
    public void showPageStateSuccess(SD data){
        hideLoading();
    }

    public void showPageStateLoading(){
        showSucceed.set(false);
        pageStateMsg = null;
    }

    public void showPageStateError(@PageDiffState int pageDiffState){
        if(pageDiffState == PageDiffState.PAGE_STATE_EMPTY) {
            //允许下拉刷新
            switchSwipeRefresh(true);
            //显示 空数据状态

        }else {
            //网络错误数据错误 关闭下拉刷新 因为网络错误有按钮可刷新
            switchSwipeRefresh(false);
            //显示错误界面

        }
        showSucceed.set(false);
    }

    public void showPageStateError(@PageDiffState int pageDiffState, String errTips){
        pageStateMsg = ( errTips );
        showPageStateError(pageDiffState);
    }


    protected void switchSwipeRefresh(boolean enable){
        if(mSwipeRefreshLayout != null) {
            //只有下拉 才出现下拉刷新  或者手动调用setRefresh
            mSwipeRefreshLayout.setRefreshing(false);//内部有判断和当前状态是否相同
            mSwipeRefreshLayout.setEnabled(enable);//内部有判断和当前状态是否相同
        }
    }

}
