package me.tatarka.bindingcollectionadapter.sample;

import android.databinding.BaseObservable;

import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.collections.JObservableList;
import me.tatarka.bindingcollectionadapter2.recv.LayoutManagers;

/**
 * Created by evan on 6/14/15.
 */
public class ItemRecvViewModel extends BaseObservable implements LayoutManagers.FullSpan{

   public JObservableList<ItemImgModel> items = new JObservableList<ItemImgModel>();

    public ItemBinding recvListBinding = ItemBinding.of(BR.imgModel, R.layout.item_img_test);

    public ItemRecvViewModel(){
        for(int i = 0; i<10; i++) {
            items.add(new ItemImgModel(i));
        }
    }
    public ItemRecvViewModel(FragmentRecyclerView clickener){
        for(int i = 0; i<10; i++) {
            items.add(new ItemImgModel(i));
        }
        recvListBinding.bindExtra(me.tatarka.bindingcollectionadapter.sample.BR.clickListener, clickener);
    }
}
