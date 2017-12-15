package me.tatarka.bindingcollectionadapter.sample;

import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;
import me.tatarka.bindingcollectionadapter2.recv.loadmorehelper.BaseLoadmoreViewModel;
import me.tatarka.bindingcollectionadapter2.view_adapter.LoadMoreWrapperAdapter;

public class RecycleViewModel extends BaseLoadmoreViewModel<Object> {


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
        multipleItems.regist(String.class, BR.item, R.layout.item_header_footer)
                .regist(ItemRecvViewModel.class, BR.recvItem, R.layout.item_recy_test)
                .regist(ItemViewModel.class, BR.item, ItemViewModel.layoutRes);
    }

    @Override
    protected LoadMoreWrapperAdapter.OnLoadmoreControl createLoadmoreControl(){
        return null;
    }
}
