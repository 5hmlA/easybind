package jzy.easybind.bindstar.viewmodel;

import jzy.easybind.BR;
import jzy.easybind.R;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.collections.JObservableList;
import me.tatarka.bindingcollectionadapter2.itembindings.ExtrasBindViewModel;
import me.tatarka.bindingcollectionadapter2.recv.LayoutManagers;

/**
 * Created by evan on 6/14/15.
 * 内部 recycleview
 */
public class ItemRecvViewModel extends ExtrasBindViewModel implements LayoutManagers.FullSpan {

    public static final int layoutRes = R.layout.bind_item_recv;

    //所有的数据集合 必须是 JObservableList
    public JObservableList<ItemImgModel> dataList = new JObservableList<ItemImgModel>();


    //OnItemBindClass
    //注册 item 和 对应的布局
    public ItemBinding singleBinding = ItemBinding.of(BR.itemImgModel, R.layout.bind_item_tv_iv);


    public ItemRecvViewModel(){
        for(int i = 0; i<10; i++) {
            dataList.add(new ItemImgModel("默认数据"+i));
        }
    }

    //public ItemRecvViewModel(FragmentRecyclerView clickener){
    //    for(int i = 0; i<10; i++) {
    //        items.add(new ItemImgModel(i));
    //    }
    //    recvListBinding.bindExtra(me.tatarka.bindingcollectionadapter.sample.BR.clickListener, clickener);
    //}
}
