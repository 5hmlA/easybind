package me.tatarka.bindingcollectionadapter2.itembindings;

import android.databinding.BaseObservable;
import android.util.SparseArray;

import java.lang.ref.WeakReference;

/**
 * @another 江祖赟
 * @date 2017/12/15.
 * 为每个item绑定一些额外的监听事件
 */
public abstract class ExtrasBindViewModel extends BaseObservable {
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
}
