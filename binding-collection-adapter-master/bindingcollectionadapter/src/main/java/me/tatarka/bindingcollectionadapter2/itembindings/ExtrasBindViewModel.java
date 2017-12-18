package me.tatarka.bindingcollectionadapter2.itembindings;

import android.databinding.BaseObservable;
import android.util.SparseArray;

import java.lang.ref.WeakReference;

import me.tatarka.bindingcollectionadapter2.collections.IRecvDataDiff;

/**
 * @another 江祖赟
 * @date 2017/12/15.
 * 为每个item绑定一些额外的监听事件
 */
public abstract  class ExtrasBindViewModel extends BaseObservable implements IRecvDataDiff {

    public SparseArray<WeakReference<Object>> extraBindings = new SparseArray<>();

    /**
     * Bind an extra variable to the view with the given variable id. The same instance will be
     * provided to all views the binding is bound to.
     */
    public final void bindExtra(int variableId, Object value) {
        if (extraBindings == null) {
            extraBindings = new SparseArray<>(1);
        }
        extraBindings.put(variableId, new WeakReference<Object>(value));
    }

    public final void clearExtras() {
        if (extraBindings != null) {
            extraBindings.clear();
        }
    }

    public void removeExtra(int variableId) {
        if (extraBindings != null) {
            extraBindings.remove(variableId);
        }
    }

    @Override
    public int hashCode(){
        return extraBindings != null ? extraBindings.hashCode() : 0;
    }

    @Override
    public boolean areItemsTheSame(IRecvDataDiff oldData, IRecvDataDiff newData){
        return oldData  == newData;
    }

    @Override
    public boolean areContentsTheSame(IRecvDataDiff oldData, IRecvDataDiff newData){
        return true;
    }

    @Override
    public Object getChangePayload(IRecvDataDiff oldData, IRecvDataDiff newData){
        return null;
    }
}
