package jzy.easybind.bindstar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import jzy.easybind.R;

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
public class JToolbar extends Toolbar {

    private static final int[] ATTRS = new int[]{android.R.attr.gravity, android.R.attr.fitsSystemWindows};
    private int mGravity = Gravity.START;
    private int mSubtitleTextAppearance;
    private int mTitleTextAppearance;
    private boolean mIsTitleCenter = true;
    private CharSequence mTitleText;
    private AppCompatTextView mTitleTextView;
    private AppCompatTextView mRightTextView;
    private int mTitleTextColor;
    private int mTitleOrignLeft;
    private ImageView mRightIconView;
    private int mRightViewRightPadint = dp2px(16);

    public JToolbar(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        setClickable(true);
        //        final TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs, R.styleable.Toolbar, android.support.v7.appcompat.R.attr.toolbarStyle, 0);
        wrapperAttrs(context, attrs);
    }

    public JToolbar(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        setClickable(true);
        wrapperAttrs(context, attrs);
    }

    private void wrapperAttrs(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Toolbar, android.support.v7.appcompat.R.attr.toolbarStyle, 0);
        mTitleTextAppearance = a.getResourceId(android.support.v7.appcompat.R.styleable.Toolbar_titleTextAppearance, 0);
        mSubtitleTextAppearance = a.getResourceId(android.support.v7.appcompat.R.styleable.Toolbar_subtitleTextAppearance, 0);
        a.recycle();
        TypedArray sa1 = context.obtainStyledAttributes(attrs, ATTRS);
        mIsTitleCenter = ( a.getInt(0, Gravity.START)&Gravity.CENTER ) == Gravity.CENTER;
        sa1.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(shouldLayout(mRightTextView)) {
            //mRightTextView的宽度不超过 toolbard的一/3
            measureChild(mRightTextView, MeasureSpec.makeMeasureSpec(getMeasuredWidth()/3, MeasureSpec.AT_MOST), heightMeasureSpec);
        }
        if(shouldLayout(mRightIconView)) {
            measureChild(mRightIconView, MeasureSpec.makeMeasureSpec(getMeasuredWidth()/3, MeasureSpec.AT_MOST), heightMeasureSpec);
        }
        //        measureChild(mRightTextView, -2, heightMeasureSpec);
    }

    private boolean shouldLayout(View view){
        return view != null && view.getParent() == this && view.getVisibility() != GONE;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b){
        super.onLayout(changed, l, t, r, b);
        //layout RightTextView
        int width = 0;
        if(shouldLayout(mRightTextView)) {
            width = mRightTextView.getMeasuredWidth();
            mRightTextView.layout(getWidth()-width, getPaddingTop(), getWidth(), getHeight());
        }
        if(shouldLayout(mRightIconView)) {
            width = mRightIconView.getMeasuredWidth();
            mRightIconView.layout(getWidth()-width, getPaddingTop(), getWidth(), mRightIconView.getMeasuredHeight());
        }
        if(mTitleOrignLeft == 0 && shouldLayout(mTitleTextView)) {
            mTitleOrignLeft = mTitleTextView.getLeft();
            int righMargin = Math.max(mTitleOrignLeft, width);
            ( (Toolbar.LayoutParams)mTitleTextView.getLayoutParams() ).rightMargin = righMargin;
            ( (LayoutParams)mTitleTextView.getLayoutParams() ).leftMargin = Math.max(0, righMargin-mTitleOrignLeft);
        }
    }

    public static int getStatusBarHeight(){
        Resources system = Resources.getSystem();
        int resourceId = system.getIdentifier("status_bar_height", "dimen", "android");
        return system.getDimensionPixelSize(resourceId);
    }

    public static int dp2px(float dipValue){
        float fontScale = Resources.getSystem().getDisplayMetrics().density;
        return (int)( dipValue*fontScale+0.5f );
    }

    public TextView setTitle2(CharSequence title){
        setTitle(title);
        return mTitleTextView;
    }

    @Override
    public void setTitle(CharSequence title){
        if(mIsTitleCenter) {
            if(!TextUtils.isEmpty(title)) {
                if(mTitleTextView == null) {
                    final Context context = getContext();
                    mTitleTextView = new AppCompatTextView(context);
                    mTitleTextView.setSingleLine();
                    mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
                    if(mTitleTextAppearance != 0) {
                        mTitleTextView.setTextAppearance(context, mTitleTextAppearance);
                    }
                    mTitleTextView.setGravity(Gravity.CENTER);
                    if(mTitleTextColor != 0) {
                        mTitleTextView.setTextColor(mTitleTextColor);
                    }
                }
                if(mTitleTextView.getParent() != this) {
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

    public TextView setRightTitle(CharSequence title){
        if(mIsTitleCenter) {
            if(!TextUtils.isEmpty(title)) {
                if(mRightTextView == null) {
                    final Context context = getContext();
                    mRightTextView = new AppCompatTextView(context);
                    mRightTextView.setSingleLine();
                    mRightTextView.setEllipsize(TextUtils.TruncateAt.END);
                    mRightTextView.setPadding(1, 0, mRightViewRightPadint, 0);
                    if(mSubtitleTextAppearance != 0) {
                        mRightTextView.setTextAppearance(context, mSubtitleTextAppearance);
                    }
                    mRightTextView.setGravity(Gravity.CENTER);
                }
                if(mRightTextView.getParent() != this) {
                    addRightView(mRightTextView);
                }
            }else if(mRightTextView != null && mRightTextView.getParent() == this) {
                removeView(mRightTextView);
            }
            if(mRightTextView != null) {
                mRightTextView.setText(title);
                mTitleOrignLeft = 0;
            }
        }
        return mRightTextView;
    }

    public ImageView setRightIcon(@DrawableRes int iconId){
        return setRightIcon(iconId, 0);
    }

    public ImageView setRightIcon(@DrawableRes int iconId, int height){
        if(mIsTitleCenter) {
            if(iconId != 0) {
                if(mRightIconView == null) {
                    final Context context = getContext();
                    mRightIconView = new ImageView(context);
                    mRightIconView.setPadding(1, 0, mRightViewRightPadint, 0);
                }
                if(mRightIconView.getParent() != this) {
                    addRightView(mRightIconView, height);
                }
            }else if(mRightIconView != null && mRightIconView.getParent() == this) {
                removeView(mRightIconView);
            }
            if(mRightIconView != null) {
                mRightIconView.setImageResource(iconId);
                mTitleOrignLeft = 0;
            }
        }
        return mRightIconView;
    }

    private void addTitleView(View v){
        final ViewGroup.LayoutParams vlp = v.getLayoutParams();
        final LayoutParams lp;
        if(vlp == null) {
            lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }else if(!checkLayoutParams(vlp)) {
            lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }else {
            lp = (LayoutParams)vlp;
        }
        addView(v, lp);
    }

    private void addRightView(View v, int height){
        final ViewGroup.LayoutParams vlp = v.getLayoutParams();
        final LayoutParams lp;
        if(vlp == null || !checkLayoutParams(vlp)) {
            if(height>0) {
                lp = new LayoutParams(height+v.getPaddingRight()+v.getPaddingLeft(), LayoutParams.MATCH_PARENT);
                //                lp = new LayoutParams(height+v.getPaddingStart()+v.getPaddingEnd(), LayoutParams.MATCH_PARENT);
            }else {
                lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            }
        }else {
            lp = (LayoutParams)vlp;
        }
        addView(v, lp);
    }

    private void addRightView(View v){
        final ViewGroup.LayoutParams vlp = v.getLayoutParams();
        final LayoutParams lp;
        if(vlp == null) {
            lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        }else if(!checkLayoutParams(vlp)) {
            lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
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
    public void setSubtitle(CharSequence subtitle){
        if(!mIsTitleCenter) {
            super.setSubtitle(subtitle);
        }else {
        }
    }
}
