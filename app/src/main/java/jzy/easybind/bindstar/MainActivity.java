package jzy.easybind.bindstar;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import jzy.easybind.R;
import jzy.easybind.bindstar.frgmt.EasyRecvFrgmt;
import jzy.easybind.databinding.BindActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String STATE_TITLE = "title";
    
    private BindActivityMainBinding binding;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.bind_activity_main);

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        binding.drawerLayout.setDrawerListener(toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open_drawer, R.string.close_drawer));

        NavigationView.OnNavigationItemSelectedListener listener = new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment fragment = new EasyRecvFrgmt();
                switch (menuItem.getItemId()) {
                    case R.id.action_listview:
//                        fragment = new FragmentListView();
                        break;
                    case R.id.action_recyclerview:
                        fragment = new EasyRecvFrgmt();
                        break;
                    case R.id.action_viewpager:
//                        fragment = new FragmentViewPagerView();
                        break;
                    case R.id.action_spinner:
//                        fragment = new FragmentSpinnerView();
                        break;
                    default:
                        binding.drawerLayout.closeDrawers();
                        return false;
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content, fragment)
                        .commit();
                getSupportActionBar().setTitle(menuItem.getTitle());
                binding.drawerLayout.closeDrawers();
                return true;
            }
        };
        binding.navView.setNavigationItemSelectedListener(listener);

        if (savedInstanceState == null) {
            listener.onNavigationItemSelected(binding.navView.getMenu().getItem(0));
        } else {
            CharSequence title = savedInstanceState.getCharSequence(STATE_TITLE);
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(STATE_TITLE, getSupportActionBar().getTitle());
    }
}
