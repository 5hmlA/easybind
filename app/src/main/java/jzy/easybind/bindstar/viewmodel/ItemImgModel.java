package jzy.easybind.bindstar.viewmodel;

import android.databinding.Bindable;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import jzy.easybind.R;

/**
 * Created by evan on 6/14/15.
 */
public class ItemImgModel extends ItemViewModel {
        public static final int layoutRes = R.layout.bind_item_tv_iv;
    @Bindable public int imgRes;
    @Bindable public boolean checked;
    private static final List<Integer> imgs = Arrays.asList(R.mipmap.m2,R.mipmap.m5,R.mipmap.c1,R.mipmap.c4,R.mipmap.timg);



    public ItemImgModel(PageRecvViewModel recvViewModel, String content){
        super(recvViewModel, content);
    }

    public ItemImgModel(String content){
        super(content);
        imgRes = imgs.get(new Random().nextInt(4));
    }

    public void clickShow(View v, int index){
        checked = !checked;
        notifyChange();
        Toast.makeText(v.getContext(), index+"----", Toast.LENGTH_SHORT).show();
    }
}
