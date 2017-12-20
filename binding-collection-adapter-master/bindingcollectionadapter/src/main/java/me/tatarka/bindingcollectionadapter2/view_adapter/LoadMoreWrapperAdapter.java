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
    /**
     * 代表底部的loading 提示框<br>
     * <li>值为1 时 当item的数据不包括底部loading为空 就不显示底部loading</li>
     * <li>值为0 时 当item的数据不包括底部loading为空 任然显示底部loading</li>
     */
    public static int FOOT_LOAD_HOLDER = 1;

    /**
     * 是否正处于 加载数据状态<br>
     * <li>在{@link #checkUp2loadMore()}中 达到上拉加载的条件 同时 非处于加载状态 会设置为 true</li>
     * <li>在{@link JObservableList.JOnListChangedCallback}中 监听 数据的增删改 设置为false表示 已经拿到数据结束加载状态</li>
     */
    private boolean mInLoadingState;

    /**
     * 下拉刷新 必须要设置
     */
    public static abstract class OnLoadmoreControl extends BaseObservable {

        /**
         * 上拉加载 是否结束
         * <li>外部 手动调整 下拉刷新 需要重置为 false 调用{@link #forceDown2Refresh()}</li>
         * <li>外部 手动调整 接口数据表示没有下一页 需要设置为 true 调用{@link #loadmoreFinished()} ,{@link #loadmoreFinished(String)}</li>
         */
        @Bindable public boolean loadmoreFinished = false;
        /**
         * 是否 是 处于 上拉加载 失败的状态
         * <br>
         * <b>false 表示上拉加载处于 失败的状态</b><br>
         * <b>true 表示上拉加载处于 成功/正在加载 的状态</b>
         * <br>
         * <li>外部 手动调整 上拉加载失败为 true 调用 {@link #loadMoreFail()}, {@link #loadMoreFail(String)} </li>
         * <li>上拉加载成功 false  不需要外部手动调用，在adapter中监听到插入数据就会回掉，还有重启上拉刷新{@link #forceDown2Refresh()}也会回掉  调用 {@link #loadmoreSucceed()} </li>
         */
        @Bindable private boolean loadmoreFailed = false;
        public String mLoadFailTips;
        public String mLoadFinishTips;
        public String mLoadingTips;
        private OnPropertyChangedCallback mCallback;

        /**
         * 上拉加载 成功/上拉加载的loading状态<br>
         * 在adapter中监听到插入数据{@link #onItemRangeInserted(JObservableList, int, int)} 就表示成功拿到数据 就是加载成功 会调用该方法
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
                LOG("OnLoadmoreControl", "跟新底部loading状态: loadmoreFailed", fail);
                //上拉加载成功和失败的切换
                loadmoreFailed = fail;
                notifyPropertyChanged(BR.loadmoreFailed);
            }
        }

        public boolean isLoadmoreFailed(){
            return loadmoreFailed;
        }

        /**
         * 上拉加载结束之后 下拉刷新 重新重置底部loading状态为loading状态
         */
        public void forceDown2Refresh(){
            if(loadmoreFinished) {
                mLoadFinishTips = null;
                loadmoreFinished = false;
                loadmoreSucceed();//在上拉加载失败后 重新下拉刷新 时 设置底部loading处于加载数据状态
                notifyPropertyChanged(BR.loadmoreFinished);
            }
        }

        /**
         * 上拉加载 结束 之后上拉不再检测是否加载数据
         */
        public void loadmoreFinished(){
            if(!loadmoreFinished) {
                mLoadFinishTips = null;
                loadmoreFinished = true;
                notifyPropertyChanged(BR.loadmoreFinished);
            }
        }

        /**
         * 上拉加载 结束 之后上拉不再检测是否加载数据
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

        /**
         * 底部loading的重试 按钮<br>
         * loadmoreFailed=true && loadmoreFinished=false<br>
         * <li>jfoot_loadmore_holder.xml 布局中的点击触发</li>
         */
        public final void retryLoadMore(){
            loadmoreSucceed();
            onLoadmoreRetry();
        }

        /**
         * 底部loading的重试 按钮<br>
         * loadmoreFailed=true && loadmoreFinished=false<br>
         * <li>jfoot_loadmore_holder.xml 布局中的点击触发</li>
         */
        public void onLoadmoreRetry(){
            loadmoreSucceed();
            onUp2Loadmore(null);
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
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //滑动停止 才检查
                    checkUp2loadMore();
                }
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

    @Override
    public int getItemCount(){
        //如果只有一条数据 就是底部的loading 不现实
        return items == null ? 0 : items.size()>FOOT_LOAD_HOLDER ? items.size() : 0;
    }

    /**
     * <p>只在停止滚动的状态检测</p>
     * 检查 是否loadingholder可见，可见则回掉监听的onup2LoadingMore 去加载下一页数据
     */
    private synchronized void checkUp2loadMore(){
        LOG("checkUp2loadMore()-> mInLoadingState 当前列表的是否处于加载状态(加载状态不检测上拉加载)", mInLoadingState);
        if(!mLoadmoreControl.loadmoreFinished && getItemCount() == 1) {
            //清空到 只剩下一个 底部loading 则重启 上拉加载
            mLoadmoreControl.forceDown2Refresh();
            //当前状态为停止滑动状态SCROLL_STATE_IDLE时   getItemCount()-1去掉底部 loading
        }else if(!mInLoadingState && !mLoadmoreControl.loadmoreFinished && getItemCount()-1>0) {
            //1 上拉加载 没结束的时候 上拉加载
            //2 mInLoadingState 为false 即没有处于 加载数据ing 状态的时候 允许检测
            //3 处于错误状态的时候 没有监听到数据的增删改 mInLoadingState会 一直为false （回到 第2点）
            //4 下拉刷新 更新数据之后 会出现增删改的情况 但是 数据都一样就不会 触发增删改 也就是没有检查上拉请求过数据checkUp2loadMore()没走 mInLoadingState为false
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
                if(mLoadmoreControl != null) {
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
            checkUp2loadMore();
        }
    }

    @Override
    public void onItemRangeInserted(JObservableList<T> ts, int positionStart, int itemCount){
        super.onItemRangeInserted(ts, positionStart, itemCount);
        mInLoadingState = false;
        //上拉加载成功 一定是插入数据 不会删改数据
        mLoadmoreControl.loadmoreSucceed();//上拉加载成功//下拉刷新成功
        checkUp2loadMore();//删除数据后检查 是否要自动拉取数据
        LOG(TAG, itemCount, " 条数据变化 (观察者) onItemRangeInserted --> finished?", mLoadmoreControl.loadmoreFinished);
    }

    @Override
    public void onItemRangeRemoved(JObservableList ts, int positionStart, int itemCount){
        super.onItemRangeRemoved(ts, positionStart, itemCount);
        LOG(TAG, items.size(), " 清除数据 onItemRangeMoved ", itemCount);
        mInLoadingState = false;
        mLoadmoreControl.loadmoreSucceed();//删除之后 重新检测上拉加载
        checkUp2loadMore();//删除数据后检查 是否要自动拉取数据
    }

    @Override
    public void onMoved(JObservableList ts, int fromPosition, int toPosition){
        super.onMoved(ts, fromPosition, toPosition);
        LOG(TAG, items.size(), " 数据移动 onMoved ", fromPosition, toPosition);
        mInLoadingState = false;
    }
}
