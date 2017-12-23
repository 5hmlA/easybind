package jzy.easybind.bindstar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * @des [由于fitsSystemWindows属性在fragment中无效所以自定义适配]
 * <p><a href="https://github.com/ZuYun">github</a>
 * ====================================================
 * android:drawableLeft="@drawable/icon_btn_back"
 * android:drawableRight="@drawable/icon_btn_back"
 * android:textColor="@color/colorAccent"
 * android:fitsSystemWindows="true"
 * android:textSize="18dp"
 * android:text="测试"
 * ======================================
 */
public class JToolbar2 extends Toolbar {

    private static final int[] ATTRS = new int[]{android.R.attr.textSize, android.R.attr.textColor, android.R.attr.fitsSystemWindows};
//    private final boolean mIsfitsSystemWindows;
    private int mOrignPaddingTop;
    private boolean mIsTitleCenter = false;
    private CharSequence mTitleText;
    private AppCompatTextView mTitleTextView;
    private int mTitleTextColor;

    public JToolbar2(Context context){
        this(context, null);
    }

    public JToolbar2(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public JToolbar2(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        setClickable(true);
//        TypedArray sa1 = context.obtainStyledAttributes(attrs, ATTRS);
//        mIsfitsSystemWindows = sa1.getBoolean(2, true);
//        sa1.recycle();
//        //如果再activity里面设置fitsSystemWindows 会出问题 布局会自己加上pading 如果在fragment里面正常
//        if(mIsfitsSystemWindows && Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
//            //            View fitSystem = findViewById(com.blueprint.R.id.jtitlebar_pading_status);
//            //            ViewGroup.LayoutParams layoutParams = fitSystem.getLayoutParams();
//            //            layoutParams.height = getStatusBarHeight();
//            //fix：activity里面设置fitsSystemWindows
//            mOrignPaddingTop = getPaddingTop();
//            setPadding(getPaddingLeft(), mOrignPaddingTop+getStatusBarHeight(), getPaddingRight(), getPaddingBottom());
//        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b){
        super.onLayout(changed, l, t, r, b);
        //        int hpading = Math.max(mJtitlebarLeftButton.getMeasuredWidth()+getPaddingLeft(),
        //                getPaddingRight()+mJtitlebarRightButton.getMeasuredWidth());
        //        RelativeLayout.LayoutParams layoutParams = (LayoutParams)mJtitlebarTitle.getLayoutParams();
        //        layoutParams.leftMargin = hpading;
        //        layoutParams.rightMargin = hpading;
    }

//    public void removeFitSystemWindow(){
//        if(mIsfitsSystemWindows && Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
//            setPadding(getPaddingLeft(), mOrignPaddingTop, getPaddingRight(), getPaddingBottom());
//        }
//    }

    public static int getStatusBarHeight(){
        Resources system = Resources.getSystem();
        int resourceId = system.getIdentifier("status_bar_height", "dimen", "android");
        return system.getDimensionPixelSize(resourceId);
    }

    @Override
    public void setTitle(CharSequence title){
        if(mIsTitleCenter) {
            if(!TextUtils.isEmpty(title)) {
                if(mTitleTextView == null) {
                    final Context context = getContext();
                    mTitleTextView = new AppCompatTextView(context);
                    mTitleTextView.setBackgroundColor(Color.GRAY);
                    mTitleTextView.setSingleLine();
                    mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
                    if(mTitleTextColor != 0) {
                        mTitleTextView.setTextColor(mTitleTextColor);
                    }
                }
                if(mTitleTextView.getParent()!= this) {
                    addTitleView(mTitleTextView);
                }
            }else if(mTitleTextView != null && mTitleTextView.getParent() == this) {
                removeView(mTitleTextView);
            }
            if(mTitleTextView != null) {
                mTitleTextView.setText(title);
            }
            mTitleText = title;
        }else {
            super.setTitle(title);
        }
    }

    private void addTitleView(View v){
        final ViewGroup.LayoutParams vlp = v.getLayoutParams();
        final LayoutParams lp;
        if(vlp == null) {
            lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        }else if(!checkLayoutParams(vlp)) {
            lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        }else {
            lp = (LayoutParams)vlp;
        }
        addView(v, lp);
    }

    public void setTitleTextColor(@ColorInt int color){
        if(mIsTitleCenter) {
            mTitleTextColor = color;
            if(mTitleTextView != null) {
                mTitleTextView.setTextColor(color);
            }
        }else {
            super.setTitleTextColor(color);
        }

    }

    @Override
    public CharSequence getTitle(){
        if(mIsTitleCenter) {
            return mTitleText;
        }else {
            return super.getTitle();
        }
    }

        @Override
        protected LayoutParams generateDefaultLayoutParams() {
            return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        }

    @Override
    public void setSubtitle(CharSequence subtitle){
        if(!mIsTitleCenter) {
            super.setSubtitle(subtitle);
        }else {
        }
    }


    @Override
    public void setLogo(Drawable drawable){
        if(!mIsTitleCenter) {
            super.setLogo(drawable);
        }else {
        }
    }
}
