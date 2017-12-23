package com.blueprint.basic.frgmt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.blueprint.Consistent;
import com.blueprint.R;
import com.blueprint.basic.JBasePresenter;
import com.blueprint.widget.JTitleBar;

import jonas.jlayout.MultiStateLayout;

import static com.blueprint.Consistent.ErrorCode.ERROR_EMPTY;
import static com.blueprint.Consistent.PageState.STATE_DATA_EMPTY;
import static com.blueprint.Consistent.PageState.STATE_DATA_ERROR;
import static com.blueprint.Consistent.PageState.STATE_DATA_SUCCESS;
import static com.blueprint.Consistent.PageState.STATE_FIRST_LOAD;
import static com.blueprint.helper.LogHelper.Log_e;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [标题+状态界面  有统一处理 basePresenter的subscribe和unsubscribe]
 * onCreate中获取 getArguments数据
 */
public abstract class JBaseTitleStateFrgmt<SD> extends JBaseFragment{
    public MultiStateLayout mMultiStateLayout;
    public JTitleBar mTitleBar;
    public JBasePresenter mBasePresenter;
    /**
     * 不为空表示 支持下拉刷新,支持多状态必然需要联网，默认支持下拉刷新
     */
    public SwipeRefreshLayout mSwipeRefreshLayout;

    protected int mCurrentPageState = STATE_FIRST_LOAD;

    protected abstract JBasePresenter initPresenter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        mBasePresenter = initPresenter();
        View rootView = null;
        if(!forceNoSwipeRefresh() && setEnableOuterSwipeRefresh()) {
            rootView = inflater.inflate(R.layout.jbasic_title_state_swipe_layout, null);
            mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.jbase_swipe);
            mTitleBar = (JTitleBar)rootView.findViewById(R.id.jbase_titlebar);
            mMultiStateLayout = (MultiStateLayout)rootView.findViewById(R.id.jbase_state_container);
        }else {
            rootView = inflater.inflate(R.layout.jbasic_title_state_layout, null);
            mTitleBar = (JTitleBar)rootView.findViewById(R.id.jbase_titlebar);
            mMultiStateLayout = (MultiStateLayout)rootView.findViewById(R.id.jbase_state_container);
        }
        if(requestNoTitleBar()) {
            mTitleBar.setVisibility(View.GONE);
        }else {
            initTitlebar();
            reConfigTitleBar(mTitleBar);
            if(setContentBelowTitleBar()) {
                contentLayoutBelowTitleBar();
            }
        }
        onCreateContent(inflater, mMultiStateLayout);
        //默认一定有下拉刷新，，最后开放方法  关闭下拉刷新
        return rootView;
    }

    public boolean setContentBelowTitleBar(){
        return true;
    }

    protected void contentLayoutBelowTitleBar(){
        View host = mMultiStateLayout;
        if(mSwipeRefreshLayout != null) {
            host = mSwipeRefreshLayout;//此下拉刷新一定是最外层的
        }
        RelativeLayout.LayoutParams stateLayoutParams = (RelativeLayout.LayoutParams)host.getLayoutParams();
        stateLayoutParams.addRule(RelativeLayout.BELOW, R.id.jbase_titlebar);
    }

    protected void stateLayoutBelowTitleBar(){
        RelativeLayout.LayoutParams stateLayoutParams = (RelativeLayout.LayoutParams)mSwipeRefreshLayout
                .getLayoutParams();
        stateLayoutParams.addRule(RelativeLayout.BELOW, R.id.jbase_titlebar);
    }

    /**
     * 默认开启下拉刷新 ，强制关闭下拉刷新，不要复写 onCreateView()
     *
     * @return
     */
    public boolean forceNoSwipeRefresh(){
        return false;
    }

    /**
     * 默认 有titlebar
     *
     * @return
     */
    protected boolean requestNoTitleBar(){
        return false;
    }

    protected void initTitlebar(){
        //标题内容
        int titleColor = setTitleBarColor();
        if(titleColor != Consistent.DEFAULTERROR) {
            mTitleBar.getTitleTextView().setTextColor(titleColor);
        }
        //        //空页面提示信息
        //        setTextView(mContentLayout.getEmptyLayout(), R.id.j_multity_empt_msg, setEmptMsg());
        //        setTextView(mContentLayout.getEmptyLayout(), R.id.j_multity_retry, setEmptRetryMsg());
        //        //错误页面提示信息 只有出现错误页面才加载错误页面 应该注册文字
        //        setTextView(mContentLayout.getErrorLayout(), R.id.j_multity_error_msg, setErrorMsg());
        //        setTextView(mContentLayout.getErrorLayout(), R.id.j_multity_retry, setErrorRetryMsg());
    }

    protected int setTitleBarColor(){
        return Consistent.DEFAULTERROR;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        setClicks();
    }

    /**
     * 支持多状态必然需要联网，默认支持下拉刷新
     *
     * @return
     */
    protected boolean setEnableOuterSwipeRefresh(){
        return true;
    }

    /**
     * 复写 更新titlebar样式
     *
     * @param titleBar
     */
    protected void reConfigTitleBar(JTitleBar titleBar){

    }

    private void setClicks(){
    }

    protected void doOnTBrightClick(){

    }

    protected void doOnTBleftClick(){
        if(getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void firstUserVisibile(){
        toSubscribeData(null);
    }

    /**
     * 默认 onretry传的参数为null 第一次加载参数也为null
     *
     * @param from
     */
    protected void toSubscribeData(@Nullable Object from){
        mCurrentPageState = STATE_FIRST_LOAD;
        if(mBasePresenter != null) {
            mBasePresenter.subscribe(from);
        }else {
            Log_e("mBasePresenter没有赋值---");
        }
    }

    /**
     * 将布局添加到 container中
     *
     * @param inflater
     * @param container
     */
    protected abstract void onCreateContent(LayoutInflater inflater, RelativeLayout container);

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mBasePresenter != null) {
            mBasePresenter.unsubscribe();
        }
    }

    public void hideLoading(){
        mCurrentPageState = STATE_DATA_SUCCESS;
        switchSwipeRefresh(true);
        mMultiStateLayout.showStateSucceed();
    }

    public void showError(int eCode){
        if(eCode == ERROR_EMPTY) {
            mCurrentPageState = STATE_DATA_EMPTY;
            //允许下拉刷新
            switchSwipeRefresh(true);
            mMultiStateLayout.showStateEmpty();
        }else {
            mCurrentPageState = STATE_DATA_ERROR;
            //网络错误数据错误 关闭下拉刷新
            switchSwipeRefresh(false);
            mMultiStateLayout.showStateError();
        }
    }

    /**
     * 开启或关闭 下拉刷新，同时需要的话隐藏正在刷新状态
     *
     * @param enable
     */
    protected void switchSwipeRefresh(boolean enable){
        if(mSwipeRefreshLayout != null) {
            //只有下拉 才出现下拉刷新  或者手动调用setRefresh
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(enable);
        }
    }
}
