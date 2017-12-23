package jzy.easybind.bindstar;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import jzy.easybind.Main2Activity;
import jzy.easybind.R;
import jzy.easybind.bindstar.frgmt.EasyRecvFrgmt;
import jzy.easybind.bindstar.frgmt.EasyRecvObFrgmt;
import jzy.easybind.bindstar.frgmt.EasyVpFrgmt;
import jzy.easybind.databinding.BindActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String STATE_TITLE = "title";

    private BindActivityMainBinding binding;
    private ActionBarDrawerToggle toggle;
    public static MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.bind_activity_main);

        setSupportActionBar(binding.toolbar);
        //        binding.toolbar.setLogo(R.mipmap.ic_launcher);
        //        TextView right = binding.toolbar.setRightTitle("确定");
        //        binding.toolbar.setRightIcon(R.drawable.icon_btn_score_yellow_24);
        //        binding.toolbar.setRightIcon(R.drawable.icon_btn_score_yellow_601,90);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //        Transformations.map(mutableLiveData, input->{
        //            return input;
        //        });
        mutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s){
                System.out.println(s+"============EasyRecvObFrgmt========================");
            }
        });
        mutableLiveData.setValue("该改改");
        binding.drawerLayout.setDrawerListener(toggle = new

                ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open_drawer, R.string.close_drawer));

        NavigationView.OnNavigationItemSelectedListener listener = new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem){
                Fragment fragment = null;
                switch(menuItem.getItemId()) {
                    case R.id.action_listview:
                        fragment = new EasyRecvObFrgmt();
                        break;
                    case R.id.action_recyclerview:
                        fragment = new EasyRecvFrgmt();
                        break;
                    case R.id.action_viewpager:
                        fragment = new EasyVpFrgmt();
                        break;
                    case R.id.action_spinner:
                        fragment = new EasyRecvFrgmt();
                        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                        startActivity(intent);
                        break;
                    default:
                        binding.drawerLayout.closeDrawers();
                        return false;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
                getSupportActionBar().setTitle(menuItem.getTitle());
                setTitle("00000");
                binding.drawerLayout.closeDrawers();
                return true;
            }
        };
        binding.navView.setNavigationItemSelectedListener(listener);

        if(savedInstanceState == null)

        {
            listener.onNavigationItemSelected(binding.navView.getMenu().getItem(0));
        }else

        {
            CharSequence title = savedInstanceState.getCharSequence(STATE_TITLE);
            getSupportActionBar().setTitle(title);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return toggle.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putCharSequence(STATE_TITLE, getSupportActionBar().getTitle());
    }
}
