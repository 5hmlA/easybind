package me.tatarka.bindingcollectionadapter.sample;

import android.databinding.Bindable;
import android.view.View;
import android.widget.Toast;

import me.tatarka.bindingcollectionadapter2.collections.IRecvDataDiff;
import me.tatarka.bindingcollectionadapter2.itembindings.ExtrasBindViewModel;


/**
 * Created by evan on 6/14/15.
 */
public class ItemViewModel extends ExtrasBindViewModel {
    public static int layoutRes = R.layout.item;
    public final boolean checkable;
    @Bindable public int index;
    @Bindable public boolean checked;

    public ItemViewModel(MyViewModel myViewModel, int index, boolean checkable){
        this.index = index;
        this.checkable = checkable;
        bindExtra(BR.listener, myViewModel);
        bindExtra(BR.listener2, new textviewExtra());
    }

    public ItemViewModel(int index, boolean checkable){
        this.index = index;
        this.checkable = checkable;
    }

    //    public int getIndex() {
    //        return index;
    //    }
    //
    //    public boolean isChecked() {
    //        return checked;
    //    }

    public boolean onToggleChecked(View v){
        //        if (!checkable) {
        //            return false;
        //        }
        checked = !checked;
        notifyPropertyChanged(BR.checked);
        return true;
    }

    public void clickShow(View v, int index){

        Toast.makeText(v.getContext(), index+"", Toast.LENGTH_SHORT).show();
    }

    public static class textviewExtra {
        public void clickShow(View v, int index){

            Toast.makeText(v.getContext(), index+"----", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean areContentsTheSame(IRecvDataDiff oldData, IRecvDataDiff newData){
        if(oldData instanceof ItemViewModel) {
            return ( (ItemViewModel)oldData ).index==( (ItemViewModel)newData ).index;
        }else {
            return true;
        }
    }
}
