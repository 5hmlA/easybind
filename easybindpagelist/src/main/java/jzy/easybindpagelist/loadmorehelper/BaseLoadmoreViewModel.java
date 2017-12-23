package jzy.easybindpagelist.loadmorehelper;

import android.databinding.BaseObservable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import jonas.jlayout.MultiStateLayout;
import jzy.easybindpagelist.ScrollChildSwipeRefreshLayout;
import jzy.easybindpagelist.statehelper.PageDiffState;
import jzy.easybindpagelist.statehelper.StateDiffViewModel;
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList;
import me.tatarka.bindingcollectionadapter2.collections.IRecvDataDiff;
import me.tatarka.bindingcollectionadapter2.collections.JObservableList;
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList;
import me.tatarka.bindingcollectionadapter2.itembindings.ExtrasBindViewModel;
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;
import me.tatarka.bindingcollectionadapter2.recv.LayoutManagers;
import me.tatarka.bindingcollectionadapter2.view_adapter.LoadMoreWrapperAdapter;

import static jzy.easybindpagelist.loadmorehelper.LoadmoreFootViewModel.wrapperLoadMoreBinding;
import static me.tatarka.bindingcollectionadapter2.Utils.LOG;

/**
 * @anthor <a href="https://github.com/ZuYun">江祖赟</a>
 * @see
 */
public abstract class BaseLoadmoreViewModel<ID> extends StateDiffViewModel<List<ID>> {

    /**
     * 上拉加载 控制器{ 上拉加载成功/失败/结束/重启}
     */
    public final LoadMoreWrapperAdapter.OnLoadmoreControl mLoadmoreControl = new LoadMoreWrapperAdapter.OnLoadmoreControl() {
        @Override
        protected void onUp2Loadmore(RecyclerView recyclerView){
            up2LoadMoreData(recyclerView);
        }

        @Override
        public void onLoadmoreRetry(){
            retryUp2LoadMoreData(null);
        }
    };

    /**
     * 底部 loadmore 布局的viewModel
     */
    protected final LoadmoreFootViewModel mLoadmoreFootViewModel = new LoadmoreFootViewModel(mLoadmoreControl);

    /**
     * 数据容器必须是JObservableList类<br>
     * <b>《接口返回的集合数据必须是{@link ExtrasBindViewModel}的子类 》</b><br>
     * <b>《接口返回的集合数据被添加到{@link #mDataLists}JObservableList通过changecallback通知adapter增删改 》</b><br>
     * <b>《对RecycleView列表的增删改操作只需要对{@link #mDataLists}执行增删改即可 》</b>
     */
    public JObservableList<ID> mDataLists = new JObservableList<>();

    /**
     * 列表数据容器{@link #godLists} 在处理上拉加载的ViewModel 中<br>
     * <b>主要包括两部分:1, 界面的多种类数据{@link #mDataLists}. 2, 底部的 上拉加载提示loading</b>
     * <li><b>{@link #mDataLists} 用来收集存储 展示的所有有效数据(不同类型布局)</b></li>
     * <li><b>{@link #mLoadmoreFootViewModel} 就是底部的上拉加载提示 提示的各种状态(loading,error,finish)主要由
     * {@link LoadMoreWrapperAdapter.OnLoadmoreControl}控制,该控制器主要控制上拉加载的状态</b></li>
     */
    public final MergeObservableList godLists = new MergeObservableList().insertList(mDataLists).insertItem(mLoadmoreFootViewModel);

    /**
     * 注册 不同类型布局及其布局变量ID(data binding传数据给布局的方式通过BR.id方式) 和 对应的class数据类型
     */
    public final OnItemBindClass<ID> multipleItems = wrapperLoadMoreBinding(new OnItemBindClass<>());
    //======  分页 ====
    public static final long FIRST_PAGE = 1;
    /**
     * 静态全局的搜索关键字，简化搜索开发，偷个懒~
     */
    public static String CURRENT_SEARCH_KEY = "";
    /**
     * 存储上一次搜索的关键字，主要用于判断多次执行的搜索动作的关键字是不是同一个
     */
    private String mLastSearchKey;
    /**
     * 当前页码，调接口传参需要
     */
    public long mCurrentPage = FIRST_PAGE;

    /**
     * 用于下拉刷新全部数据的时候 比较数据差异
     */
    DiffObservableList mDiffObservableList = new DiffObservableList(mDataLists);

    protected RecyclerView mRecyclerView;
    protected MultiStateLayout mMultiStateLayout;

    /**
     * 不同的layoutmanager复写该方法即可
     *
     * @return
     */
    public LayoutManagers.LayoutManagerFactory layoutManager(){
        return LayoutManagers.linear();
    }

    /**
     * 注册 不同的 item 类型<br>
     * 缺点是每个item只能绑定一种数据变量 (类--布局--变量)<br>
     * <b>让各自的item数据 去绑定 多余的变量 那么该item的数据 类(类--布局--变量) 必须继承自{@link ExtrasBindViewModel}</b>
     *
     * @param multipleItems
     */
    protected abstract void registItemTypes(OnItemBindClass<ID> multipleItems);

    {
        registItemTypes(multipleItems);
    }

    /**
     * <a href="https://developer.android.com/topic/libraries/architecture/viewmodel.html"><b> Caution: A ViewModel must never reference a view, Lifecycle, or any class that may hold a reference to the activity context.</b></a>
     *
     * @param v
     *         主要用来 获取布局中的控件对象<br>
     */
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom){
        if(v instanceof RecyclerView) {
            mRecyclerView = (RecyclerView)v;
            if(mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setScrollUpChild(mRecyclerView);
            }
        }else if(v instanceof SwipeRefreshLayout) {
            mSwipeRefreshLayout = (ScrollChildSwipeRefreshLayout)v;
            if(mRecyclerView != null) {
                mSwipeRefreshLayout.setScrollUpChild(mRecyclerView);
            }
        }else if(v instanceof MultiStateLayout) {
            mMultiStateLayout = (MultiStateLayout)v;
        }
    }

    // ========================== 上拉加载 逻辑 ==============================

    public boolean isFirstPage(){
        return mCurrentPage == FIRST_PAGE;
    }

    @Override
    public void subscribeData(Object orignParam){
        super.subscribeData(orignParam);
    }

    /**
     * 保留请求参数 (重试等时候需要用到)
     *
     * @param orignParam
     */
    public void putOrignParam(Object orignParam){
        mOrignParam = orignParam;
    }

    protected void checkOrignParam(){
        if(mOrignParam == null) {
            throw new RuntimeException(" oops! you need to putOrignParam(orignParam) in onSubscribeData(orignParam) ...you have to");
        }
    }

    public void search(EditText editText, String key){
        CURRENT_SEARCH_KEY = key;
        if(!key.equals(mLastSearchKey)) {
            mCurrentPage = FIRST_PAGE;
            toSearchFromService(key);
        }else {
            theSameSearchKey(key);
        }
        mLastSearchKey = key;
    }

    protected void theSameSearchKey(String key){ /* 搜索同一个关键字 */ }

    /**
     * 搜索 关键字 复写方法<br>
     * <li>{@link #CURRENT_SEARCH_KEY} 会保存 当前的搜索关键字</li>
     * <li>默认 回掉 {@link #onSubscribeData(Object)}</li>
     *
     * @param key
     */
    protected void toSearchFromService(String key){
        LOG("=========== toSearchFromService ===========");
        /* 发请求 去搜索关键字吧 */
        onSubscribeData(mOrignParam);
    }

    protected void up2LoadMoreData(RecyclerView recyclerView){
        ++mCurrentPage;//建议用索引
        LOG("=========== up2LoadMoreData ===========", mCurrentPage);
        retryUp2LoadMoreData(recyclerView);
    }

    protected void retryUp2LoadMoreData(RecyclerView recyclerView){
        LOG("=========== retryUp2LoadMoreData ===========", mCurrentPage);
        if(mCurrentPage>FIRST_PAGE) {
            //上拉加载 失败 重试 一定不是在第一页的时候
            if(TextUtils.isEmpty(CURRENT_SEARCH_KEY)) {
                //关键字为空 非搜索
                onSubscribeData(mOrignParam);
            }else {
                toSearchFromService(CURRENT_SEARCH_KEY);
            }
        }
    }

    @Override
    public void onRefresh(SwipeRefreshLayout swipeRefreshLayout){
        mLoadmoreControl.forceDown2Refresh();//需要重启 底部loading
        super.onRefresh(swipeRefreshLayout);
    }

    /**
     * 布局中swipeRefreshView的onRefresh调用
     */
    @Override
    public void down2RefreshData(){
        mCurrentPage = FIRST_PAGE;
        LOG("=========== down2RefreshData ===========", mCurrentPage);
        //显示 第一页的 loading 状态 下拉刷新 不显示 不然会同时显示 下拉刷新的圆圈
        //        showPageStateLoading();
        if(TextUtils.isEmpty(CURRENT_SEARCH_KEY)) {
            //关键字为空 非搜索
            onSubscribeData(mOrignParam);
        }else {
            toSearchFromService(CURRENT_SEARCH_KEY);
        }
    }

    public void removeFootLoadmoreItem(){
        godLists.removeItem(mLoadmoreFootViewModel);
    }

    public void restoreFootLoadmoreItem(){
        godLists.insertItem(mLoadmoreFootViewModel);
    }

    @Override
    public void showPageStateSuccess(List<ID> listData){
        super.showPageStateSuccess(listData);
        //子类注意重写 addMoreData逻辑 不然一直可以无限上拉加载
        if(mCurrentPage == FIRST_PAGE) {
            refreshedAllData(listData);
        }else {
            addMoreData(listData);
        }
    }

    /**
     * 重新 刷新 到 第一页数据<br>
     * 更新全部数据 回到第一页<br>
     * List 的数据元素 建议继承  {@link ExtrasBindViewModel}，当然也可以直接继承{@link BaseObservable}
     *
     * @param newData
     */
    protected final void refreshedAllData(List<ID> newData){
        refreshedAllData(newData, false);
    }

    /**
     * 只有在 newData的元素是{@link IRecvDataDiff}的子类时 detectMoves 才有效
     *
     * @param newData
     * @param detectMoves
     */
    protected void refreshedAllData(List<ID> newData, boolean detectMoves){
        checkOrignParam();
        LOG("=========== refreshedAllData ===========", newData.size());
        if(mDataLists.isEmpty()) {
            mDataLists.addAll(newData);
        }else {
            mDataLists.clear();
            mDataLists.addAll(newData);
            //ID不一定是 IRecvDataDiff 的子类
            //            if(!mDiffObservableList.set(mDataLists).detectMoves(detectMoves).update(newData)) {
            //                mDataLists.clear();
            //                mDataLists.addAll(newData);
            //            }
        }
        hideLoading();
    }

    public void addMoreData(List<ID> moreData){
        addMoreData(moreData, true, null);
    }

    public void addMoreData(List<ID> moreData, boolean hasNext, String tips){
        LOG("=========== addMoreData ===========", hasNext, tips);
        mLoadmoreControl.setLoadmoreFinished(!hasNext, tips);//有下一页 finish就是false不结束
        mDataLists.addAll(moreData);
    }

    public void addMoreData(List<ID> moreData, boolean hasNext){
        addMoreData(moreData, hasNext, null);
    }

    @Override
    public void showPageStateError(@PageDiffState int pageDiffState){
        if(mCurrentPage == FIRST_PAGE) {
            //网络错误等 加载数据失败
            super.showPageStateError(pageDiffState);
        }else {
            //上拉加载 失败
            mLoadmoreControl.loadMoreFail();
            switchSwipeRefresh(true);
        }
    }

    @Override
    public void showPageStateError(@PageDiffState int pageDiffState, String errTips){
        if(mCurrentPage == FIRST_PAGE) {
            //网络错误等 加载数据失败
            super.showPageStateError(pageDiffState, errTips);
        }else {
            //上拉加载 失败
            mLoadmoreControl.loadMoreFail(errTips);
            switchSwipeRefresh(true);
        }
    }

    @Override
    public void onRetry(int layoutState){
        down2RefreshData();
    }
}
