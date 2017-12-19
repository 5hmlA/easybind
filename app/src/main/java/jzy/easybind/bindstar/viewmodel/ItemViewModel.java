package jzy.easybind.bindstar.viewmodel;

import android.databinding.Bindable;
import android.view.View;
import android.widget.Toast;

import jzy.easybind.BR;
import jzy.easybind.R;
import me.tatarka.bindingcollectionadapter2.collections.IRecvDataDiff;
import me.tatarka.bindingcollectionadapter2.itembindings.ExtrasBindViewModel;

/**
 * Created by evan on 6/14/15.
 */
public class ItemViewModel extends ExtrasBindViewModel {
    public static final int layoutRes = R.layout.bind_item_tv;
    @Bindable public String content;
    @Bindable public boolean checked;


    public ItemViewModel(PageRecvViewModel recvViewModel, String content) {
        this.content = content;
        bindExtra(BR.outRecvViewModel,recvViewModel);
    }


    public ItemViewModel(String content) {
        this.content = content;
    }

    public void clickShow(View v, String show) {
        Toast.makeText(v.getContext(), show, Toast.LENGTH_SHORT).show();
    }


    public static class textviewExtra {

        public void clickShow(View v, int index) {

            Toast.makeText(v.getContext(), index + "----", Toast.LENGTH_SHORT).show();
        }
    }


    @Override public boolean areContentsTheSame(IRecvDataDiff oldData, IRecvDataDiff newData) {
        if (oldData instanceof ItemViewModel) {
            return ((ItemViewModel) oldData).content == ((ItemViewModel) newData).content;
        }
        else {
            return true;
        }
    }
}
