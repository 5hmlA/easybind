package me.tatarka.bindingcollectionadapter2;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

/**
 * Helper databinding utilities. May be made public some time in the future if they prove to be
 * useful.
 */
public class Utils {
    private static final String TAG = "EasyBinding";
    public static boolean sInDebug = true;

    /**
     * Helper to throw an exception when {@link android.databinding.ViewDataBinding#setVariable(int, * Object)} returns false.
     */
    public static void throwMissingVariable(ViewDataBinding binding, int bindingVariable, @LayoutRes int layoutRes){
        Context context = binding.getRoot().getContext();
        Resources resources = context.getResources();
        String layoutName = resources.getResourceName(layoutRes);
        String bindingVariableName = DataBindingUtil.convertBrIdToString(bindingVariable);
        throw new IllegalStateException("Could not bind variable '"+bindingVariableName+"' in layout '"+layoutName+"'");
    }

    /**
     * Ensures the call was made on the main thread. This is enforced for all ObservableList change
     * operations.
     */
    public static void ensureChangeOnMainThread(){
        if(Thread.currentThread() != Looper.getMainLooper().getThread()) {
            throw new IllegalStateException("You must only modify the ObservableList on the main thread.");
        }
    }

    /**
     * Constructs a binding adapter class from it's class name using reflection.
     */
    @SuppressWarnings("unchecked")
    public static <T, A extends BindingCollectionAdapter<T>> A createClass(Class<? extends BindingCollectionAdapter> adapterClass, ItemBinding<T> itemBinding){
        try {
            return (A)adapterClass.getConstructor(ItemBinding.class).newInstance(itemBinding);
        }catch(Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    public static void startActivity(View view, Intent intent){

    }

    public static void LOG(Object... msg){
        if(sInDebug) {
            StringBuilder sb = new StringBuilder();
            for(Object o : msg) {
                if(o != null) {
                    sb.append(o.toString()).append("   ");
                }
            }
            Log.d(TAG, sb.toString());
        }
    }
}
