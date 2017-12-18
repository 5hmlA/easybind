package me.tatarka.bindingcollectionadapter2.view_adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import me.tatarka.bindingcollectionadapter2.BindingCollectionAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.Utils;
import me.tatarka.bindingcollectionadapter2.collections.JObservableList;

/**
 * A {@link BaseAdapter} that binds mDataLists to layouts using the given {@link ItemBinding} If you give
 * it an {@link ObservableList} it will also updated itself based on changes to that list.
 */
public class BindingListViewAdapter<T> extends BaseAdapter implements BindingCollectionAdapter<T>,JObservableList.JOnListChangedCallback {
    private final int itemTypeCount;
    private ItemBinding<T> itemBinding;
    @LayoutRes
    private int dropDownItemLayout;
    private JObservableList<T> items;
    private int[] layouts;
    private LayoutInflater inflater;
    private ItemIds<? super T> itemIds;
    private ItemIsEnabled<? super T> itemIsEnabled;

    /**
     * Constructs a new instance with the given item count.
     */
    public BindingListViewAdapter(int itemTypeCount) {
        this.itemTypeCount = itemTypeCount;
    }

    @Override
    public void setItemBinding(ItemBinding<T> itemBinding) {
        this.itemBinding = itemBinding;
    }

    @Override
    public ItemBinding<T> getItemBinding() {
        return itemBinding;
    }

    /**
     * Set a different layout to show for {@link #getDropDownView(int, View, ViewGroup)}. If this is
     * null, it will default to {@link #getView(int, View, ViewGroup)}.
     */
    public void setDropDownItemLayout(@LayoutRes int layoutRes) {
        dropDownItemLayout = layoutRes;
    }

    @Override
    public void setItems(@Nullable JObservableList<T> items) {
        if(this.items == items ) {
            return;
        }
        // If a recyclerview is listening, set up listeners. Otherwise wait until one is attached.
        // No need to make a sound if nobody is listening right?
        this.items = items;
        this.items.addOnListChangedCallback2(this);
        notifyDataSetChanged();
    }

    @Override
    public T getAdapterItem(int position) {
        return items.get(position);
    }

    @Override
    public ViewDataBinding onCreateBinding(LayoutInflater inflater, @LayoutRes int layoutRes, ViewGroup viewGroup) {
        return DataBindingUtil.inflate(inflater, layoutRes, viewGroup, false);
    }

    @Override
    public void onBindBinding(ViewDataBinding binding, int variableId, @LayoutRes int layoutRes, int position, T item) {
        if (itemBinding.bind(binding, item)) {
            binding.executePendingBindings();
        }
    }

    /**
     * Set the item id's for the mDataLists. If not null, this will make {@link #hasStableIds()} return
     * true.
     */
    public void setItemIds(@Nullable ItemIds<? super T> itemIds) {
        this.itemIds = itemIds;
    }

    /**
     * Sets {@link #isEnabled(int)} for the mDataLists.
     */
    public void setItemIsEnabled(@Nullable ItemIsEnabled<? super T> itemIsEnabled) {
        this.itemIsEnabled = itemIsEnabled;
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return itemIds == null ? position : itemIds.getItemId(position, items.get(position));
    }

    @Override
    public boolean isEnabled(int position) {
        return itemIsEnabled == null || itemIsEnabled.isEnabled(position, items.get(position));
    }

    @Override
    public final View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        int viewType = getItemViewType(position);
        int layoutRes = layouts[viewType];

        ViewDataBinding binding;
        if (convertView == null) {
            binding = onCreateBinding(inflater, layoutRes, parent);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }

        T item = items.get(position);
        onBindBinding(binding, itemBinding.variableId(), layoutRes, position, item);

        return binding.getRoot();
    }

    @Override
    public final View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        if (dropDownItemLayout == 0) {
            return super.getDropDownView(position, convertView, parent);
        } else {
            int layoutRes = dropDownItemLayout;
            ViewDataBinding binding;
            if (convertView == null) {
                binding = onCreateBinding(inflater, layoutRes, parent);
            } else {
                binding = DataBindingUtil.getBinding(convertView);
            }

            T item = items.get(position);
            onBindBinding(binding, itemBinding.variableId(), layoutRes, position, item);

            return binding.getRoot();
        }
    }

    @Override
    public int getItemViewType(int position) {
        ensureLayoutsInit();
        T item = items.get(position);
        itemBinding.updateItemLayoutRes(position, item);

        int firstEmpty = 0;
        for (int i = 0; i < layouts.length; i++) {
            if (itemBinding.layoutRes() == layouts[i]) {
                return i;
            }
            if (layouts[i] == 0) {
                firstEmpty = i;
            }
        }
        layouts[firstEmpty] = itemBinding.layoutRes();
        return firstEmpty;
    }

    @Override
    public boolean hasStableIds() {
        return itemIds != null;
    }

    @Override
    public int getViewTypeCount() {
        return ensureLayoutsInit();
    }

    private int ensureLayoutsInit() {
        int count = itemTypeCount;
        if (layouts == null) {
            layouts = new int[count];
        }
        return count;
    }

    @Override
    public void onChanged(JObservableList jObservableList, int fromPosition, int count, Object payload){
        Utils.ensureChangeOnMainThread();
        notifyDataSetChanged();
    }

    @Override
    public void onItemRangeInserted(JObservableList jObservableList, int fromPosition, int count){
        Utils.ensureChangeOnMainThread();
        notifyDataSetChanged();
    }

    @Override
    public void onItemRangeRemoved(JObservableList jObservableList, int fromPosition, int count){
        Utils.ensureChangeOnMainThread();
        notifyDataSetChanged();
    }

    @Override
    public void onMoved(JObservableList jObservableList, int fromPosition, int toPosition){
        Utils.ensureChangeOnMainThread();
        notifyDataSetChanged();
    }

    @Override
    public void onClear(JObservableList jObservableList, int oldSize){
        Utils.ensureChangeOnMainThread();
        notifyDataSetChanged();
    }

    public interface ItemIds<T> {
        long getItemId(int position, T item);
    }

    public interface ItemIsEnabled<T> {
        boolean isEnabled(int position, T item);
    }
}
