package jzy.easybindpagelist.statehelper;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import jonas.jlayout.MultiStateLayout;
import jonas.jlayout.OnStateClickListener;
import jzy.easybindpagelist.ScrollChildSwipeRefreshLayout;
import me.tatarka.bindingcollectionadapter2.itembindings.ExtrasBindViewModel;

import static jonas.jlayout.MultiStateLayout.LayoutState.STATE_UNMODIFY;
import static me.tatarka.bindingcollectionadapter2.Utils.LOG;

/**
 * @another 江祖赟
 * @date 2017/12/16.
 * 带有 泛型 不能在 databinding里面 作为变量 引用
 */
public abstract class StateDiffViewModel<SD> extends ExtrasBindViewModel implements OnStateClickListener, View.OnLayoutChangeListener {

    public CharSequence pageStateMsg;
    public ObservableInt pageState = new ObservableInt(MultiStateLayout.LayoutState.STATE_LOADING);
    public ObservableInt pageLoadingRes = new ObservableInt(STATE_UNMODIFY);
    public ObservableInt pageEmptyRes = new ObservableInt(STATE_UNMODIFY);
    public ObservableInt pageErrorRes = new ObservableInt(STATE_UNMODIFY);
    public ObservableInt pageLoadingColorInt = new ObservableInt(STATE_UNMODIFY);
    protected Object mOrignParam;
    protected ScrollChildSwipeRefreshLayout mSwipeRefreshLayout;
    /**
     * 显示 SwipeRefreshLayout 刷新状态
     */
    public ObservableBoolean down2Refreshing = new ObservableBoolean(false);
    public ObservableBoolean enableSwipeRefresh = new ObservableBoolean(true);

    {
        //构造代码块    执行顺序 父类--子类
        customMultiStateLayoutRes();
    }

    /**
     * 自定义 多状态布局的 不同状态的 布局id
     * <li>{@link #pageLoadingRes}</li>
     * <li>{@link #pageEmptyRes}</li>
     * <li>{@link #pageErrorRes}</li>
     * <li>{@link #pageLoadingColorInt}</li>
     */
    public void customMultiStateLayoutRes(){

    }

    /**
     * 由 view 发起请求数据<br>
     * <b>主要是保存 原始请求参数 下拉上拉加载数据的时候需要用到</b>
     *
     * @param orignParam
     */
    public void subscribeData(Object orignParam){
        mOrignParam = orignParam;
        onSubscribeData(mOrignParam);
    }

    /**
     * 实际请求数据 实现逻辑<br>
     * {@link #subscribeData(Object)}保留 请求参数之后 会回掉
     * <b>下拉刷新，上拉加载 都是回掉该方法 请求数据的</b>
     *
     * @param orignParam
     */
    public abstract void onSubscribeData(Object orignParam);

    public void onRefresh(SwipeRefreshLayout swipeRefreshLayout){
        LOG("=========== onRefresh ===========");
        down2Refreshing.set(true);
        mSwipeRefreshLayout = (ScrollChildSwipeRefreshLayout)swipeRefreshLayout;
        down2RefreshData();
    }

    public void down2RefreshData(){
        onSubscribeData(mOrignParam);
    }

    public void hideLoading(){
        //下拉刷新成功，网络错误重试(下拉不可用--转可用)
        switchSwipeRefresh(true);
        // 隐藏多状态 布局
        pageState.set(MultiStateLayout.LayoutState.STATE_EXCEPT);
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
        pageState.set(MultiStateLayout.LayoutState.STATE_LOADING);
        pageStateMsg = null;
    }

    public void showPageStateError(@PageDiffState int pageDiffState){
        if(pageDiffState == PageDiffState.PAGE_STATE_EMPTY) {
            LOG("=========== showPageStateError ===========", "数据为空");
            //允许下拉刷新
            switchSwipeRefresh(true);
            //显示 空数据状态
            pageState.set(MultiStateLayout.LayoutState.STATE_EMPTY);
        }else {
            LOG("=========== showPageStateError ===========", "数据异常/网络错误");
            //网络错误数据错误 关闭下拉刷新 因为网络错误有按钮可刷新
            switchSwipeRefresh(false);
            //显示错误界面
            pageState.set(MultiStateLayout.LayoutState.STATE_ERROR);
        }
    }

    public void showPageStateError(@PageDiffState int pageDiffState, String errTips){
        //底部的 loading 提示(loading,empty,error)
        pageStateMsg = errTips;
        //        pageStateMsg = Utils.hightLightStrParser(new SpannableString(errTips), errTips, Color.RED);
        showPageStateError(pageDiffState);
    }

    /**
     * 下拉显示出 下拉圈 回掉onRefresh
     *
     * @param enable
     */
    protected void switchSwipeRefresh(boolean enable){
        down2Refreshing.set(false);
        enableSwipeRefresh.set(enable);
        //        if(mSwipeRefreshLayout != null) {
        //不使用 obverseData 是因为 手动下拉之后 down2Refreshing任然为false,隐藏再次设置为false不会触发notify
        //可以使用 boolean数据 但是在onrefresh中需要重新设置为true 还是用data binding的方式吧
        //            //只有下拉 才出现下拉刷新  或者手动调用setRefresh
        //            mSwipeRefreshLayout.setRefreshing(false);//内部有判断和当前状态是否相同
        //            mSwipeRefreshLayout.setEnabled(enable);//内部有判断和当前状态是否相同
        //        }
    }

    /**
     * 空或者数据异常 的重试
     *
     * @param layoutState
     */
    @Override
    public void onRetry(int layoutState){
        LOG("=========== Retry from stateLayout ===========");
        showPageStateLoading();
        onSubscribeData(mOrignParam);
    }

    @Override
    public void onLoadingCancel(){
    }

    /**
     * 获取布局中的控件
     *
     * @param v
     */
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom){
    }

    public void enableSwipeRefreshLayout(boolean enable){
        enableSwipeRefresh.set(enable);
    }
}
