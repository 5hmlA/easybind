package me.tatarka.bindingcollectionadapter2.collections;

import android.databinding.ObservableList;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.tatarka.bindingcollectionadapter2.Utils.LOG;

/**
 * An {@link ObservableList} that presents multiple lists and mDataLists as one contiguous source.
 * Changes to any of the given lists will be reflected here. You cannot modify {@code
 * MergeObservableList} itself other than adding and removing backing lists or mDataLists with {@link
 * #insertItem(Object)} and {@link #insertList(JObservableList)} respectively.  This is a good case
 * where you have multiple data sources, or a handful of fixed mDataLists mixed in with lists of data.
 */
public class MergeObservableList<T> extends JObservableList<T> implements JObservableList.JOnListChangedCallback {

    private static final String TAG = MergeObservableList.class.getSimpleName();
    private final ArrayList<List<? extends T>> lists = new ArrayList<>();

    /**
     * Inserts the given item into the merge list.
     */
    public MergeObservableList<T> insertItem(int index, T object){
        lists.add(index, Collections.singletonList(object));
        modCount += 1;
        for(JOnListChangedCallback listener : mListeners) {
            listener.onItemRangeInserted(this, size()-1, 1);
        }
        return this;
    }

    /**
     * Inserts the given item into the merge list.
     */
    public MergeObservableList<T> insertItem(T object){
        lists.add(Collections.singletonList(object));
        modCount += 1;
        for(JOnListChangedCallback listener : mListeners) {
            listener.onItemRangeInserted(this, size()-1, 1);
        }
        return this;
    }

    /**
     * Inserts the given {@link ObservableList} into the merge list. Any changes in the given list
     * will be reflected and propagated here.
     */
    @SuppressWarnings("unchecked")
    public MergeObservableList<T> insertList(@NonNull JObservableList<? extends T> list){
        list.addOnListChangedCallback2(this);
        int oldSize = size();
        lists.add(list);
        modCount += 1;
        if(!list.isEmpty()) {
            for(JOnListChangedCallback listener : mListeners) {
                listener.onItemRangeInserted(this, oldSize, list.size());
            }
        }
        return this;
    }

    public MergeObservableList<T> insertList(int index, @NonNull JObservableList<? extends T> list){
        list.addOnListChangedCallback2(this);
        int oldSize = 0;
        if(index<lists.size()) {
            for(int i = 0; i<index; i++) {
                oldSize += lists.get(i).size();
            }
        }
        lists.add(index, list);
        modCount += 1;
        if(!list.isEmpty()) {
            for(JOnListChangedCallback listener : mListeners) {
                listener.onItemRangeInserted(this, oldSize, list.size());
            }
        }
        return this;
    }

    /**
     * Removes the given item from the merge list.
     * 移除 通过 insertItem添加的数据
     */
    public boolean removeItem(T object){
        int size = 0;
        for(int i = 0, listsSize = lists.size(); i<listsSize; i++) {
            List<? extends T> list = lists.get(i);
            if(!( list instanceof ObservableList )) {
                Object item = list.get(0);
                if(( object == null ) ? ( item == null ) : object.equals(item)) {
                    lists.remove(i);
                    modCount += 1;
                    for(JOnListChangedCallback listener : mListeners) {
                        listener.onItemRangeRemoved(this, size, 1);
                    }
                    return true;
                }
            }
            size += list.size();
        }
        return false;
    }

    /**
     * Removes the given {@link JObservableList} from the merge list.
     */
    @SuppressWarnings("unchecked")
    public boolean removeList(JObservableList<? extends T> listToRemove){
        int size = 0;
        for(int i = 0, listsSize = lists.size(); i<listsSize; i++) {
            List<? extends T> list = lists.get(i);
            if(list == listToRemove) {
                listToRemove.removeOnListChangedCallback2(this);
                lists.remove(i);
                modCount += 1;
                for(JOnListChangedCallback listener : mListeners) {
                    listener.onItemRangeRemoved(this, size, list.size());
                }
                return true;
            }
            size += list.size();
        }
        return false;
    }

    /**
     * Removes all mDataLists and lists from the merge list.
     */
    public void removeAll(){
        int size = size();
        if(size == 0) {
            return;
        }
        for(int i = 0, listSize = lists.size(); i<listSize; i++) {
            List<? extends T> list = lists.get(i);
            if(list instanceof JObservableList) {
                ( (JObservableList)list ).removeOnListChangedCallback2(this);
            }
        }
        lists.clear();
        modCount += 1;
        for(JOnListChangedCallback listener : mListeners) {
            listener.onClear(this, size);
        }
    }

    /**
     * Converts an index into this merge list into an into an index of the given backing list.
     *
     * @throws IndexOutOfBoundsException
     *         for an invalid index.
     * @throws IllegalArgumentException
     *         if the given list is not backing this merge list.
     */
    public int mergeToBackingIndex(ObservableList<? extends T> backingList, int index){
        if(index<0) {
            throw new IndexOutOfBoundsException();
        }
        int size = 0;
        for(int i = 0, listsSize = lists.size(); i<listsSize; i++) {
            List<? extends T> list = lists.get(i);
            if(backingList == list) {
                if(index<list.size()) {
                    return size+index;
                }else {
                    throw new IndexOutOfBoundsException();
                }
            }
            size += list.size();
        }
        throw new IllegalArgumentException();
    }

    /**
     * Converts an index into a backing list into an index into this merge list.
     *
     * @throws IndexOutOfBoundsException
     *         for an invalid index.
     * @throws IllegalArgumentException
     *         if the given list is not backing this merge list.
     */
    public int backingIndexToMerge(ObservableList<? extends T> backingList, int index){
        if(index<0) {
            throw new IndexOutOfBoundsException();
        }
        int size = 0;
        for(int i = 0, listsSize = lists.size(); i<listsSize; i++) {
            List<? extends T> list = lists.get(i);
            if(backingList == list) {
                if(index-size<list.size()) {
                    return index-size;
                }else {
                    throw new IndexOutOfBoundsException();
                }
            }
            size += list.size();
        }
        throw new IllegalArgumentException();
    }

    @Override
    public T get(int location){
        if(location<0) {
            throw new IndexOutOfBoundsException();
        }
        int size = 0;
        for(int i = 0, listsSize = lists.size(); i<listsSize; i++) {
            List<? extends T> list = lists.get(i);
            if(location-size<list.size()) {
                return list.get(location-size);
            }
            size += list.size();
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int size(){
        int size = 0;
        for(int i = 0, listsSize = lists.size(); i<listsSize; i++) {
            List<? extends T> list = lists.get(i);
            size += list.size();
        }
        return size;
    }

    @Override
    public void onChanged(JObservableList ts, int fromPosition, int count, Object payload){
        modCount += 1;
        int size = 0;
        for(int i = 0, listsSize = lists.size(); i<listsSize; i++) {
            List list = lists.get(i);
            if(list == ts) {
                LOG(TAG,size," =======数据变化了======= ",fromPosition,count);
                for(JOnListChangedCallback listener : mListeners) {
                    listener.onChanged(this, size+fromPosition, count, payload);
                }
                return;
            }
            size += list.size();
        }
    }


    @Override
    public void onItemRangeInserted(JObservableList ts, int fromPosition, int count){
        modCount += 1;
        int size = 0;
        for(int i = 0, listsSize = lists.size(); i<listsSize; i++) {
            List list = lists.get(i);
            if(list == ts) {
                LOG(TAG,size," =======插入数据了======= ",fromPosition,count);
                for(JOnListChangedCallback listener : mListeners) {
                    listener.onItemRangeInserted(this, size+fromPosition, count);
                }
                return;
            }
            size += list.size();
        }

    }

    @Override
    public void onItemRangeRemoved(JObservableList ts, int fromPosition, int count){
        modCount += 1;
        int size = 0;
        for(int i = 0, listsSize = lists.size(); i<listsSize; i++) {
            List list = lists.get(i);
            if(list == ts) {
                LOG(TAG,size," =======删除数据了======= ",fromPosition,count);
                for(JOnListChangedCallback listener : mListeners) {
                    listener.onItemRangeRemoved(this, size+fromPosition, count);
                }
                return;
            }
            size += list.size();
        }
    }

    @Override
    public void onMoved(JObservableList ts, int fromPosition, int toPosition){
        int size = 0;
        for(int i = 0, listsSize = lists.size(); i<listsSize; i++) {
            List list = lists.get(i);
            if(list == ts) {
                LOG(TAG,size," =======移动数据了======= ",fromPosition,toPosition);
                for(JOnListChangedCallback listener : mListeners) {
                    listener.onMoved(this, size+fromPosition, size+toPosition);
                }
                return;
            }
            size += list.size();
        }
    }

    @Override
    public void onClear(JObservableList ts, int oldSize){
        modCount += 1;
        int size = 0;
        for(int i = 0, listsSize = lists.size(); i<listsSize; i++) {
            List list = lists.get(i);
            if(list == ts) {
                LOG(TAG,size," =======清空数据了======= ");
                for(JOnListChangedCallback listener : mListeners) {
                    listener.onItemRangeRemoved(this, size, oldSize);
                }
                return;
            }
            size += list.size();
        }
    }
}
