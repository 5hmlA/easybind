package jzy.easybind.bindstar.frgmt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;

import jzy.easybind.bindstar.viewmodel.PageObRecvViewModel;
import jzy.easybind.databinding.BindObPagelistLayoutBinding;

/**
 * Created by evan on 5/31/15.
 */
public class EasyRecvObFrgmt extends Fragment {
    private static final String TAG = "BindingRecyclerView";
    private PageObRecvViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        viewModel = new PageObRecvViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        BindObPagelistLayoutBinding binding = BindObPagelistLayoutBinding.inflate(inflater, container, false);
//        ComPagelistLayoutBinding c = (ComPagelistLayoutBinding)binding.comPagelistLayout;
//        viewModel.setRecvItemClick(this);
        binding.setPagelistViewModel(viewModel);
        viewModel.toSubscribeData(new HashMap());
//        binding.setListeners(new Listeners(viewModel));
        binding.executePendingBindings();
        return binding.getRoot();
    }

    public void onImgClick(int index){
        Toast.makeText(getContext(), index+"", Toast.LENGTH_SHORT).show();
    }
}
