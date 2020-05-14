package tech.kood.parallaxrecyclerview.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import tech.kood.parallaxrecyclerview.ParallaxViewHolder

class ParallaxRecyclerView : RecyclerView {

    companion object {
        const val VERTICAL = 0
        const val HORIZONTAL = 1
    }

    private var scrollListener: OnScrollListener? = null


    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle) {
        init()
    }


    override fun addOnScrollListener(listener: OnScrollListener) =
            if (listener !== defaultListener) scrollListener = listener else super.addOnScrollListener(listener)


    private fun init() = addOnScrollListener(defaultListener)

    private val defaultListener: OnScrollListener = object : OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (scrollListener != null) scrollListener!!.onScrollStateChanged(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            for (i in 0 until recyclerView.childCount) {
                val viewHolder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i))
                if (viewHolder is ParallaxViewHolder) {
                    viewHolder.animateImage()
                }
            }

            if (scrollListener != null) scrollListener!!.onScrolled(recyclerView, dx, dy)
        }
    }

}