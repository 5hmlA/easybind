package me.tatarka.bindingcollectionadapter2.collections;

import android.databinding.ObservableArrayList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @another 江祖赟
 * @date 2017/12/17 0017.
 */
public class JObservableList<T> extends ObservableArrayList<T> {

    public interface JOnListChangedCallback<T> {

        void onChanged(JObservableList<T> ts, int fromPosition, int count, Object payload);

        void onItemRangeInserted(JObservableList<T> ts, int fromPosition, int count);

        void onItemRangeRemoved(JObservableList<T> ts, int fromPosition, int count);

        void onMoved(JObservableList<T> ts, int fromPosition, int toPosition);

        void onClear(JObservableList<T> ts, int oldSize);
    }

    protected ArrayList<JOnListChangedCallback> mListeners = new ArrayList<>();


    public void addOnListChangedCallback2(JOnListChangedCallback listener){
        if(!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public ArrayList<JOnListChangedCallback> getListChangedCallbacks(){
        return mListeners;
    }

    public void removeOnListChangedCallback2(JOnListChangedCallback listener){
        mListeners.remove(listener);
    }

    public T change(int index, T object, Object payload){
        T set = super.set(index, object);
        for(JOnListChangedCallback listener : mListeners) {
            listener.onChanged(this, index, 1, payload);
        }
        return set;
    }

    public void move(int fromPosition, int toPosition){
        T toItem = get(toPosition);
        super.set(toPosition, get(fromPosition));
        super.set(fromPosition, toItem);
        for(JOnListChangedCallback listener : mListeners) {
            listener.onMoved(this, fromPosition, toPosition);
        }
    }

    @Override
    public T remove(int index){
        T remove = super.remove(index);
        for(JOnListChangedCallback listener : mListeners) {
            listener.onItemRangeRemoved(this, index, 1);
        }
        return remove;
    }

    public void remove(int position, int count){
        for(JOnListChangedCallback listener : mListeners) {
            listener.onItemRangeRemoved(this, position, count);
        }
        removeRange(position, position+count);
    }

    @Override
    public boolean add(T object){
        int oldSize = size();
        boolean add = super.add(object);
        if(add) {
            for(JOnListChangedCallback listener : mListeners) {
                listener.onItemRangeInserted(this, oldSize, 1);
            }
        }
        return add;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection){
        int oldSize = size();
        boolean addAll = super.addAll(collection);
        if(addAll) {
            for(JOnListChangedCallback listener : mListeners) {
                listener.onItemRangeInserted(this, oldSize, collection.size());
            }
        }
        return addAll;
    }

    @Override
    public void clear(){
        int oldSize = size();
        super.clear();
        if(oldSize>0) {
            for(JOnListChangedCallback listener : mListeners) {
                listener.onClear(this, oldSize);
            }
        }
    }

    public void change(List<T> newList){
        super.clear();
        super.addAll(newList);
    }
}
