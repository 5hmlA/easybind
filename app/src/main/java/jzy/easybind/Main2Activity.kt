package jzy.easybind

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import jzy.easybind.bindstar.MainActivity.mutableLiveData
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.content_main2.*

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)

        mutableLiveData.observe(this, Observer { s -> tv_livedata.setText(s) })

//        val s = Transformations.map(mutableLiveData, { input -> Integer(9) })
        fab.setOnClickListener { view ->
            mutableLiveData.value = "改变"
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

}
