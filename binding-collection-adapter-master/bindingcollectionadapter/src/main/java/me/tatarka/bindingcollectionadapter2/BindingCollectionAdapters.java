package me.tatarka.bindingcollectionadapter2;

import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.LayoutRes;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.WrapperListAdapter;

import java.util.List;

import me.tatarka.bindingcollectionadapter2.recv.LayoutManagers;
import me.tatarka.bindingcollectionadapter2.view_adapter.BindingListViewAdapter;
import me.tatarka.bindingcollectionadapter2.view_adapter.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.view_adapter.BindingViewPagerAdapter;
import me.tatarka.bindingcollectionadapter2.view_adapter.LoadMoreWrapperAdapter;

/**
 * All the BindingAdapters so that you can set your adapters and mDataLists directly in your layout.
 */
public class BindingCollectionAdapters {
    // AdapterView
    @SuppressWarnings("unchecked")
    @BindingAdapter(value = {"itemBinding", "itemTypeCount", "dataList", "adapter", "itemDropDownLayout", "itemIds", "itemIsEnabled"}, requireAll = false)
    public static <T> void setAdapter(AdapterView adapterView, ItemBinding<T> itemBinding, Integer itemTypeCount, List items, BindingListViewAdapter<T> adapter,
                                      @LayoutRes
                                              int itemDropDownLayout, BindingListViewAdapter.ItemIds<? super T> itemIds, BindingListViewAdapter.ItemIsEnabled<? super T> itemIsEnabled){
        if(itemBinding == null) {
            throw new IllegalArgumentException("updateItemLayoutRes must not be null");
        }
        BindingListViewAdapter<T> oldAdapter = (BindingListViewAdapter<T>)unwrapAdapter(adapterView.getAdapter());
        if(adapter == null) {
            if(oldAdapter == null) {
                int count = itemTypeCount != null ? itemTypeCount : 1;
                adapter = new BindingListViewAdapter<>(count);
            }else {
                adapter = oldAdapter;
            }
        }
        adapter.setItemBinding(itemBinding);
        adapter.setDropDownItemLayout(itemDropDownLayout);
        adapter.setItems(items);
        adapter.setItemIds(itemIds);
        adapter.setItemIsEnabled(itemIsEnabled);

        if(oldAdapter != adapter) {
            adapterView.setAdapter(adapter);
        }
    }


    /**
     * Unwraps any {@link android.widget.WrapperListAdapter}, commonly {@link
     * android.widget.HeaderViewListAdapter}.
     */
    private static Adapter unwrapAdapter(Adapter adapter){
        return adapter instanceof WrapperListAdapter ? unwrapAdapter(
                ( (WrapperListAdapter)adapter ).getWrappedAdapter()) : adapter;
    }


    // ViewPager
    @SuppressWarnings("unchecked")
    @BindingAdapter(value = {"itemBinding", "dataList", "adapter", "pageTitles"}, requireAll = false)
    public static <T> void setAdapter(ViewPager viewPager, ItemBinding<T> itemBinding, List items, BindingViewPagerAdapter<T> adapter, BindingViewPagerAdapter.PageTitles<T> pageTitles){
        if(itemBinding == null) {
            throw new IllegalArgumentException("updateItemLayoutRes must not be null");
        }
        BindingViewPagerAdapter<T> oldAdapter = (BindingViewPagerAdapter<T>)viewPager.getAdapter();
        if(adapter == null) {
            if(oldAdapter == null) {
                adapter = new BindingViewPagerAdapter<>();
            }else {
                adapter = oldAdapter;
            }
        }
        adapter.setItemBinding(itemBinding);
        adapter.setItems(items);
        adapter.setPageTitles(pageTitles);

        if(oldAdapter != adapter) {
            viewPager.setAdapter(adapter);
        }
    }


    @BindingConversion
    public static <T> ItemBinding<T> toItemBinding(OnItemBind<T> onItemBind){
        return ItemBinding.of(onItemBind);
    }


    @BindingConversion
    public static ColorDrawable convertColorToDrawable(int color){
        return new ColorDrawable(color);
    }


    //=============================  recyeleView  ============================================
    // RecyclerView
    @SuppressWarnings("unchecked")
    @BindingAdapter(value = {"itemBinding", "dataList", "adapter", "itemIds", "viewHolder", "loadmoreControl"},
                    requireAll = false)
    public static <T> void setAdapter(RecyclerView recyclerView, ItemBinding<T> itemBinding, List<T> items, BindingRecyclerViewAdapter<T> adapter, BindingRecyclerViewAdapter.ItemIds<? super T> itemIds, BindingRecyclerViewAdapter.ViewHolderFactory viewHolderFactory, LoadMoreWrapperAdapter.OnLoadmoreControl loadmoreControl){
        if(itemBinding == null) {
            throw new IllegalArgumentException("itemBinding must not be null");
        }
        BindingRecyclerViewAdapter oldAdapter = (BindingRecyclerViewAdapter)recyclerView.getAdapter();
        if(adapter == null) {
            if(oldAdapter == null) {
                if(loadmoreControl == null) {
                    adapter = new BindingRecyclerViewAdapter<>();
                }else {
                    adapter = new LoadMoreWrapperAdapter(loadmoreControl);
                }
            }else {
                adapter = oldAdapter;
            }
        }
        adapter.setItemBinding(itemBinding);
        adapter.setItems(items);
        adapter.setItemIds(itemIds);
        adapter.setViewHolderFactory(viewHolderFactory);
        if(oldAdapter != adapter) {
            recyclerView.setAdapter(adapter);
        }
    }


    @BindingAdapter("layoutManager")
    public static void setLayoutManager(RecyclerView recyclerView, LayoutManagers.LayoutManagerFactory layoutManagerFactory){
        recyclerView.setLayoutManager(layoutManagerFactory.create(recyclerView));
    }

    @BindingAdapter("paddingLR")
    public static void setPaddingLeft(View view, float padding){
        view.setPadding(( (int)padding ), view.getPaddingTop(), ( (int)padding ), view.getPaddingBottom());
    }
}