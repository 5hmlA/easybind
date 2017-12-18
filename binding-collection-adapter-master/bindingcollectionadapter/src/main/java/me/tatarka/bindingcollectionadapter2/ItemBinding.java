package me.tatarka.bindingcollectionadapter2;

import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;

import java.lang.ref.WeakReference;

import me.tatarka.bindingcollectionadapter2.itembindings.ExtrasBindViewModel;
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;

/**
 * Provides the necessary information to bind an item in a collection to a view. This includes the
 * variable id and the layout as well as any extra bindings you may want to provide.
 * 布局类型 -- 布局ID ，绑定事件extraBindings
 * updateItemLayoutRes 干啥的
 *
 * @param <T>
 *         The item type.
 */
public final class ItemBinding<T> {

    /**
     * Use this constant as the variable id to not bind the item in the collection to the layout if
     * no data is need, like a static footer or loading indicator.
     */
    public static final int VAR_NONE = 0;//静态 item 不需要数据
    private static final int VAR_INVALID = -1;
    private static final int LAYOUT_NONE = 0;
    //OnItemBindClass
    private final OnItemBind<T> onItemBind;
    private int variableId;
    @LayoutRes private int layoutRes;
    private SparseArray<WeakReference<Object>> extraBindings;

    /**
     * Constructs an instance with the given variable id and layout.
     *
     * @param variableId  布局中接受数据变量的ID(BR)
     * @param layoutRes  item 对应的布局ID(res)
     * @param <T>
     * @return
     */
    public static <T> ItemBinding<T> of(int variableId, @LayoutRes int layoutRes){
        return new ItemBinding<T>(null).set(variableId, layoutRes);
    }

    /**
     * Constructs an instance with the given callback. It will be called for each item in the
     * collection to set the binding info.
     * 多类型 布局 OnItemBindClass {#BindingCollectionAdapters.toItemBinding()}
     * 主要作用是 将 {@link OnItemBindClass} 转化为 {@link ItemBinding}
     *
     * @see OnItemBind
     */
    public static <T> ItemBinding<T> of(OnItemBind<T> onItemBind){
        if(onItemBind == null) {
            throw new NullPointerException("updateItemLayoutRes == null");
        }
        return new ItemBinding<>(onItemBind);
    }

    private ItemBinding(OnItemBind<T> onItemBind){
        this.onItemBind = onItemBind;
    }

    /**
     * Set the variable id and layout. This is normally called in {@link
     * OnItemBind#onItemBind(ItemBinding, int, Object)}.
     */
    public final ItemBinding<T> set(int variableId, @LayoutRes int layoutRes){
        this.variableId = variableId;
        this.layoutRes = layoutRes;
        return this;
    }

    /**
     * Set the variable id. This is normally called in {@link OnItemBind#onItemBind(ItemBinding, int, Object)}.
     */
    public final ItemBinding<T> variableId(int variableId){
        this.variableId = variableId;
        return this;
    }

    /**
     * Set the layout. This is normally called in {@link OnItemBind#onItemBind(ItemBinding, int, Object)}.
     */
    public final ItemBinding<T> layoutRes(@LayoutRes int layoutRes){
        this.layoutRes = layoutRes;
        return this;
    }

    /**
     * Bind an extra variable to the view with the given variable id. The same instance will be
     * provided to all views the binding is bound to.
     */
    public final ItemBinding<T> bindExtra(int variableId, Object value){
        if(extraBindings == null) {
            extraBindings = new SparseArray<>(1);
        }
        extraBindings.put(variableId, new WeakReference<Object>(value));
        return this;
    }

    /**
     * Clear all extra variables. This is normally called in {@link
     * OnItemBind#onItemBind(ItemBinding, int, Object)}.
     */
    public final ItemBinding<T> clearExtras(){
        if(extraBindings != null) {
            extraBindings.clear();
        }
        return this;
    }

    /**
     * Remove an extra variable with the given variable id. This is normally called in {@link
     * OnItemBind#onItemBind(ItemBinding, int, Object)}.
     */
    public ItemBinding<T> removeExtra(int variableId){
        if(extraBindings != null) {
            extraBindings.remove(variableId);
        }
        return this;
    }

    /**
     * Returns the current variable id of this binding.
     */
    public final int variableId(){
        return variableId;
    }

    /**
     * Returns the current layout fo this binding.
     */
    @LayoutRes
    public final int layoutRes(){
        return layoutRes;
    }

    /**
     * Returns the current extra binding for the given variable id or null if one isn't present.
     */
    public final Object extraBinding(int variableId){
        if(extraBindings == null) {
            return null;
        }
        return extraBindings.get(variableId);
    }

    /**
     * Updates the state of the binding for the given item and position. This is called internally
     * by the binding collection adapters.
     * 根据给定的item类型和position更新Item Binding的layoutRes布局
     */
    public void updateItemLayoutRes(int position, T item){
        if(onItemBind != null) {
            //多类型 布局
            variableId = VAR_INVALID;
            layoutRes = LAYOUT_NONE;
            //OnItemBindClass 进入 绑定的多布局类型 寻找对应position的OnItemBind 更新layoutRes
            onItemBind.onItemBind(this, position, item);
            if(variableId == VAR_INVALID) {
                throw new IllegalStateException("variableId not set in updateItemLayoutRes()");
            }
            if(layoutRes == LAYOUT_NONE) {
                throw new IllegalStateException("layoutRes not set in updateItemLayoutRes()");
            }
        }
    }

    /**
     * 绑定数据设置参数
     * Binds the item and extra bindings to the given binding. Returns true if anything was bound
     * and false otherwise. This is called internally by the binding collection adapters.
     *
     * @throws IllegalStateException
     *         if the variable id isn't present in the layout.
     */
    public boolean bind(ViewDataBinding binding, T item){
        if(variableId == VAR_NONE) {
            return false;
        }
        boolean result = binding.setVariable(variableId, item);
        if(!result) {
            Utils.throwMissingVariable(binding, variableId, layoutRes);
        }
        if(item instanceof ExtrasBindViewModel) {
            //绑定 item 对应的 额外监听
            bindExtras(( (ExtrasBindViewModel)item ).extraBindings, binding);
        }else {
            bindExtras(extraBindings, binding);
        }
        return true;
    }

    private void bindExtras(SparseArray<WeakReference<Object>> extraBindings, ViewDataBinding binding){
        if(extraBindings != null) {
            for(int i = 0, size = extraBindings.size(); i<size; i++) {
                int variableId = extraBindings.keyAt(i);
                WeakReference<Object> objectWeakReference = extraBindings.valueAt(i);
                if(variableId != VAR_NONE && objectWeakReference != null) {
                    binding.setVariable(variableId, objectWeakReference.get());
                }
            }
        }
    }
}
