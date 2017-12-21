package jzy.easybindpagelist;

import android.databinding.BindingAdapter;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import jonas.jlayout.MultiStateLayout;
import jonas.jlayout.OnStateClickListener;
import jzy.easybindpagelist.loadmorehelper.BaseLoadmoreViewModel;

import static jonas.jlayout.MultiStateLayout.LayoutState.STATE_UNMODIFY;

/**
 * @another 江祖赟
 * @date 2017/12/16.
 */
public class CommonBindingAdapter {
    public static final int NON_VALUE = -1;
    /**
     * Reloads the data when the pull-to-refresh is triggered.
     * Creates the {@code android:onRefresh} for a {@link SwipeRefreshLayout}.
     */
    @BindingAdapter("android:onRefresh")
    public static void setSwipeRefreshLayoutOnRefreshListener(final SwipeRefreshLayout view, final BaseLoadmoreViewModel viewModel){
        view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(){
                viewModel.onRefresh(view);
            }
        });
    }

    /**
     * 没有必要使用 因为 手动下拉 触发onRefresh之后 isRefreshing值不会自动为true 需要隐藏的时候 设置为false 不会出发notify，因为isRefreshing的值没变
     *
     * @param view
     * @param isRefreshing
     * @param enableRefresh
     */
    @BindingAdapter(value = {"isRefreshing", "enableRefresh"}, requireAll = false)
    public static void configSwipeRefreshLayout(final SwipeRefreshLayout view, boolean isRefreshing, boolean enableRefresh){
        view.setRefreshing(isRefreshing);
        view.setEnabled(enableRefresh);
    }

    //    @BindingAdapter("editTextSearch")
    //    public static void setEditTextSearchListener(final EditText editText, final BaseLoadmoreViewModel viewModel){
    //        RxTextView.afterTextChangeEvents(editText).debounce(500, TimeUnit.MICROSECONDS).subscribe(new Consumer<TextViewAfterTextChangeEvent>() {
    //            @Override
    //            public void accept(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) throws Exception{
    //                viewModel.search(editText, textViewAfterTextChangeEvent.toString());
    //            }
    //        });
    //    }

    @BindingAdapter("swipeRefreshLayout")
    public static void attachToSwipeRefreshLayout(final RecyclerView view, final ScrollChildSwipeRefreshLayout swipeRefreshLayout){
        swipeRefreshLayout.setScrollUpChild(view);
    }

    @BindingAdapter(value = {"showState", "onRetryListener"}, requireAll = false)
    public static void showState(final MultiStateLayout multiStateLayout, int state, OnStateClickListener stateClickListener){
        multiStateLayout.showStateLayout(state);
        multiStateLayout.setOnStateClickListener(stateClickListener);
    }

    @BindingAdapter("layoutChange")
    public static void setLayoutChangeListener(View view, View.OnLayoutChangeListener newValue){
        if(newValue != null) {
            view.addOnLayoutChangeListener(newValue);
        }
    }

    //============== MultiStateLayout ====================
    @BindingAdapter(value = {"registLoading", "registEmpty", "registError", "loadingColor"}, requireAll = false)
    public static void configMultiStateLayout(MultiStateLayout view, @LayoutRes int loadingRes, @LayoutRes int emptyRes, @LayoutRes int errorRes, @ColorInt int loadingColor){
        if(loadingRes != STATE_UNMODIFY) {
            view.registStateLayout(loadingRes, MultiStateLayout.LayoutState.STATE_LOADING);
        }
        if(emptyRes != STATE_UNMODIFY) {
            view.registStateLayout(emptyRes, MultiStateLayout.LayoutState.STATE_EMPTY);
        }
        if(errorRes != STATE_UNMODIFY) {
            view.registStateLayout(errorRes, MultiStateLayout.LayoutState.STATE_ERROR);
        }
        if(loadingColor != STATE_UNMODIFY) {
            view.setLoadingPageBgColor(loadingColor);
        }
    }
}
