package jzy.easybind.bindstar.viewmodel;


import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import jzy.easybind.BR;
import jzy.easybindpagelist.loadmorehelper.LoadMoreViewModel2;
import jzy.easybindpagelist.statehelper.PageDiffState;
import me.tatarka.bindingcollectionadapter2.collections.JObservableList;
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;

public class PageRecvViewModel extends LoadMoreViewModel2 {

    @Override
    protected void registItemTypes(OnItemBindClass<Object> multipleItems){
        multipleItems.regist(ItemRecvViewModel.class, BR.recvItem, ItemRecvViewModel.layoutRes).regist(ItemImgModel.class, BR.itemImgModel, ItemImgModel.layoutRes)
                .regist(ItemViewModel.class, BR.itemViewModel, ItemViewModel.layoutRes);
    }

    @Override
    public void toGetData(HashMap mapParam){

        final JObservableList newData = new JObservableList();
        if(mCurrentPage == FIRST_PAGE && !mDataLists.isEmpty()) {
            int a = new Random().nextInt(refreshList.size()-2);
            int b = new Random().nextInt(refreshList.size()-3);
            if(a != b) {
                refreshList.swap(a, b);
            }else {
                refreshList.remove(a);
            }
            newData.addAll(refreshList);
        }else {
            for(int i = 0; i<11; i++) {
                newData.add(new ItemViewModel(this,"抓取的数据"+( mDataLists.size()+i )));
            }
        }
        if(mDataLists.isEmpty()) {
            newData.add(0,new ItemRecvViewModel());

        }

        Observable.just(1).delay(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new io.reactivex.functions.Consumer<Integer>() {
            @Override
            public void accept(Integer jObservableList) throws Exception{
                if(isFirstPage()) {
                    refreshedAllData(newData, true);
                }else {
                    if(new Random().nextBoolean()) {
                        addMoreData(newData, mDataLists.size()<30,"自定义提示信息");
                    }else {
                        showPageStateError(PageDiffState.PAGE_STATE_ERROR);
                    }
                }
            }
        });
    }


    private JObservableList refreshList = new JObservableList();

    public void deleItem(ItemViewModel o){
        mDataLists.remove(o);
    }

    public PageRecvViewModel(){
        for(int i = 0; i<11; i++) {
            refreshList.add(new ItemViewModel(this,"下拉刷新的数据 "+i));
        }
    }

    public void addItem(){
        mDataLists.add(new ItemViewModel(this,"新增数据"));
    }


    public void removeItem(){
        if(mDataLists.size() == 11) {
            mDataLists.clear();
        }else if(mDataLists.size() == 12) {
            mDataLists.clear();
            mDataLists.add(new ItemViewModel("新增数据 1"));
            mDataLists.add(new ItemRecvViewModel());
            mDataLists.add(new ItemViewModel("新增数据 2"));
        }else if(mDataLists.size()>1) {
            mDataLists.remove(mDataLists.size()-1);
        }
    }
}
