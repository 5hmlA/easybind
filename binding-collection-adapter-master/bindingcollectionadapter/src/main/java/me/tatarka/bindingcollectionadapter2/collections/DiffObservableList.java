package me.tatarka.bindingcollectionadapter2.collections;

import android.databinding.ObservableList;
import android.support.annotation.MainThread;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;

import java.util.ArrayList;
import java.util.List;

import static me.tatarka.bindingcollectionadapter2.Utils.LOG;

/**
 * An {@link ObservableList} that uses {@link DiffUtil} to calculate and dispatch it's change
 * updates.
 */
public class DiffObservableList<T extends IRecvDataDiff> implements ListUpdateCallback {

    private static final String TAG = DiffObservableList.class.getSimpleName();
    private final Object LIST_LOCK = new Object();
    private JObservableList mOrignList;
    private boolean detectMoves;
    private ArrayList<JObservableList.JOnListChangedCallback> mListChangedCallbacks = new ArrayList<>(0);

    /**
     * Creates a new DiffObservableList of type T.
     * 默认不计算 移动
     */
    public DiffObservableList(JObservableList orignList){
        this(orignList, false);
    }

    /**
     * Creates a new DiffObservableList of type T.
     *
     * @param detectMoves
     *         True if DiffUtil should try to detect moved items, false otherwise.
     */
    public DiffObservableList(JObservableList orignList, boolean detectMoves){
        mOrignList = orignList;
        mListChangedCallbacks = mOrignList.getListChangedCallbacks();
        this.detectMoves = detectMoves;
    }

    private DiffUtil.DiffResult doCalculateDiff(final List<T> oldItems, final List<T> newItems){
        return DiffUtil.calculateDiff(new JSimpleDiffCallback<T>(oldItems, newItems), detectMoves);
    }

    public DiffObservableList set(JObservableList orignList){
        mOrignList = orignList;
        return this;
    }

    /**
     * 是否 需要计算 移动
     * @param detectMoves
     * @return
     */
    public DiffObservableList detectMoves(boolean detectMoves){
        this.detectMoves = detectMoves;
        return this;
    }

    /**
     *
     * @param newItems
     *         The items to set this list to.
     */
    @MainThread
    public void update(List<T> newItems){
        if(newItems instanceof JObservableList) {
            LOG(TAG,"DiffObservableList #update(list) 检测数据差异-->JOnListChangedCallback-->adapter更新");
            DiffUtil.DiffResult diffResult = doCalculateDiff(mOrignList, newItems);
            mOrignList.change(newItems);//使用新数据  adapter跟新的时候 需要使用到
            diffResult.dispatchUpdatesTo(this);
        }else {
            throw new RuntimeException(" list 并非 JObservableList 的子类 不进行差异判断");
        }
    }

    @Override
    public void onInserted(int position, int count){
//        mOrignList.inSerted(mOrignList, position, count);
        //转接出去
        for(JObservableList.JOnListChangedCallback changedCallback : mListChangedCallbacks) {
            changedCallback.onItemRangeInserted(mOrignList,position,count);
        }
    }

    @Override
    public void onRemoved(int position, int count){
//        mOrignList.remove(position, count);
        for(JObservableList.JOnListChangedCallback changedCallback : mListChangedCallbacks) {
            changedCallback.onItemRangeRemoved(mOrignList,position,count);
        }
    }

    @Override
    public void onMoved(int fromPosition, int toPosition){
//        mOrignList.move(fromPosition, toPosition);
        for(JObservableList.JOnListChangedCallback changedCallback : mListChangedCallbacks) {
            changedCallback.onMoved(mOrignList,fromPosition,toPosition);
        }
    }

    @Override
    public void onChanged(int position, int count, Object payload){
        //diff 一次只改一条 无法根据payload改变局部 蛋疼
//        mOrignList.change(position, mOrignList.get(position), payload);
        for(JObservableList.JOnListChangedCallback changedCallback : mListChangedCallbacks) {
            changedCallback.onChanged(mOrignList,position,count,payload);
        }
    }
}
