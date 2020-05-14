package com.yayandroid.parallaxrecyclerview.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by yahyabayramoglu on 14/04/15.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        verticalBtn.setOnClickListener {
            setupParallaxRecyclerView(LinearLayoutManager.VERTICAL, R.layout.item_vertical)
        }

        horizontalBtn.setOnClickListener {
            setupParallaxRecyclerView(LinearLayoutManager.HORIZONTAL, R.layout.item_horizontal)
        }

        verticalBtn.performClick()
    }

    private fun setupParallaxRecyclerView(orientation: Int, layoutRes: Int) {
        recyclerView.layoutManager = LinearLayoutManager(this, orientation, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = SampleRecyclerAdapter(this, layoutRes)
    }
}