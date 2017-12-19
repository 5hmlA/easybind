package jzy.easybindpagelist.statehelper;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * @another 江祖赟
 * @date 2017/12/16.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({PageDiffState.PAGE_STATE_EMPTY, PageDiffState.PAGE_STATE_ERROR, PageDiffState.PAGE_STATE_LOADING, PageDiffState.PAGE_STATE_SUCCEESS})
public @interface PageDiffState {
    int PAGE_STATE_EMPTY = 0X00000000;
    int PAGE_STATE_ERROR = 0X00000001;
    int PAGE_STATE_LOADING = 0X00000002;
    int PAGE_STATE_SUCCEESS = 0X00000003;
}
