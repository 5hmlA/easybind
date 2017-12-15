package me.tatarka.bindingcollectionadapter.sample;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import me.tatarka.bindingcollectionadapter2.ItemBinding;

/**
 * Created by evan on 6/14/15.
 */
public class ItemRecvViewModel extends BaseObservable {

   public ObservableList<ItemImgModel> items = new ObservableArrayList<ItemImgModel>();

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
        recvListBinding.bindExtra(BR.clickListener, clickener);
    }
}
