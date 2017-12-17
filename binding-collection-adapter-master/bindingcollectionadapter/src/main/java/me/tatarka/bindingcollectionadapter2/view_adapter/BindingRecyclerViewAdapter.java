package me.tatarka.bindingcollectionadapter2.view_adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import me.tatarka.bindingcollectionadapter2.BindingCollectionAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.Utils;
import me.tatarka.bindingcollectionadapter2.collections.JObservableList;
import me.tatarka.bindingcollectionadapter2.recv.LayoutManagers;

/**
 * A {@link RecyclerView.Adapter} that binds mDataLists to layouts using the given {@link ItemBinding}.
 * If you give it an {@link ObservableList} it will also updated itself based on changes to that
 * list.
 */
public class BindingRecyclerViewAdapter<T> extends RecyclerView.Adapter<ViewHolder>
        implements BindingCollectionAdapter<T>, JObservableList.JOnListChangedCallback<T> {
    private static final Object DATA_INVALIDATION = new Object();
    public static final String TAG = BindingRecyclerViewAdapter.class.getSimpleName();
    protected ItemBinding<T> itemBinding;
    protected JObservableList<T> items;
    protected LayoutInflater inflater;
    protected ItemIds<? super T> itemIds;
    protected ViewHolderFactory viewHolderFactory;
    // Currently attached recyclerview, we don't have to listen to notifications if null.
    @Nullable protected RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    public void setItemBinding(ItemBinding<T> itemBinding){
        this.itemBinding = itemBinding;
    }

    @Override
    public ItemBinding<T> getItemBinding(){
        return itemBinding;
    }

    @Override
    public void setItems(@Nullable List<T> items){
        if(this.items == items || !( items instanceof JObservableList )) {
            return;
        }
        // If a recyclerview is listening, set up listeners. Otherwise wait until one is attached.
        // No need to make a sound if nobody is listening right?
        this.items = (JObservableList<T>)items;
        if(recyclerView != null) {
            this.items.addOnListChangedCallback2(this);
        }
        notifyDataSetChanged();
    }

    @Override
    public T getAdapterItem(int position){
        return items.get(position);
    }

    @Override
    public ViewDataBinding onCreateBinding(LayoutInflater inflater, @LayoutRes int layoutId, ViewGroup viewGroup){
        return DataBindingUtil.inflate(inflater, layoutId, viewGroup, false);
    }

    @Override
    public void onBindBinding(ViewDataBinding binding, int variableId, @LayoutRes int layoutRes, int position, T item){
        if(itemBinding.bind(binding, item)) {
            binding.executePendingBindings();
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        if(this.recyclerView == null && items != null) {
            items.addOnListChangedCallback2(this);
        }
        this.recyclerView = recyclerView;
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder){
        super.onViewAttachedToWindow(holder);
        if(recyclerView != null) {
            checkLayoutManager(holder);
        }
    }

    private void checkLayoutManager(ViewHolder holder){
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if(lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams)lp;
            T itemData = items.get(holder.getAdapterPosition());
            if(itemData != null && itemData instanceof LayoutManagers.FullSpan) {
                params.setFullSpan(true);
            }else {
                params.setFullSpan(false);
            }
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView){
        if(this.recyclerView != null && items != null) {
            items.removeOnListChangedCallback2(this);
        }
        this.recyclerView = null;
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup viewGroup, int layoutId){
        if(inflater == null) {
            inflater = LayoutInflater.from(viewGroup.getContext());
        }
        ViewDataBinding binding = onCreateBinding(inflater, layoutId, viewGroup);
        final ViewHolder holder = onCreateViewHolder(binding);
        binding.addOnRebindCallback(new OnRebindCallback() {
            @Override
            public boolean onPreBind(ViewDataBinding binding){
                return recyclerView != null && recyclerView.isComputingLayout();
            }

            @Override
            public void onCanceled(ViewDataBinding binding){
                if(recyclerView == null || recyclerView.isComputingLayout()) {
                    return;
                }
                int position = holder.getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    notifyItemChanged(position, DATA_INVALIDATION);
                }
            }
        });
        return holder;
    }

    /**
     * Constructs a view holder for the given databinding. The default implementation is to use
     * {@link ViewHolderFactory} if provided, otherwise use a default view holder.
     */
    public ViewHolder onCreateViewHolder(ViewDataBinding binding){
        if(viewHolderFactory != null) {
            return viewHolderFactory.createViewHolder(binding);
        }else {
            return new BindingViewHolder(binding);
        }
    }

    public void configLayoutManager(final RecyclerView.LayoutManager layoutManager){
        mLayoutManager = layoutManager;
        if(mLayoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager)mLayoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position){
                    return items.get(
                            position) instanceof LayoutManagers.FullSpan ? gridLayoutManager.getSpanCount() : 1;
                }
            });
        }
    }

    private static class BindingViewHolder extends ViewHolder {
        public BindingViewHolder(ViewDataBinding binding){
            super(binding.getRoot());
        }
    }

    @Override
    public final void onBindViewHolder(ViewHolder viewHolder, int position){
        T item = items.get(position);
        ViewDataBinding binding = DataBindingUtil.getBinding(viewHolder.itemView);
        onBindBinding(binding, itemBinding.variableId(), itemBinding.layoutRes(), position, item);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads){
        if(isForDataBinding(payloads)) {
            ViewDataBinding binding = DataBindingUtil.getBinding(holder.itemView);
            binding.executePendingBindings();
        }else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    private boolean isForDataBinding(List<Object> payloads){
        if(payloads == null || payloads.size() == 0) {
            return false;
        }
        for(int i = 0; i<payloads.size(); i++) {
            Object obj = payloads.get(i);
            if(obj != DATA_INVALIDATION) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getItemViewType(int position){
        T itemData = items.get(position);
        itemBinding.updateItemLayoutRes(position, itemData);
        return itemBinding.layoutRes();
    }

    /**
     * Set the item id's for the mDataLists. If not null, this will set {@link
     * RecyclerView.Adapter#setHasStableIds(boolean)} to true.
     */
    public void setItemIds(@Nullable ItemIds<? super T> itemIds){
        if(this.itemIds != itemIds) {
            this.itemIds = itemIds;
            setHasStableIds(itemIds != null);
        }
    }

    /**
     * Set the factory for creating view holders. If null, a default view holder will be used. This
     * is useful for holding custom state in the view holder or other more complex customization.
     */
    public void setViewHolderFactory(@Nullable ViewHolderFactory factory){
        viewHolderFactory = factory;
    }

    /**
     * 包括 底部的一个loading   总数+1
     * @return
     */
    @Override
    public int getItemCount(){
        return items == null ? 0 : items.size();
    }

    @Override
    public long getItemId(int position){
        return itemIds == null ? position : itemIds.getItemId(position, items.get(position));
    }

    @Override
    public void onChanged(JObservableList ts, int position, int count, Object payload){
        Utils.ensureChangeOnMainThread();
        if(count == 1) {
            notifyItemChanged(position, 0);
        }else {
            for(int i = 0; i<count; i++) {
                notifyItemChanged(position+i, 0);
            }
        }
    }

    @Override
    public void onItemRangeInserted(JObservableList<T> ts, int positionStart, int itemCount){
        Utils.ensureChangeOnMainThread();
        if(itemCount == getItemCount()-1) {//减去 底部loading
            //刷新全部 数据 不包括底部loading
            notifyDataSetChanged();
        }else {
            notifyItemRangeInserted(positionStart, itemCount);
        }
        // adapter.inserted -- oncreateholder--binderholder(数据来自item)
    }

    @Override
    public void onItemRangeRemoved(JObservableList ts, int positionStart, int itemCount){
        Utils.ensureChangeOnMainThread();
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    @Override
    public void onMoved(JObservableList ts, int fromPosition, int toPosition){
        Utils.ensureChangeOnMainThread();
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onClear(JObservableList ts, int itemCount){
        //        this.recyclerView.scrollToPosition(0);
        onItemRangeRemoved(ts, 0, itemCount);
        //        if(getItemCount() == 1) {
        //            LOG("清空数据了,只剩下 loading");
        //        }
    }

    public interface ItemIds<T> {
        long getItemId(int position, T item);
    }

    public interface ViewHolderFactory {
        ViewHolder createViewHolder(ViewDataBinding binding);
    }
}
