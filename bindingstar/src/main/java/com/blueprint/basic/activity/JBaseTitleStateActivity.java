package com.blueprint.basic.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.blueprint.Consistent;
import com.blueprint.R;
import com.blueprint.basic.JBasePresenter;
import com.blueprint.widget.JToolbar;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [标题+状态界面  有统一处理 basePresenter的subscribe和unsubscribe]
 * 在 onAttachtoWindow会获取数据
 * onContentChanged在setContentView内部调用 然后基类才获取各种控件，所以在onContentChanged中可以获取intent中的数据，就不需要在super.onCreate之前获取数据
 */
public abstract class JBaseTitleStateActivity<SD> extends JBaseActivity {
    public JToolbar mJToolbar;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    public RelativeLayout mMultiStateLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(!forceNoSwipeRefresh() && setEnableOuterSwipeRefresh()) {
            //支持下拉刷新
            setContentView(R.layout.jbasic_title_state_swipe_layout);
        }else {
            setContentView(R.layout.jbasic_title_state_layout);
        }
        if(requestNoTitleBar()) {
            mJToolbar.setVisibility(View.GONE);
        }else {
            initTitleBar();
            reConfigTitleBar(mJToolbar);
            if(setContentBelowTitleBar()) {
                contentLayoutBelowTitleBar();
            }
        }
        onCreateContent(getLayoutInflater(), mMultiStateLayout);
        try2findCustomSwipeRefreshLayout();
        //默认一定有下拉刷新，，最后开放方法  关闭下拉刷新
        if(forceNoSwipeRefresh()) {
            mSwipeRefreshLayout = null;
        }
        toSubscribeData(null);
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

    /**
     * 寻找自定义布局中的下拉刷新 id固定
     */
    private void try2findCustomSwipeRefreshLayout(){
        if(!forceNoSwipeRefresh() && mSwipeRefreshLayout == null) {
            //使用自己的布局的时候看看有没有swipelayout
            configCustomSwipeRefreshLayout(R.id.jbase_swipe);
        }
    }


    protected void configCustomSwipeRefreshLayout(@IdRes int srlId){
        mSwipeRefreshLayout = findViewById(srlId);
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
     * 支持多状态必然需要联网，默认支持下拉刷新
     *
     * @return
     */
    protected boolean setEnableOuterSwipeRefresh(){
        return true;
    }

    protected abstract JBasePresenter initPresenter();

    //onResume之后
    @Override
    public void onAttachedToWindow(){
        super.onAttachedToWindow();

    }

    @Override
    public void onContentChanged(){
        super.onContentChanged();
        //该方法是在 setContentView()内部触发的 并不是onCreate结束之后才执行
        //        @Override
        //        public void setContentView(View v) {
        //            ensureSubDecor();
        //            ViewGroup contentParent = (ViewGroup) mSubDecor.findViewById(android.R.id.content);
        //            contentParent.removeAllViews();
        //            contentParent.addView(v);
        //            mOriginalWindowCallback.onContentChanged();
        //        }
    }

    /**
     * 默认 有titlebar
     *
     * @return
     */
    protected boolean requestNoTitleBar(){
        return false;
    }

    private void initTitleBar(){
        //标题内容
    }

    protected int setTitleBarColor(){
        return Consistent.DEFAULTERROR;
    }

    /**
     * 复写 更新titlebar样式
     *
     * @param titleBar
     */
    protected void reConfigTitleBar(JToolbar titleBar){

    }

    protected void setClicks(){
        //左边的点击事件
    }

    protected void doOnTBrightClick(){

    }

    protected void doOnTBleftClick(){
        onBackPressed();
    }

    protected CharSequence setErrorRetryMsg(){
        return null;
    }

    protected CharSequence setEmptRetryMsg(){
        return null;
    }

    protected String setErrorMsg(){
        return "";
    }

    protected String setEmptMsg(){
        return "";
    }

    protected Object setTitle(){
        return null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    /**
     * 将布局添加到 container中
     *
     * @param inflater
     * @param container
     */
    protected abstract void onCreateContent(LayoutInflater inflater, RelativeLayout container);

    /**
     * 默认 onretry传的参数为null 第一次加载参数也为null
     *
     * @param from
     */
    protected void toSubscribeData(@Nullable Object from){
    }
}
