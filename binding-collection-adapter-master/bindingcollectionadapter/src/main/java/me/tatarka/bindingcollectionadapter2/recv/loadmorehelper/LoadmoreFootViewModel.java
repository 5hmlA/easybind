package me.tatarka.bindingcollectionadapter2.recv.loadmorehelper;

import android.databinding.Bindable;
import android.databinding.Observable;

import com.android.databinding.library.baseAdapters.BR;

import me.tatarka.bindingcollectionadapter2.R;
import me.tatarka.bindingcollectionadapter2.itembindings.ExtrasBindViewModel;
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;
import me.tatarka.bindingcollectionadapter2.view_adapter.LoadMoreWrapperAdapter;

import static me.tatarka.bindingcollectionadapter2.Utils.LOG;

/**
 * @another 江祖赟
 * @date 2017/12/15.
 */
public class LoadmoreFootViewModel extends ExtrasBindViewModel {
    public static final int layoutRes = R.layout.loadmore_holder;
    /**
     * 加载失败/加载结束(没有下一页数据)
     */
    @Bindable public boolean moreload_fail = false;
    @Bindable public boolean moreload_finish = false;
    public String mTips;
    private LoadMoreWrapperAdapter.OnLoadmoreControl loadmoreControl;

    public LoadmoreFootViewModel(LoadMoreWrapperAdapter.OnLoadmoreControl moreloadControl){
        loadmoreControl = moreloadControl;
        loadmoreControl.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i){
                if(i == me.tatarka.bindingcollectionadapter2.BR.loadmoreFinished) {
                    if(loadmoreControl.isLoadmoreFinished()) {
                        LOG("上拉加载 结束", loadmoreControl.mLoadFinishTips);
                        switch2LoadFinish(loadmoreControl.mLoadFinishTips);
                    }else {
                        moreload_finish = false;
                        LOG("重启 上拉加载", loadmoreControl.mLoadingTips);
                        switch2Loading(loadmoreControl.mLoadingTips);
                    }
                }else if(i == me.tatarka.bindingcollectionadapter2.BR.loadmoreFailed) {
                    //上拉加载成功/失败
                    if(loadmoreControl.isLoadmoreFailed()) {
                        LOG("上拉加载 失败", loadmoreControl.mLoadFailTips);
                        switch2LoadFail(loadmoreControl.mLoadFailTips);
                    }else {
                        LOG("上拉加载 成功", loadmoreControl.mLoadingTips);
                        switch2Loading(loadmoreControl.mLoadingTips);
                    }
                }
            }
        });
        bindExtra(BR.loadmorelistener, moreloadControl);
    }

    /**
     * 上拉加载失败
     *
     * @param loadFailTips
     */
    private void switch2LoadFail(String loadFailTips){
        mTips = loadFailTips;
        moreload_fail = ( true );
        notifyPropertyChanged(BR.moreload_fail);
    }

    /**
     * 上拉加载ing
     *
     * @param loadingTips
     */
    private void switch2Loading(String loadingTips){
        mTips = loadingTips;
        moreload_fail = ( false );
        notifyPropertyChanged(BR.moreload_fail);
    }

    /**
     * 加载结束(没有下一页数据)
     *
     * @param finishTips
     *         设置 加载 成功/失败
     */
    private void switch2LoadFinish(String finishTips){
        mTips = finishTips;
        //加载结束 但是 下拉刷新之后 要重置
        moreload_fail = moreload_finish = ( true );
        notifyPropertyChanged(BR.moreload_fail);
    }

    public static OnItemBindClass wrapperLoadMoreBinding(OnItemBindClass onItemBindClass){
        return onItemBindClass.regist(LoadmoreFootViewModel.class, BR.loadmoreFoot, LoadmoreFootViewModel.layoutRes);
    }
}
