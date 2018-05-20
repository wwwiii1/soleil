package net.daum.www.soleil.View.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import net.daum.www.soleil.R
import net.daum.www.soleil.View.Fragment.ContentListFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            var transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.frame_layout_1, ContentListFragment())
            transaction.commit()

    }

}
