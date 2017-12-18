package me.tatarka.bindingcollectionadapter.sample;

import android.arch.lifecycle.ViewModel;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import easybind.jzy.bindingstar.loadmorehelper.LoadmoreFootViewModel;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.collections.JObservableList;
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList;
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;
import me.tatarka.bindingcollectionadapter2.view_adapter.BindingListViewAdapter;
import me.tatarka.bindingcollectionadapter2.view_adapter.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.view_adapter.BindingViewPagerAdapter;
import me.tatarka.bindingcollectionadapter2.view_adapter.LoadMoreWrapperAdapter;

public class MyViewModel extends ViewModel {
    private boolean checkable;

    public final LoadMoreWrapperAdapter.OnLoadmoreControl loadmoreListener = new LoadMoreWrapperAdapter.OnLoadmoreControl() {
        @Override
        public void onUp2Loadmore(RecyclerView recyclerView){
            System.out.println("==============================");
            if(items.size()<=3) {
                //                mMoreLoadViewModel.onMoreloadFail(true);
                loadmoreListener.loadMoreFail("自定义加载失败");
                //                mMoreLoadViewModel.setMoreloadFail("自定义");
                //                for(int i = 0; i<2; i++) {
                //                    mDataLists.add(new ItemViewModel(MyViewModel.this, i, true));
                //                }
            }
            if(items.size()>10) {
                loadmoreListener.loadmoreFinished();
            }
        }


        @Override
        public void onLoadmoreRetry(){
            for(int i = 0; i<2; i++) {
                items.add(new ItemViewModel(MyViewModel.this, i, true));
                loadmoreListener.loadmoreSucceed();
            }
        }
    };

    public final JObservableList<Object> items = new JObservableList<Object>(){
        {
                        add("Header");
                        add(new ItemRecvViewModel());
                        add("Header");
        }
    };
//    public final ObservableList<ItemViewModel> mDataLists = new ObservableArrayList<>();

    private final LoadmoreFootViewModel mMoreLoadViewModel = new LoadmoreFootViewModel(loadmoreListener);
    /**
     * Items merged with a header on top and footer on bottom.
     */
    public final MergeObservableList<Object> headerFooterItems = new MergeObservableList<>()
            .insertList(items).insertItem(mMoreLoadViewModel);

    /**
     * Custom adapter that logs calls.
     */
    public final LoggingRecyclerViewAdapter<Object> adapter = new LoggingRecyclerViewAdapter<>();


    public MyViewModel(){
        for(int i = 0; i<5; i++) {
            items.add(new ItemViewModel(this, i, checkable));
        }
    }


    public void setRecvItemClick(FragmentRecyclerView listener){
//        headerFooterItems.insertItem(0, new ItemRecvViewModel(listener));
    }


    public void setCheckable(boolean checkable){
        this.checkable = checkable;
    }


    /**
     * Binds a homogeneous list of mDataLists to a layout.
     */
    public final ItemBinding<ItemViewModel> singleItem = ItemBinding.of(BR.item, R.layout.item);

    /**
     * Binds multiple mDataLists types to different layouts based on class. This could have also be
     * written manually as
     * <pre>{@code
     * public final OnItemBind<Object> multipleItems = new OnItemBind<Object>() {
     *     @Override
     *     public void updateItemLayoutRes(ItemBinding itemBinding, int position, Object item) {
     *         if (String.class.equals(item.getClass())) {
     *             itemBinding.set(BR.item, R.layout.item_header_footer);
     *         } else if (ItemViewModel.class.equals(item.getClass())) {
     *             itemBinding.set(BR.item, R.layout.item);
     *         }
     *     }
     * };
     * }</pre>
     */
    public final OnItemBindClass<Object> multipleItems = new OnItemBindClass<>()
            .regist(String.class, BR.item, R.layout.item_header_footer)
            .regist(ItemRecvViewModel.class, BR.recvItem, R.layout.item_recy_test)
            .regist(ItemViewModel.class, BR.item, ItemViewModel.layoutRes)
            .regist(LoadmoreFootViewModel.class, BR.loadmoreFoot, LoadmoreFootViewModel.layoutRes);

    /**
     * Define stable item ids. These are just based on position because the mDataLists happen to not
     * every move around.
     */
    public final BindingListViewAdapter.ItemIds<Object> itemIds = new BindingListViewAdapter.ItemIds<Object>() {
        @Override
        public long getItemId(int position, Object item){
            return position;
        }
    };

    /**
     * Define page titles for a ViewPager
     */
    public final BindingViewPagerAdapter.PageTitles<ItemViewModel> pageTitles = new BindingViewPagerAdapter.PageTitles<ItemViewModel>() {
        @Override
        public CharSequence getPageTitle(int position, ItemViewModel item){
            return "Item "+( item.index+1 );
        }
    };

    /**
     * Custom view holders for RecyclerView
     */
    public final BindingRecyclerViewAdapter.ViewHolderFactory viewHolder = new BindingRecyclerViewAdapter.ViewHolderFactory() {
        @Override
        public RecyclerView.ViewHolder createViewHolder(ViewDataBinding binding){
            return new MyAwesomeViewHolder(binding.getRoot());
        }
    };

    private static class MyAwesomeViewHolder extends RecyclerView.ViewHolder {
        public MyAwesomeViewHolder(View itemView){
            super(itemView);
        }
    }


    public void addItem(){
        loadmoreListener.forceDown2Refresh();
        items.add(new ItemViewModel(items.size(), checkable));
    }


    public void removeItem(){
        if(items.size() == 11) {
            items.clear();
        }else if(items.size() == 12) {
            items.clear();
            items.add("Header");
            items.add(new ItemRecvViewModel());
            items.add("Header");
        }else if(items.size()>1) {
            items.remove(items.size()-1);
        }
    }
}
