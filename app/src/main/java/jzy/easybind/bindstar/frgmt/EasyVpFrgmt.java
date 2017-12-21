package jzy.easybind.bindstar.frgmt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jzy.easybind.bindstar.viewmodel.VpViewModel;
import jzy.easybind.databinding.BindVpAdapterBinding;

/**
 * Created by evan on 5/31/15.
 */
public class EasyVpFrgmt extends Fragment {
    private static final String TAG = "BindingRecyclerView";
    private VpViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        viewModel = new VpViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        BindVpAdapterBinding binding = BindVpAdapterBinding.inflate(inflater, container, false);
        binding.setViewModel(viewModel);
//        binding.executePendingBindings();
        PagerAdapter adapter = binding.pager.getAdapter();
        binding.pager.setOffscreenPageLimit(5);
        binding.tabs.setupWithViewPager(binding.pager);
        return binding.getRoot();
    }

}