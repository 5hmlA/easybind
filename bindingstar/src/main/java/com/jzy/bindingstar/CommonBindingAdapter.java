package com.jzy.bindingstar;

import android.databinding.BindingAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import com.jzy.bindingstar.pagingviewmodel.loadmorehelper.BaseLoadmoreViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * @another 江祖赟
 * @date 2017/12/16.
 */
public class CommonBindingAdapter {
    /**
     * Reloads the data when the pull-to-refresh is triggered.
     * <p>
     * Creates the {@code android:onRefresh} for a {@link SwipeRefreshLayout}.
     */
    @BindingAdapter("android:onRefresh")
    public static void setSwipeRefreshLayoutOnRefreshListener(final SwipeRefreshLayout view,
                                                              final BaseLoadmoreViewModel viewModel) {
        view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.onRefresh(view);
            }
        });
    }

    @BindingAdapter("editTextSearch")
    public static void setEditTextSearchListener(final EditText editText,
                                                              final BaseLoadmoreViewModel viewModel) {
        RxTextView.afterTextChangeEvents(editText).debounce(500, TimeUnit.MICROSECONDS).subscribe(new Consumer<TextViewAfterTextChangeEvent>() {
            @Override
            public void accept(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) throws Exception{
                viewModel.search(editText,textViewAfterTextChangeEvent.toString());
            }
        });
    }
}
