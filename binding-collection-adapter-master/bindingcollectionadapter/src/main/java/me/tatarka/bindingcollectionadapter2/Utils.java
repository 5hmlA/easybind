package me.tatarka.bindingcollectionadapter2;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Helper databinding utilities. May be made public some time in the future if they prove to be
 * useful.
 */
public class Utils {
    private static final String TAG = "EasyBinding";
    public static boolean sInDebug = true;
    private static Context sContext;

    public static void take(Context context){
        sContext = context;
    }

    /**
     * Helper to throw an exception when {@link android.databinding.ViewDataBinding#setVariable(int, Object Object)} returns false.
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
        Context context = view.getContext();
        while(context instanceof ContextWrapper) {
            if(context instanceof Activity) {
                ( (Activity)context ).startActivity(intent);
                return;
            }
            context = ( (ContextWrapper)context ).getBaseContext();
        }
        Log.e(TAG, "无法从View中跳转activity，没有找到View依附的Activity");
    }


    /**
     * dip转为PX
     */
    public static int dp2px(Context context, float dipValue){
        float fontScale = context.getResources().getDisplayMetrics().density;
        return (int)( dipValue*fontScale+0.5f );
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

    public static Context getContext(){
        return sContext;
    }

    public static SpannableString hightLightStrParser(@NonNull SpannableString orign, String key, int keyColor){
        if(!TextUtils.isEmpty(orign) && !TextUtils.isEmpty(key)) {
            try {
                Pattern p = Pattern.compile(new StringBuilder("[").append(key).append("]").toString());
                Matcher m = p.matcher(orign);
                while(m.find()) {
                    int start = m.start();
                    int end = m.end();
                    if(!TextUtils.isEmpty(m.group())) {
                        orign.setSpan(new ForegroundColorSpan(keyColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }catch(PatternSyntaxException e) {
                LOG(Log.getStackTraceString(e));
            }
        }
        return orign;
    }
}
