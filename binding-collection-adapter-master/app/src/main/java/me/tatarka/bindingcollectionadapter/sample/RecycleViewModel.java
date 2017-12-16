package me.tatarka.bindingcollectionadapter.sample;

import android.support.v7.widget.RecyclerView;

import com.jzy.bindingstar.pagingviewmodel.loadmorehelper.BaseLoadmoreViewModel;

import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;
import me.tatarka.bindingcollectionadapter2.recv.LayoutManagers;

import static me.tatarka.bindingcollectionadapter2.Utils.LOG;

public class RecycleViewModel extends BaseLoadmoreViewModel<Object> {

    public RecycleViewModel(){
        mDataLists.add(new ItemRecvViewModel());
        for(int i = 0; i<5; i++) {
            mDataLists.add(new ItemViewModel(i, true));
        }
    }

    @Override
    public LayoutManagers.LayoutManagerFactory layoutManager(){
        return LayoutManagers.grid(2);
    }

    public void addItem(){
        mLoadmoreControl.forceDown2Refresh();
        mDataLists.add(new ItemViewModel(mDataLists.size(), true));
    }


    public void removeItem(){
        if(mDataLists.size() == 11) {
            mDataLists.clear();
        }else if(mDataLists.size() == 12) {
            mDataLists.clear();
            mDataLists.add("Header");
            mDataLists.add(new ItemRecvViewModel());
            mDataLists.add("Header");
        }else if(mDataLists.size()>1) {
            mDataLists.remove(mDataLists.size()-1);
        }
    }

    @Override
    protected void registItemTypes(OnItemBindClass<Object> multipleItems){
        multipleItems.regist(String.class, me.tatarka.bindingcollectionadapter.sample.BR.item, R.layout.item_header_footer)
                .regist(ItemRecvViewModel.class, me.tatarka.bindingcollectionadapter.sample.BR.recvItem, R.layout.item_recy_test)
                .regist(ItemViewModel.class, me.tatarka.bindingcollectionadapter.sample.BR.item, ItemViewModel.layoutRes);
    }

    @Override
    public void subscribeData(Object orignParam){

    }

    @Override
    public void up2LoadMoreData(RecyclerView recyclerView){
//        super.up2LoadMoreData(recyclerView);
        LOG("======== up2LoadMoreData ========");
        if(mDataLists.size()<=3) {
            //                mMoreLoadViewModel.onMoreloadFail(true);
            mLoadmoreControl.loadMoreFail("自定义加载失败");
            //                mMoreLoadViewModel.setMoreloadFail("自定义");
            //                for(int i = 0; i<2; i++) {
            //                    mDataLists.add(new ItemViewModel(MyViewModel.this, i, true));
            //                }
        }
        if(mDataLists.size()>10) {
            mLoadmoreControl.loadmoreFinished();
        }
    }

    @Override
    public void retryUp2LoadMoreData(RecyclerView recyclerView){
        super.retryUp2LoadMoreData(recyclerView);
        LOG("======== retryUp2LoadMoreData ========");
        for(int i = 0; i<2; i++) {
            mDataLists.add(new ItemViewModel( i, true));
            mLoadmoreControl.loadmoreSucceed();
        }
    }
}
