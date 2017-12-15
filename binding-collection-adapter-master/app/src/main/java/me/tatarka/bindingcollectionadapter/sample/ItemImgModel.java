package me.tatarka.bindingcollectionadapter.sample;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

/**
 * Created by evan on 6/14/15.
 */
public class ItemImgModel extends BaseObservable {
    public final boolean checkable;
    @Bindable
    public int index;
    @Bindable
    public boolean checked;

    public ItemImgModel(int index) {
        this.index = index;
        this.checkable = true;
    }

//    public int getIndex() {
//        return index;
//    }
//
//    public boolean isChecked() {
//        return checked;
//    }
    
    public boolean onToggleChecked(View v) {
//        if (!checkable) {
//            return false;
//        }
        checked = !checked;
        notifyPropertyChanged(BR.checked);
        return true;
    }
}
