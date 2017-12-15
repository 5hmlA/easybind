package me.tatarka.bindingcollectionadapter.sample;

/**
 * Created by evan on 6/3/15.
 */
public class Listeners {
    private MyViewModel viewModel;
    private RecycleViewModel recvModel;

    public Listeners(MyViewModel viewModel){
        this.viewModel = viewModel;
    }

    public Listeners(RecycleViewModel viewModel){
        this.recvModel = viewModel;
    }

    public void onAddItem(){
        if(viewModel != null) {
            viewModel.addItem();
        }else {
            recvModel.addItem();
        }
    }

    public void onRemoveItem(){
        if(viewModel != null) {
            viewModel.removeItem();
        }else {
            recvModel.removeItem();
        }
    }
}
