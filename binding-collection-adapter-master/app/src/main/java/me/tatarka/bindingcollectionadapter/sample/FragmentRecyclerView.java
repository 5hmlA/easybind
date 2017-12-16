package me.tatarka.bindingcollectionadapter.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import me.tatarka.bindingcollectionadapter.sample.databinding.RecyclerViewBinding;

/**
 * Created by evan on 5/31/15.
 */
public class FragmentRecyclerView extends Fragment {
    private static final String TAG = "BindingRecyclerView";
    private RecycleViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        viewModel = new RecycleViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        RecyclerViewBinding binding = RecyclerViewBinding.inflate(inflater, container, false);
//        viewModel.setRecvItemClick(this);
        binding.setRecViewModel(viewModel);
        binding.setListeners(new Listeners(viewModel));
        binding.executePendingBindings();
        return binding.getRoot();
    }

    public void onImgClick(int index){
        Toast.makeText(getContext(), index+"", Toast.LENGTH_SHORT).show();
    }
}
