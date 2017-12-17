package me.tatarka.bindingcollectionadapter.sample;

import android.support.v7.widget.RecyclerView;

import com.jzy.bindingstar.pagingviewmodel.loadmorehelper.BaseLoadmoreViewModel;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import me.tatarka.bindingcollectionadapter2.collections.JObservableList;
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;

import static me.tatarka.bindingcollectionadapter2.Utils.LOG;

public class RecycleViewModel extends BaseLoadmoreViewModel {

    private JObservableList refreshList = new JObservableList();

    public RecycleViewModel(){
//        mDataLists.add(new ItemRecvViewModel());
        for(int i = 0; i<6; i++) {
            ItemViewModel object = new ItemViewModel(i, true);
            mDataLists.add(object);
            refreshList.add(object);
//            refreshList.add(new ItemViewModel(i, true));
        }
//        refreshList.move(2,0);
//        for(int i = 0; i<20; i++) {

        refreshList.add(new ItemViewModel(55, true));
//        }
        refreshList.remove(3);
        refreshList.remove(3);
        refreshList.remove(0);
    }

    //    @Override
    //    public LayoutManagers.LayoutManagerFactory layoutManager(){
    //        return LayoutManagers.grid(2);
    //    }

    public void addItem(){
        mDataLists.add(new ItemViewModel(110, true));
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
        multipleItems.regist(String.class, me.tatarka.bindingcollectionadapter.sample.BR.item,
                             R.layout.item_header_footer).regist(ItemRecvViewModel.class,
                                                                 me.tatarka.bindingcollectionadapter.sample.BR.recvItem,
                                                                 R.layout.item_recy_test).regist(ItemViewModel.class,
                                                                                                 me.tatarka.bindingcollectionadapter.sample.BR.item,
                                                                                                 ItemViewModel.layoutRes);
    }

    @Override
    public void subscribeData(Object orignParam){
        final ArrayList mDataLists = new ArrayList();
        //        mDataLists.add(new ItemRecvViewModel());
//        for(int i = 0; i<11; i++) {
//            mDataLists.add(new ItemViewModel(i, true));
//        }
        Observable.just(mDataLists).delay(2, TimeUnit.SECONDS).observeOn(
                AndroidSchedulers.mainThread()).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception{
                showPageStateSuccess(refreshList);
            }
        });
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
            mDataLists.add(new ItemViewModel(i, true));
            mLoadmoreControl.loadmoreSucceed();
        }
        JObservableList list2 = new JObservableList();
    }

}
