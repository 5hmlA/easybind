package easybind.jzy.bindingstar.loadmorehelper;

import android.databinding.BaseObservable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import easybind.jzy.bindingstar.ScrollChildSwipeRefreshLayout;
import easybind.jzy.bindingstar.statehelper.PageDiffState;
import easybind.jzy.bindingstar.statehelper.StateDiffViewModel;
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList;
import me.tatarka.bindingcollectionadapter2.collections.JObservableList;
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList;
import me.tatarka.bindingcollectionadapter2.itembindings.ExtrasBindViewModel;
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;
import me.tatarka.bindingcollectionadapter2.recv.LayoutManagers;
import me.tatarka.bindingcollectionadapter2.view_adapter.LoadMoreWrapperAdapter;

import static easybind.jzy.bindingstar.loadmorehelper.LoadmoreFootViewModel.wrapperLoadMoreBinding;
import static me.tatarka.bindingcollectionadapter2.Utils.LOG;

/**
 * 为啥 不用泛型 因为 布局里面必须要指定泛型类型 鉴于要接受不同布局 也就时任意数据对象
 * 所以 就固定 为object类型<br>
 */
public abstract class BaseLoadmoreViewModel extends StateDiffViewModel<List<Object>> {

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
     * 列表 项目 数据 不包括 底部加载布局
     */
    public JObservableList mDataLists = new JObservableList<>();

    /**
     * 列表数据{@link #godLists} 在处理上拉加载的ViewModel 中 主要包括两部分:1, 界面的多种类数据. 2, 底部的 上拉加载提示loading
     * <li><b>{@link #mDataLists} 用来收集存储 展示的所有有效数据(不同类型布局)</b></li>
     * <li><b>{@link #mLoadmoreFootViewModel} 就是底部的上拉加载提示 提示的各种状态(loading,error,finish)主要由
     * {@link LoadMoreWrapperAdapter.OnLoadmoreControl}控制,该控制器主要控制上拉加载的状态</b></li>
     */
    public final MergeObservableList<Object> godLists = new MergeObservableList<>().insertList(mDataLists).insertItem(
            mLoadmoreFootViewModel);

    /**
     * 注册 不同类型布局 和 对应的class数据类型
     */
    public final OnItemBindClass<Object> multipleItems = wrapperLoadMoreBinding(new OnItemBindClass<>());
    //======  分页 ====
    public static final long FIRST_PAGE = 1;
    public static String CURRENT_SEARCH_KEY = "";
    private String mLastSearchKey;
    public long mCurrentPage;
    DiffObservableList mDiffObservableList = new DiffObservableList(mDataLists);

    protected RecyclerView mRecyclerView;

    public LayoutManagers.LayoutManagerFactory layoutManager(){
        return LayoutManagers.linear();
    }

    /**
     * 注册 不同的 item 类型
     *
     * @param multipleItems
     */
    protected abstract void registItemTypes(OnItemBindClass<Object> multipleItems);

    {
        registItemTypes(multipleItems);
    }

    /**
     * @param v
     *         主要用来 获取布局中的控件对象
     */
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight,
                               int oldBottom){
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
        }
    }

    // ========================== 上拉加载 逻辑 ==============================


    /**
     * 网络请求参数
     *
     * @param orignParam
     */
    public void putOrignParam(Object orignParam){
        mOrignParam = orignParam;
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
     * <li>默认 回掉 {@link #subscribeData(Object)}</li>
     *
     * @param key
     */
    protected void toSearchFromService(String key){
        LOG("=========== toSearchFromService ===========");
        /* 发请求 去搜索关键字吧 */
        subscribeData(mOrignParam);
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
                subscribeData(mOrignParam);
            }else {
                toSearchFromService(CURRENT_SEARCH_KEY);
            }
        }
    }

    @Override
    public void onRefresh(SwipeRefreshLayout swipeRefreshLayout){
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
            subscribeData(mOrignParam);
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
    public void showPageStateSuccess(List listData){
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
    protected final void refreshedAllData(List newData){
        refreshedAllData(newData, false);
        hideLoading();
    }

    public final void refreshedAllData(List newData, boolean detectMoves){
        LOG("=========== refreshedAllData ===========");
        if(mDataLists.isEmpty()) {
            mDataLists.addAll(newData);
        }else {
            try {
                mDiffObservableList.set(mDataLists).detectMoves(detectMoves).update(newData);
            }catch(ClassCastException e) {
                LOG("========== 数据包含 非  IRecvDataDiff 的子类 ===========");
                mDataLists.clear();
                mDataLists.addAll(newData);
            }
        }
    }

    public void addMoreData(List moreData){
        addMoreData(moreData, true, null);
    }

    public void addMoreData(List moreData, boolean hasNext, String tips){
        LOG("=========== addMoreData ===========", hasNext, tips);
        mLoadmoreControl.setLoadmoreFinished(hasNext, tips);
        mDataLists.addAll(moreData);
    }

    public void addMoreData(List moreData, boolean hasNext){
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
