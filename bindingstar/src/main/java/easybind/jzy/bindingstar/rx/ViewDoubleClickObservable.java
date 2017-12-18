package easybind.jzy.bindingstar.rx;

import android.os.Looper;

import com.jakewharton.rxbinding2.internal.Notification;

import easybind.jzy.bindingstar.helper.interf.DoubleClickAble;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

import static me.tatarka.bindingcollectionadapter2.Utils.LOG;

/**
 * @another 江祖赟
 * @date 2017/10/20 0020.
 */
public class ViewDoubleClickObservable extends Observable<Object> {
    private final DoubleClickAble view;

    public ViewDoubleClickObservable(DoubleClickAble view){
        this.view = view;
    }

    @Override
    protected void subscribeActual(Observer<? super Object> observer){
        if(!checkMainThread(observer)) {
            return;
        }
        DoubleListener doubleListener = new DoubleListener(view,observer);
        observer.onSubscribe(doubleListener);
        view.setOnDoubleClickListener(doubleListener);
    }

    public static boolean checkMainThread(Observer<?> observer) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            LOG("Expected to be called on the main thread but was " + Thread.currentThread().getName());
            observer.onComplete();
            return false;
        }
        return true;
    }

    static final class DoubleListener extends MainThreadDisposable implements DoubleClickAble.OnDoubleClickListener {
        private final DoubleClickAble view;
        private final Observer<? super Object> observer;

        DoubleListener(DoubleClickAble view, Observer<? super Object> observer){
            this.view = view;
            this.observer = observer;
        }

        @Override
        protected void onDispose(){
            view.setOnDoubleClickListener(null);
        }

        @Override
        public void onDoubleClicked(DoubleClickAble view){
            if(!isDisposed()) {
                observer.onNext(Notification.INSTANCE);
            }
        }
    }
}
