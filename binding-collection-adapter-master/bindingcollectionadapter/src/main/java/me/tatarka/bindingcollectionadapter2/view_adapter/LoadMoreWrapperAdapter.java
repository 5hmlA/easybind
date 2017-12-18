package me.tatarka.bindingcollectionadapter2.view_adapter;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import me.tatarka.bindingcollectionadapter2.BR;
import me.tatarka.bindingcollectionadapter2.collections.JObservableList;
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList;

import static me.tatarka.bindingcollectionadapter2.Utils.LOG;

/**
 * 分页列表 涉及到改变数据的比如回复删除 获取分页数据最好用索引 从哪个索引开始取多少条数据
 * 关于回复评论/回复回复，需要自己伪造新增的回复数据添加的被回复的评论中去 （涉及到分页不能重新刷洗数据）
 * <br></>
 * <li> 在 {@link LoadMoreWrapperAdapter} 中 {@link MergeObservableList} 主要监听 {@link JObservableList}的变化(列表展示的数据的增删改),同时将
 * 变化 依赖自己的监听器转发给 {@link LoadMoreWrapperAdapter} 使 adapter 在变化中 处理对应的增删改
 * </b></li>
 */
public class LoadMoreWrapperAdapter<T> extends BindingRecyclerViewAdapter<T> {

    private static final String TAG = LoadMoreWrapperAdapter.class.getSimpleName();
    private OnLoadmoreControl mLoadmoreControl;
    private boolean mInLoadingState;

    /**
     * 下拉刷新 必须要设置
     */
    public static abstract class OnLoadmoreControl extends BaseObservable {

        @Bindable public boolean loadmoreFinished = false;
        @Bindable private boolean loadmoreFailed = false;
        public String mLoadFailTips;
        public String mLoadFinishTips;
        public String mLoadingTips;
        private OnPropertyChangedCallback mCallback;

        /**
         * 上拉加载 成功
         */
        public void loadmoreSucceed(){
            setLoadmoreFailed(false, null);
        }

        /**
         * 上拉加载失败
         */
        public void loadMoreFail(){
            setLoadmoreFailed(true, null);
        }

        /**
         * 上拉加载失败
         */
        public void loadMoreFail(String tips){
            setLoadmoreFailed(true, tips);
        }

        /**
         * 加载成功之后 不要忘了 设置false
         * 上拉加载 开关 同时 清除 自定义提示信息
         *
         * @param fail
         *         设置 加载 成功/失败
         */
        private void setLoadmoreFailed(boolean fail, String tips){
            mLoadFailTips = tips;
            if(!loadmoreFinished && loadmoreFailed != fail) {
                //上拉加载成功和失败的切换
                loadmoreFailed = fail;
                notifyPropertyChanged(BR.loadmoreFailed);
            }
        }

        public boolean isLoadmoreFailed(){
            return loadmoreFailed;
        }

        /**
         * 重新 允许下拉刷新
         */
        public void forceDown2Refresh(){
            if(loadmoreFinished) {
                mLoadFinishTips = null;
                loadmoreFailed = loadmoreFinished = false;
                notifyPropertyChanged(BR.loadmoreFinished);
            }
        }

        /**
         * 关闭下拉刷新
         */
        public void loadmoreFinished(){
            if(!loadmoreFinished) {
                mLoadFinishTips = null;
                loadmoreFinished = true;
                notifyPropertyChanged(BR.loadmoreFinished);
            }
        }

        /**
         * 关闭下拉刷新
         */
        public void loadmoreFinished(String finishTips){
            loadmoreFinished();
            mLoadFinishTips = finishTips;
        }

        public void setLoadmoreFinished(boolean finished, String finishTips){
            mLoadFinishTips = finishTips;
            if(loadmoreFinished != finished) {
                mLoadFinishTips = null;
                this.loadmoreFinished = finished;
                notifyPropertyChanged(BR.loadmoreFinished);
            }
        }

        public boolean isLoadmoreFinished(){
            return loadmoreFinished;
        }

        /**
         * 发起请求 加载更多数据//注意被多次调用的可能
         */
        protected abstract void onUp2Loadmore(RecyclerView recyclerView);

        public void onLoadmoreRetry(){
            if(!loadmoreFailed) {
                onUp2Loadmore(null);
            }
        }

        @Override
        public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback){
            mCallback = callback;
            super.addOnPropertyChangedCallback(callback);
        }

        public void removeCallBack(){
            removeOnPropertyChangedCallback(mCallback);
        }
    }

    public int mLastCheckDataSize;


    public LoadMoreWrapperAdapter(OnLoadmoreControl listener){
        mLoadmoreControl = listener;
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState){
                super.onScrollStateChanged(recyclerView, newState);
                checkUp2loadMore(newState);
            }

            //            @Override
            //            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
            //                super.onScrolled(recyclerView, dx, dy);
            //                if(mLoadmoreitem == NEED_UP2LOAD_MORE) {
            //                    //向上无法滚动
            //                    if(dy>0 && !mRecyclerView.canScrollVertically(1) && mLoadmoreitem == NEED_UP2LOAD_MORE && !mInLoadingMore) {
            //                        mInLoadingMore = true;
            //                        if(mLoadmoreControl != null) {
            //                            mLoadmoreControl.onUp2Loadmore();
            //                        }
            //                    }
            //                }
            //            }
        });
    }


    /**
     * <p>只在停止滚动的状态检测</p>
     * 检查 是否loadingholder可见，可见则回掉监听的onup2LoadingMore 去加载下一页数据
     */
    private void checkUp2loadMore(int newState){
        if(getItemCount() == 1) {
            //清空到 只剩下一个loading item则重启 上拉加载
            mLoadmoreControl.forceDown2Refresh();
            //当前状态为停止滑动状态SCROLL_STATE_IDLE时   getItemCount()-1去掉底部 loading
        }else if(!mLoadmoreControl.loadmoreFinished && getItemCount()-1>0 &&
                newState == RecyclerView.SCROLL_STATE_IDLE) {
            int lastPosition = 0;
            RecyclerView.LayoutManager layoutManager = this.recyclerView.getLayoutManager();
            if(layoutManager instanceof GridLayoutManager) {
                //通过LayoutManager找到当前显示的最后的item的position
                lastPosition = ( (GridLayoutManager)layoutManager ).findLastVisibleItemPosition();
            }else if(layoutManager instanceof LinearLayoutManager) {
                lastPosition = ( (LinearLayoutManager)layoutManager ).findLastVisibleItemPosition();
            }else if(layoutManager instanceof StaggeredGridLayoutManager) {
                //因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
                //得到这个数组后再取到数组中position值最大的那个就是最后显示的position值了
                int[] lastPositions = new int[( (StaggeredGridLayoutManager)layoutManager ).getSpanCount()];
                ( (StaggeredGridLayoutManager)layoutManager ).findLastVisibleItemPositions(lastPositions);
                lastPosition = findMax(lastPositions);
            }
            //时判断界面显示的最后item的position是否等于itemCount总数-1也就是最后一个item的position
            //如果相等则说明已经滑动到最后了
            if(lastPosition>=getItemCount()-1) {
                Log.d(TAG, "loading 上拉提示 item 可见");
                if(mLoadmoreControl != null && !mInLoadingState) {
                    mInLoadingState = true;
                    mLoadmoreControl.onUp2Loadmore(this.recyclerView);
                }
            }

            //                    if(mLoadingBinder != null && mLoadingBinder.itemView != null) {
            //                        //或者 loading可见自动加载 下一页
            //                        Rect visiRect = new Rect();
            //                        mLoadingBinder.itemView.getGlobalVisibleRect(visiRect);
            //                        System.out.println(visiRect.toString());
            //                        mLoadingBinder.itemView.getLocalVisibleRect(visiRect);
            //                        System.out.println(visiRect.toString());
            //                        mLoadingBinder.itemView.getWindowVisibleDisplayFrame(visiRect);
            //                        System.out.println(visiRect.toString());
            //                    }
        }
    }


    //找到数组中的最大值
    private int findMax(int[] lastPositions){
        int max = lastPositions[0];
        for(int value : lastPositions) {
            if(value>max) {
                max = value;
            }
        }
        return max;
    }


    public LoadMoreWrapperAdapter setOnMoreloadListener(OnLoadmoreControl listener){
        mLoadmoreControl = listener;
        return this;
    }

    @Override
    public void onChanged(JObservableList ts, int position, int count, Object payload){
        super.onChanged(ts, position, count, payload);
        mInLoadingState = false;
        //数据数量 变化了才需要判断  mLastCheckDataSize > 0不是第一次change
        if(!mLoadmoreControl.loadmoreFinished && mLastCheckDataSize>0 && getItemCount() != mLastCheckDataSize) {
            LOG("load_more 数据发生变化同时数据数量发生变化 检测是否需要触发上拉加载", mLastCheckDataSize = getItemCount());
            checkUp2loadMore(RecyclerView.SCROLL_STATE_IDLE);
        }
    }

    @Override
    public void onItemRangeInserted(JObservableList<T> ts, int positionStart, int itemCount){
        super.onItemRangeInserted(ts, positionStart, itemCount);
        mInLoadingState = false;
        mLoadmoreControl.loadmoreSucceed();//上拉加载成功//下拉刷新成功
        LOG(TAG, itemCount, " 条数据变化 (观察者) onItemRangeInserted --> finished?", mLoadmoreControl.loadmoreFinished);
    }

    @Override
    public void onItemRangeRemoved(JObservableList ts, int positionStart, int itemCount){
        super.onItemRangeRemoved(ts, positionStart, itemCount);
        LOG(TAG, items.size(), " 清除数据 onItemRangeMoved ", itemCount);
        mInLoadingState = false;
        checkUp2loadMore(RecyclerView.SCROLL_STATE_IDLE);//删除数据后检查 是否要自动拉取数据
    }
}
