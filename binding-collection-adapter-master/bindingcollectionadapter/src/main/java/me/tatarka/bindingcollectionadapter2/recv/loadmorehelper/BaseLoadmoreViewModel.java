package me.tatarka.bindingcollectionadapter2.recv.loadmorehelper;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import java.util.List;

import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList;
import me.tatarka.bindingcollectionadapter2.itembindings.ExtrasBindViewModel;
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;
import me.tatarka.bindingcollectionadapter2.recv.LayoutManagers;
import me.tatarka.bindingcollectionadapter2.view_adapter.LoadMoreWrapperAdapter;

import static me.tatarka.bindingcollectionadapter2.recv.loadmorehelper.LoadmoreFootViewModel.wrapperLoadMoreBinding;

public abstract class BaseLoadmoreViewModel<D> extends ExtrasBindViewModel {

    /**
     * 上拉加载 控制器{ 上拉加载成功/失败/结束/重启}
     */
    public static LoadMoreWrapperAdapter.OnLoadmoreControl mLoadmoreControl;

    /**
     * 底部 loadmore 布局的viewModel
     */
    protected final LoadmoreFootViewModel mLoadmoreFootViewModel = new LoadmoreFootViewModel(
            mLoadmoreControl = createLoadmoreControl());
    /**
     * 列表 项目 数据 不包括 底部加载布局
     */
    public final ObservableList<D> mDataLists = new ObservableArrayList<>();

    /**
     * 列表数据{@link #mDataLists} 包括 底部的 loading
     */
    public final MergeObservableList<Object> godLists = new MergeObservableList<>().insertList(mDataLists)
            .insertItem(mLoadmoreFootViewModel);

    /**
     * 注册 不同类型布局 和 对应的class数据类型
     */
    public final OnItemBindClass<Object> multipleItems = wrapperLoadMoreBinding(new OnItemBindClass<>());

    {
        registItemTypes(multipleItems);
    }

    public LayoutManagers.LayoutManagerFactory getLayoutManager(){
        return LayoutManagers.linear();
    }


    public void removeFootLoadmoreItem(){
        godLists.removeItem(mLoadmoreFootViewModel);
    }

    public void restoreFootLoadmoreItem(){
        godLists.insertItem(mLoadmoreFootViewModel);
    }

    public void onRefresh(List<D> newData){
        mDataLists.clear();
        mDataLists.addAll(newData);
    }

    public void addMoreData(List<D> moreData){
        mDataLists.addAll(moreData);
    }

    public void addMoreData(List<D> moreData, boolean hasNext, String tips){
        mLoadmoreControl.setLoadmoreFinished(hasNext, tips);
        mDataLists.addAll(moreData);
    }

    public void addMoreData(List<D> moreData, boolean hasNext){
        mLoadmoreControl.setLoadmoreFinished(hasNext, null);
        mDataLists.addAll(moreData);
    }

    protected abstract void registItemTypes(OnItemBindClass<Object> multipleItems);

    /**
     * 加载状态 控制器 {@link #mLoadmoreControl}{加载失败 加载成功 加载结束}
     *
     * @return
     */
    protected abstract LoadMoreWrapperAdapter.OnLoadmoreControl createLoadmoreControl();

}
