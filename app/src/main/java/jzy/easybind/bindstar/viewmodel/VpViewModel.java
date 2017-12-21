package jzy.easybind.bindstar.viewmodel;


import android.databinding.BaseObservable;

import jzy.easybind.BR;
import jzy.easybind.R;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.collections.JObservableList;
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;
import me.tatarka.bindingcollectionadapter2.view_adapter.BindingViewPagerAdapter;

public class VpViewModel extends BaseObservable {

    public JObservableList mDataLists = new JObservableList();
    public ItemBinding singleItem = ItemBinding.of(BR.itemViewModel, R.layout.bind_item_tv);
    public OnItemBindClass multiItem = new OnItemBindClass().regist(ItemViewModel.class,BR.itemViewModel, R.layout.bind_item_tv).regist(ItemRecvViewModel.class, BR.recvItem,
            ItemRecvViewModel.layoutRes);

    /**
     * Define page titles for a ViewPager
     */
    public final BindingViewPagerAdapter.PageTitles<Object> pageTitles = new BindingViewPagerAdapter.PageTitles<Object>() {
        @Override
        public CharSequence getPageTitle(int position, Object item){
            if(item instanceof ItemViewModel) {

                return ( (ItemViewModel)item ).content;
            }else {
                return "00";
            }
        }
    };

    public void deleItem(ItemViewModel o){
        mDataLists.remove(o);
    }

    public VpViewModel(){
        for(int i = 0; i<4; i++) {
            mDataLists.add(new ItemViewModel("vp数据： "+i));
        }
            mDataLists.add(new ItemRecvViewModel());
    }

    public void addItem(){
        mDataLists.add(new ItemViewModel("新增数据 "+mDataLists.size()));
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
