package com.yayandroid.parallaxrecyclerview.sample

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.kood.parallaxrecyclerview.ParallaxViewHolder
import tech.kood.parallaxrecyclerview.view.ParallaxRecyclerView


class SampleRecyclerAdapter internal constructor(private val context: Context, private val layoutRes: Int)
    : RecyclerView.Adapter<SampleRecyclerAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    // offline images
    /*
    private int[] imageIds = new int[]{R.mipmap.test_image_1,
            R.mipmap.test_image_2, R.mipmap.test_image_3,
            R.mipmap.test_image_4, R.mipmap.test_image_5};
    */

    // online images
    private val imageUrls = arrayOf(
            "https://yayandroid.com/data/github_library/parallax_listview/test_image_1.jpg",
            "https://yayandroid.com/data/github_library/parallax_listview/test_image_2.jpg",
            "https://yayandroid.com/data/github_library/parallax_listview/test_image_3.png",
            "https://yayandroid.com/data/github_library/parallax_listview/test_image_4.jpg",
            "https://yayandroid.com/data/github_library/parallax_listview/test_image_5.png")


    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(inflater.inflate(layoutRes, viewGroup, false),
                if (layoutRes == R.layout.item_vertical) ParallaxRecyclerView.VERTICAL else ParallaxRecyclerView.HORIZONTAL)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // viewHolder.getBackgroundImage().setImageResource(imageIds[position % imageIds.length]);

        // load image
        //Picasso.with(context).load(imageUrls[position % imageUrls.size]).into(viewHolder.backgroundImage)
        Glide.with(context).load(imageUrls[position % imageUrls.size]).into(viewHolder.backgroundImage)

        viewHolder.textView.text = "Row $position"

        // # CAUTION: Important to call this method
        viewHolder.backgroundImage.reuse()
    }

    override fun getItemCount(): Int = 5


    // CAUTION: ViewHolder must extend from ParallaxViewHolder
    class ViewHolder internal constructor(v: View, orientation: Int) : ParallaxViewHolder(v, orientation) {
        val textView: TextView = v.findViewById(R.id.label)

        override fun getParallaxImageId(): Int = R.id.backgroundImage
    }
}