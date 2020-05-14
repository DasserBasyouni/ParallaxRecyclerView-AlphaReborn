package tech.kood.parallaxrecyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import tech.kood.parallaxrecyclerview.view.ParallaxImageView


open class ParallaxViewHolderKotlin(itemView: View, parallaxImageId: Int) : RecyclerView.ViewHolder(itemView), ParallaxImageListener {

    var backgroundImage: ParallaxImageView = itemView.findViewById(parallaxImageId)

    init {
        // TODO fix the leak
        backgroundImage.listener = this
    }

    override fun requireValuesForTranslate(): IntArray? {
        return if (itemView.parent == null) {
            // Not added to parent yet!
            null
        } else {
            val itemPosition = IntArray(2)
            itemView.getLocationOnScreen(itemPosition)
            val recyclerPosition = IntArray(2)
            (itemView.parent as RecyclerView).getLocationOnScreen(recyclerPosition)
            intArrayOf(itemPosition[1], (itemView.parent as RecyclerView).measuredHeight, recyclerPosition[1])
        }
    }

    fun animateImage() {
        backgroundImage.doTranslate()
    }
}