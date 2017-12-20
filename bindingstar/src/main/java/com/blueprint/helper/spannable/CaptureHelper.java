package com.blueprint.helper.spannable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.ScrollView;

/**
 * @another 江祖赟
 * @date 2017/11/8 0008.
 */
public class CaptureHelper {
    /**
     * 对WebView进行截屏，虽然使用过期方法，但在当前Android版本中测试可行
     *
     * @param webView
     * @return
     */
    public static Bitmap captureWebViewKitKat(WebView webView){
        Picture picture = webView.capturePicture();
        int width = picture.getWidth();
        int height = picture.getHeight();
        if(width>0 && height>0) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            picture.draw(canvas);
            return bitmap;
        }
        return null;
    }

    /**
     * 在Android5.0及以上版本，Android对WebView进行了优化，为了减少内存使用和提高性能，使用WebView加载网页时只绘制显示部分。
     * 如果我们不做处理，仍然使用上述代码截图的话，就会出现只截到屏幕内显示的WebView内容，其它部分是空白的情况。
     * 这时候，我们通过调用WebView.enableSlowWholeDocumentDraw()方法可以关闭这种优化，
     * 但要注意的是，该方法需要在WebView实例被创建前就要调用，否则没有效果。所以我们在WebView实例被创建前加入代码：
     * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
     * android.webkit.WebView.enableSlowWholeDocumentDraw();
     * }
     * 作者：贝聊科技
     * 链接：https://juejin.im/post/5a016e8d518825295f5d57a4
     * 来源：掘金
     * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     *
     * @param webView
     * @return
     */
    public static Bitmap captureWebViewLollipop(WebView webView){
        float scale = webView.getScale();
        int width = webView.getWidth();
        int height = (int)( webView.getContentHeight()*scale+0.5 );
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        webView.draw(canvas);
        return bitmap;
    }

    public static Bitmap CaptureView(View view, int bitmapWidth, int bitmapHeight){
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }

    public static Bitmap ViewToBitmap(View view){
        if(null == view) {
            throw new IllegalArgumentException("parameter can't be null.");
        }
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * ScrollView 截图
     *
     * @param scrollView
     * @return
     */
    public static Bitmap captureScrollView(ScrollView scrollView){
        int h = 0;
        Bitmap bitmap = null;
        for(int i = 0; i<scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            scrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
        }
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    /**
     * 将view转成bitmap
     * 截图 /截屏
     *
     * @param view
     * @return
     */
    public static Bitmap CaptureView(View view){
        if(view == null) {
            return null;
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        //        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 这个方法也非常重要，设置布局的尺寸和位置
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        // 生成bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        // 利用bitmap生成画布
        Canvas canvas = new Canvas(bitmap);
        // 把view中的内容绘制在画布上
        view.draw(canvas);
        return bitmap;
    }

    /**
     * ScrollView截图
     *
     * @param view
     * @return
     */
    public static Bitmap shotRecyclerView(RecyclerView view){
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if(adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int)( Runtime.getRuntime().maxMemory()/1024 );

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory/8;
            LruCache<String,Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for(int i = 0; i<size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if(drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }
                height += holder.itemView.getMeasuredHeight();
            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            Drawable lBackground = view.getBackground();
            if(lBackground instanceof ColorDrawable) {
                ColorDrawable lColorDrawable = (ColorDrawable)lBackground;
                int lColor = lColorDrawable.getColor();
                bigCanvas.drawColor(lColor);
            }

            for(int i = 0; i<size; i++) {
                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }
        }
        return bigBitmap;
    }

    //    public static void screenShotRecycleView(final RecyclerView mRecyclerView, final RecycleViewRecCallback callBack) {
    //        if (mRecyclerView == null) {
    //            return;
    //        }
    //        BaseListFragment.MyAdapter adapter = (BaseListFragment.MyAdapter) mRecyclerView.getAdapter();
    //        final Paint paint = new Paint();
    //        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    //        // Use 1/8th of the available memory for this memory cache.
    //        final int cacheSize = maxMemory / 8;
    //        LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
    //        final int oneScreenHeight = mRecyclerView.getMeasuredHeight();
    //        int shotHeight = 0;
    //        if (adapter != null && adapter.getData().size() > 0) {
    //            int headerSize = adapter.getHeaderLayoutCount();
    //            int dataSize = adapter.getData().size();
    //            for (int i = 0; i < headerSize + dataSize; i++) {
    //                BaseViewHolder holder = (BaseViewHolder) adapter.createViewHolder(mRecyclerView, adapter.getItemViewType(i));
    //                if (i >= headerSize)
    //                    adapter.startConvert(holder, adapter.getData().get(i - headerSize));
    //                holder.itemView.measure(
    //                        View.MeasureSpec.makeMeasureSpec(mRecyclerView.getWidth(), View.MeasureSpec.EXACTLY),
    //                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    //                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
    //                holder.itemView.setDrawingCacheEnabled(true);
    //                holder.itemView.buildDrawingCache();
    //                Bitmap drawingCache = holder.itemView.getDrawingCache();
    //                //holder.itemView.destroyDrawingCache();//释放缓存占用的资源
    //                if (drawingCache != null) {
    //                    bitmaCache.put(String.valueOf(i), drawingCache);
    //                }
    //                shotHeight += holder.itemView.getHeight();
    //                if (shotHeight > 12000) {
    //                    //设置截图最大值
    //                    if (callBack != null)
    //                        callBack.onRecFinished(null);
    //                    return;
    //                }
    //            }
    //            //添加底部高度(加载更多或loading布局高度,此处为固定值:)
    //            final int footHight = Util.dip2px(mRecyclerView.getContext(), 42);
    //            shotHeight += footHight;
    //
    //            //返回到顶部
    //            while (mRecyclerView.canScrollVertically(-1)) {
    //                mRecyclerView.scrollBy(0, -oneScreenHeight);
    //            }
    //
    //            //绘制截图的背景
    //            final Bitmap bigBitmap = Bitmap.createBitmap(mRecyclerView.getMeasuredWidth(), shotHeight, Bitmap.Config.ARGB_8888);
    //            final Canvas bigCanvas = new Canvas(bigBitmap);
    //            Drawable lBackground = mRecyclerView.getBackground();
    //            if (lBackground instanceof ColorDrawable) {
    //                ColorDrawable lColorDrawable = (ColorDrawable) lBackground;
    //                int lColor = lColorDrawable.getColor();
    //                bigCanvas.drawColor(lColor);
    //            }
    //
    //
    //            final int[] drawOffset = {0};
    //            final Canvas canvas = new Canvas();
    //            if (shotHeight <= oneScreenHeight) {
    //                //仅有一页
    //                Bitmap bitmap = Bitmap.createBitmap(mRecyclerView.getWidth(), mRecyclerView.getHeight(), Bitmap.Config.ARGB_8888);
    //                canvas.setBitmap(bitmap);
    //                mRecyclerView.draw(canvas);
    //                if (callBack != null)
    //                    callBack.onRecFinished(bitmap);
    //            } else {
    //                //超过一页
    //                final int finalShotHeight = shotHeight;
    //                mRecyclerView.postDelayed(new Runnable() {
    //                    @Override
    //                    public void run() {
    //                        if ((drawOffset[0] + oneScreenHeight < finalShotHeight)) {
    //                            //超过一屏
    //                            Bitmap bitmap = Bitmap.createBitmap(mRecyclerView.getWidth(), mRecyclerView.getHeight(), Bitmap.Config.ARGB_8888);
    //                            canvas.setBitmap(bitmap);
    //                            mRecyclerView.draw(canvas);
    //                            bigCanvas.drawBitmap(bitmap, 0, drawOffset[0], paint);
    //                            drawOffset[0] += oneScreenHeight;
    //                            mRecyclerView.scrollBy(0, oneScreenHeight);
    //                            try {
    //                                bitmap.recycle();
    //                            } catch (Exception ex) {
    //                                ex.printStackTrace();
    //                            }
    //                            mRecyclerView.postDelayed(this, 10);
    //                        } else {
    //                            //不足一屏时的处理
    //                            int leftHeight = finalShotHeight - drawOffset[0] - footHight;
    //                            mRecyclerView.scrollBy(0, leftHeight);
    //                            int top = oneScreenHeight - (finalShotHeight - drawOffset[0]);
    //                            if (top > 0 && leftHeight > 0) {
    //                                Bitmap bitmap = Bitmap.createBitmap(mRecyclerView.getWidth(), mRecyclerView.getHeight(), Bitmap.Config.ARGB_8888);
    //                                canvas.setBitmap(bitmap);
    //                                mRecyclerView.draw(canvas);
    //                                //截图,只要补足的那块图
    //                                bitmap = Bitmap.createBitmap(bitmap, 0, top, bitmap.getWidth(), leftHeight, null, false);
    //                                bigCanvas.drawBitmap(bitmap, 0, drawOffset[0], paint);
    //                                try {
    //                                    bitmap.recycle();
    //                                } catch (Exception ex) {
    //                                    ex.printStackTrace();
    //                                }
    //                            }
    //                            if (callBack != null)
    //                                callBack.onRecFinished(bigBitmap);
    //                        }
    //                    }
    //                }, 10);
    //            }
    //        }
    //    }

    /**
     * 将四张图拼成一张
     *
     * @param pic1 图一
     * @param pic2 图二
     * @param pic3 图三
     * @param pic4 图四
     * @return only_bitmap
     * 详情见说明：{@link com.bertadata.qxb.util.ScreenShotUtils}
     */
//    public static Bitmap combineBitmapsIntoOnlyOne(Bitmap pic1, Bitmap pic2, Bitmap pic3, Bitmap pic4, Activity context) {
//        int w_total = pic2.getWidth();
//        int h_total = pic1.getHeight() + pic2.getHeight() + pic3.getHeight() + pic4.getHeight();
//        int h_pic1 = pic1.getHeight();
//        int h_pic4 = pic4.getHeight();
//        int h_pic12 = pic1.getHeight() + pic2.getHeight();
//        //此处为防止OOM需要对高度做限制
//        if (h_total > HEIGHTLIMIT) {
//            return null;
//        }
//
//        Bitmap only_bitmap = Bitmap.createBitmap(w_total, h_total, Bitmap.Config.ARGB_4444);
//        Canvas canvas = new Canvas(only_bitmap);
//        canvas.drawColor(ContextCompat.getColor(context, R.color.color_content_bg));
//        canvas.drawBitmap(pic1, 0, 0, null);
//        canvas.drawBitmap(pic2, 0, h_pic1, null);
//        canvas.drawBitmap(pic3, 0, h_pic12, null);
//        canvas.drawBitmap(pic4, 0, h_total - h_pic4, null);
//        return only_bitmap;
//    }

//    /**
//     * 将传入的Bitmap合理压缩后输出到系统截屏目录下
//     * 命名格式为：Screenshot+时间戳+启信宝报名.jpg
//     * 同时通知系统重新扫描系统文件
//     *
//     * @param pic1    图一 标题栏截图
//     * @param pic2    图二 scrollview截图
//     * @param context 用于通知重新扫描文件系统，为提升性能可去掉
//     *                详情见说明：{@link com.bertadata.qxb.util.ScreenShotUtils}
//     */
//    public static void savingBitmapIntoFile(final Bitmap pic1, final Bitmap pic2, final Activity context, final BitmapAndFileCallBack callBack) {
//        if (context == null || context.isFinishing()) {
//            return;
//        }
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String fileReturnPath = "";
//                int w = pic1.getWidth();
//                Bitmap bottom = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_picture_combine_bottom);
//                Bitmap top_banner = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_picture_combine_top);
//
//                Bitmap bitmap_bottom = anyRatioCompressing(bottom, (float) w / bottom.getWidth(), (float) w / bottom.getWidth());
//                Bitmap bitmap_top = anyRatioCompressing(top_banner, (float) w / bottom.getWidth(), (float) w / bottom.getWidth());
//                final Bitmap only_bitmap = combineBitmapsIntoOnlyOne(bitmap_top, pic1, pic2, bitmap_bottom, context);
//
//                // 获取当前时间
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-ms", Locale.getDefault());
//                String data = sdf.format(new Date());
//
//                // 获取内存路径
//                // 设置图片路径+命名规范
//                // 声明输出文件
//                String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//                String fileTitle = "Screenshot_" + data + "_com.bertadata.qxb.biz_info.jpg";
//                String filePath = storagePath + "/DCIM/";
//                final String fileAbsolutePath = filePath + fileTitle;
//                File file = new File(fileAbsolutePath);
//
//                /**
//                 * 质压与比压结合
//                 * 分级压缩
//                 * 输出文件
//                 */
//                if (only_bitmap != null) {
//                    try {
//                        // 首先，对原图进行一步质量压缩,形成初步文件
//                        FileOutputStream fos = new FileOutputStream(file);
//                        only_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
//
//                        // 另建一个文件other_file预备输出
//                        String other_fileTitle = "Screenshot_" + data + "_com.bertadata.qxb.jpg";
//                        String other_fileAbsolutePath = filePath + other_fileTitle;
//                        File other_file = new File(other_fileAbsolutePath);
//                        FileOutputStream other_fos = new FileOutputStream(other_file);
//
//                        // 其次，要判断质压之后的文件大小，按文件大小分级进行处理
//                        long file_size = file.length() / 1024; // size of file(KB)
//                        if (file_size < 0 || !(file.exists())) {
//                            // 零级： 文件判空
//                            throw new NullPointerException();
//                        } else if (file_size > 0 && file_size <= 256) {
//                            // 一级： 直接输出
//                            deleteFile(other_file);
//                            // 通知刷新文件系统，显示最新截取的图文件
//                            fileReturnPath = fileAbsolutePath;
//                            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileAbsolutePath)));
//                        } else if (file_size > 256 && file_size <= 768) {
//                            // 二级： 简单压缩:压缩为原比例的3/4，质压为50%
//                            anyRatioCompressing(only_bitmap, (float) 3 / 4, (float) 3 / 4).compress(Bitmap.CompressFormat.JPEG, 40, other_fos);
//                            deleteFile(file);
//                            // 通知刷新文件系统，显示最新截取的图文件
//                            fileReturnPath = other_fileAbsolutePath;
//                            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + other_fileAbsolutePath)));
//                        } else if (file_size > 768 && file_size <= 1280) {
//                            // 三级： 中度压缩：压缩为原比例的1/2，质压为40%
//                            anyRatioCompressing(only_bitmap, (float) 1 / 2, (float) 1 / 2).compress(Bitmap.CompressFormat.JPEG, 40, other_fos);
//                            deleteFile(file);
//                            // 通知刷新文件系统，显示最新截取的图文件
//                            fileReturnPath = other_fileAbsolutePath;
//                            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + other_fileAbsolutePath)));
//                        } else if (file_size > 1280 && file_size <= 2048) {
//                            // 四级： 大幅压缩：压缩为原比例的1/3，质压为40%
//                            anyRatioCompressing(only_bitmap, (float) 1 / 3, (float) 1 / 3).compress(Bitmap.CompressFormat.JPEG, 40, other_fos);
//                            deleteFile(file);
//                            // 通知刷新文件系统，显示最新截取的图文件
//                            fileReturnPath = other_fileAbsolutePath;
//                            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + other_fileAbsolutePath)));
//                        } else if (file_size > 2048) {
//                            // 五级： 中度压缩：压缩为原比例的1/2，质压为40%
//                            anyRatioCompressing(only_bitmap, (float) 1 / 2, (float) 1 / 2).compress(Bitmap.CompressFormat.JPEG, 40, other_fos);
//                            deleteFile(file);
//                            // 通知刷新文件系统，显示最新截取的图文件
//                            fileReturnPath = other_fileAbsolutePath;
//                            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + other_fileAbsolutePath)));
//                        }
//
//                        // 注销fos;
//                        fos.flush();
//                        other_fos.flush();
//                        other_fos.close();
//                        fos.close();
//                        //callback用于回传保存成功的路径以及Bitmap
//                        callBack.onSuccess(only_bitmap, fileReturnPath);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else callBack.onSuccess(null, "");
//            }
//        });
//        thread.start();
//    }
}