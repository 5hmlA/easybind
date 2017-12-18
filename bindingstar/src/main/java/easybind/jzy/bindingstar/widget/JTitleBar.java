//package easybind.jzy.bindingstar.widget;
//
//import android.content.Context;
//import android.content.res.Resources;
//import android.content.res.TypedArray;
//import android.graphics.Color;
//import android.os.Build;
//import android.support.annotation.Nullable;
//import android.support.v7.widget.Toolbar;
//import android.util.AttributeSet;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//
//import java.util.concurrent.TimeUnit;
//
//import easybind.jzy.bindingstar.helper.interf.DoubleClickAble;
//import easybind.jzy.bindingstar.helper.interf.JSimpleOnGestureListener;
//import easybind.jzy.bindingstar.rx.ViewDoubleClickObservable;
//import io.reactivex.Observable;
//
///**
// * @author 江祖赟.
// * @date 2017/6/7
// * @des [由于fitsSystemWindows属性在fragment中无效所以自定义适配]
// * <p><a href="https://github.com/ZuYun">github</a>
// * ====================================================
// * android:drawableLeft="@drawable/icon_btn_back"
// * android:drawableRight="@drawable/icon_btn_back"
// * android:textColor="@color/colorAccent"
// * android:fitsSystemWindows="true"
// * android:textSize="18dp"
// * android:text="测试"
// * ======================================
// */
//public class JTitleBar extends Toolbar implements DoubleClickAble, GestureDetector.OnDoubleTapListener {
//
//    private static final int[] ATTRS = new int[]{android.R.attr.textSize, android.R.attr.textColor, android.R.attr.fitsSystemWindows};
//    private final boolean mIsfitsSystemWindows;
//    private GestureDetector mGestureDetector;
//    @Nullable private OnClickListener mClickListener;
//    private OnDoubleClickListener mDoubleClickListener;
//    private int mOrignPaddingTop;
//
//    public JTitleBar(Context context){
//        this(context, null);
//    }
//
//    public JTitleBar(Context context, AttributeSet attrs){
//        this(context, attrs, 0);
//    }
//
//    public JTitleBar(Context context, AttributeSet attrs, int defStyleAttr){
//        super(context, attrs, defStyleAttr);
//        setClickable(true);
//        TypedArray sa1 = context.obtainStyledAttributes(attrs, ATTRS);
//        mTextSize = sa1.getDimensionPixelSize(0, dp2px(getContext(), mTextSize));
//        mTextColor = sa1.getColor(1, Color.WHITE);
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
//
//        mGestureDetector = new GestureDetector(getContext(), new JSimpleOnGestureListener());
//        mGestureDetector.setOnDoubleTapListener(this);
//    }
//
//    //    @Override
//    //    protected void onLayout(boolean changed, int l, int t, int r, int b){
//    //        super.onLayout(changed, l, t, r, b);
//    ////        int hpading = Math.max(mJtitlebarLeftButton.getMeasuredWidth()+getPaddingLeft(),
//    ////                getPaddingRight()+mJtitlebarRightButton.getMeasuredWidth());
//    ////        RelativeLayout.LayoutParams layoutParams = (LayoutParams)mJtitlebarTitle.getLayoutParams();
//    ////        layoutParams.leftMargin = hpading;
//    ////        layoutParams.rightMargin = hpading;
//    //    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event){
//        if(mGestureDetector != null) {
//            //GestureDetector导致系统的点击事件失效
//            return mGestureDetector.onTouchEvent(event);
//        }else {
//            return super.onTouchEvent(event);
//        }
//    }
//
////    public void replaceTitleContent(int customTitlelayoutRes){
////        mCustomTitlelayoutRes = customTitlelayoutRes;
////    }
//
////    public void replaceTitleContent(View customTitleView){
////        mCustomTitleView = customTitleView;
////    }
//
////    public RelativeLayout getRealTitleBar(){
////        return mRealTitleBar;
////    }
//
////    public void replaceLeftView(View leftView){
////        mCustomLeftView = leftView;
////    }
//
//    /**
//     * @param rightContent
//     * @return 返回right的textview 可以设置字体大小etc
//     */
////    public TextView replaceLeftAsTextView(String rightContent){
////        TextView textView = new TextView(getContext());
////        textView.setGravity(Gravity.CENTER_VERTICAL);
////        textView.setPadding(dp2pxCeilInt(14), 0, 0, 0);
////        textView.setText(rightContent);
////        return (TextView)( mCustomLeftView = textView );
////    }
//
//    /**
//     * 默认right是图片
//     * 默认布局 参数 -2,-1,RelativeLayout.CENTER_VERTICAL,建议自己设置gravity
//     *
//     * @param rightView
//     */
////    public void replaceRightView(View rightView){
////        mCustomRightView = rightView;
////    }
//
//    /**
//     * @param rightContent
//     * @return 返回right的textview 可以设置字体大小etc
//     */
////    public TextView replaceRightAsTextView(String rightContent){
////        TextView textView = new TextView(getContext());
////        textView.setGravity(Gravity.CENTER_VERTICAL);
////        textView.setPadding(0, 0, dp2pxCeilInt(14), 0);
////        textView.setText(rightContent);
////        return (TextView)( mCustomRightView = textView );
////    }
//
//    /**
//     * @param rightContent
//     * @return 返回right的textview 可以设置字体大小etc
//     */
////    public CheckBox replaceRightAsCheckBox(String rightContent){
////        CheckBox textView = new CheckBox(getContext());
////        textView.setGravity(Gravity.CENTER_VERTICAL);
////        textView.setPadding(0, 0, dp2pxCeilInt(14), 0);
////        textView.setText(rightContent);
////        return (CheckBox)( mCustomRightView = textView );
////    }
//
//    /**
//     * 默认right是图片
//     *
//     * @param rightView
//     */
//    public void replaceRightView(View rightView, LayoutParams custRightlayoutParams){
//    }
//
//    /**
//     * 默认right是图片
//     *
//     * @param leftView
//     */
//    public void replaceLeftView(View leftView, LayoutParams custLeftlayoutParams){
//    }
//
//    @Override
//    public boolean onSingleTapConfirmed(MotionEvent e){
//        if(mClickListener != null) {
//            mClickListener.onClick(this);
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    @Override
//    public boolean onDoubleTap(MotionEvent e){
//        if(mDoubleClickListener != null) {
//            mDoubleClickListener.onDoubleClicked(this);
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    @Override
//    public boolean onDoubleTapEvent(MotionEvent e){
//        return false;
//    }
//
//    @Override
//    public void setOnClickListener(@Nullable OnClickListener l){
//        super.setOnClickListener(l);
//        mGestureDetector = null;
//    }
//
//    /**
//     * 使用GestureDetector的onSingleTapConfirmed作为点击事件
//     *
//     * @param l
//     */
//    public void setOnClickListener2(@Nullable OnClickListener l){
//        mClickListener = l;
//    }
//
//    public void setOnDoubleClickListener(DoubleClickAble.OnDoubleClickListener dl){
//        mDoubleClickListener = dl;
//    }
//
//    @Override
//    protected void onDetachedFromWindow(){
//        super.onDetachedFromWindow();
//        if(null != mGestureDetector) {
//            mGestureDetector.setOnDoubleTapListener(null);
//        }
//    }
//
//    public Observable rxDoubleClick(){
//        return new ViewDoubleClickObservable(this).throttleFirst(1, TimeUnit.SECONDS);
//    }
//
//    public void removeFitSystemWindow(){
//        if(mIsfitsSystemWindows && Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
//            setPadding(getPaddingLeft(), mOrignPaddingTop, getPaddingRight(), getPaddingBottom());
//        }
//    }
//
//    public static int getStatusBarHeight(){
//        Resources system = Resources.getSystem();
//        int resourceId = system.getIdentifier("status_bar_height", "dimen", "android");
//        return system.getDimensionPixelSize(resourceId);
//    }
//}
