package tech.kood.parallaxrecyclerview.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import tech.kood.parallaxrecyclerview.ParallaxImageListener
import tech.kood.parallaxrecyclerview.R


class ParallaxImageView : AppCompatImageView {

    private val DEFAULT_PARALLAX_RATIO = 1.2f
    private val DEFAULT_CENTER_CROP = true
    private var parallaxRatio = DEFAULT_PARALLAX_RATIO
    private var shouldCenterCrop = DEFAULT_CENTER_CROP
    private var fromScale = 1f
    private var toScale = 3f
    private var withZoomAnimation = false

    private var needToTranslate = true
    var listener: ParallaxImageListener? = null
    private var itemAxisPosition = -1
    private var recyclerViewScrollLength = -1
    private var recyclerViewAxisPosition = -1
    private var itemViewLength = -1
    private var orientation = 0
    private var safeScalePercentage: Float = 0f

    private val values: Boolean
        get() {
            val values = listener!!.requireValuesForTranslate() ?: return false
            itemAxisPosition = values[0]
            recyclerViewScrollLength = values[1]
            recyclerViewAxisPosition = values[2]
            itemViewLength = values[3]
            orientation = values[4]
            safeScalePercentage = (itemViewLength.toFloat() / recyclerViewScrollLength.toFloat())
            return true
        }


    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        scaleType = ScaleType.MATRIX
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.ParallaxImageView, 0, 0)
            parallaxRatio = ta.getFloat(R.styleable.ParallaxImageView_parallax_ratio, DEFAULT_PARALLAX_RATIO)
            shouldCenterCrop = ta.getBoolean(R.styleable.ParallaxImageView_center_crop, DEFAULT_CENTER_CROP)
            fromScale = ta.getFloat(R.styleable.ParallaxImageView_fromScale, 1f)
            toScale = ta.getFloat(R.styleable.ParallaxImageView_fromScale, 3f)
            withZoomAnimation = ta.getBoolean(R.styleable.ParallaxImageView_withZoomAnimation, false)
            ta.recycle()
        }
    }

    /**
     * This trick was needed because there is no way to detect when image is displayed,
     * we need to translate image for very first time as well. This will be needed only
     * if you are using async image loading...
     *
     *
     * # If only there was another way to get notified when image has displayed.
     */
    // region EnsureTranslate
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        ensureTranslate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        ensureTranslate()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        ensureTranslate()
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        ensureTranslate()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        ensureTranslate()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        ensureTranslate()
    }

    // endregion
    /**
     * Notify this view when it is back on recyclerView, so we can reset.
     */
    fun reuse() {
        needToTranslate = true
    }

    fun centerCrop(enable: Boolean) {
        shouldCenterCrop = enable
    }

    fun setParallaxRatio(parallaxRatio: Float) {
        this.parallaxRatio = parallaxRatio
    }

    @Synchronized
    fun doTranslate(): Boolean {
        if (drawable == null) {
            return false
        }
        return if (listener != null && values) {
            calculateAndMove(orientation == ParallaxRecyclerView.VERTICAL)
            true
        } else {
            false
        }
    }

    private fun ensureTranslate(): Boolean {
        if (needToTranslate) {
            needToTranslate = !doTranslate()
        }
        return !needToTranslate
    }

    private fun calculateAndMove(isVertical: Boolean) {
        val recyclerViewCenter = (recyclerViewAxisPosition + recyclerViewScrollLength) / 2
        val distanceFromCenter = recyclerViewCenter - itemAxisPosition.toFloat()
        var drawableScrollingLineLength = if (isVertical) drawable.intrinsicHeight else drawable.intrinsicWidth
        val imageViewScrollingLineLength = if (isVertical) measuredHeight else measuredWidth
        var scale = 1f
        if (shouldCenterCrop) {
            scale = recomputeImageMatrix()
            drawableScrollingLineLength *= scale.toInt()
        }
        val difference = drawableScrollingLineLength - imageViewScrollingLineLength.toFloat()
        val move = distanceFromCenter / recyclerViewScrollLength * difference * parallaxRatio



        if (withZoomAnimation) {
            val isZoomIn = fromScale < toScale
            val calculatedToScale = toScale - fromScale
            val itemAxisPositionInsideRv = itemAxisPosition - recyclerViewAxisPosition
            val scalePercentage: Float = itemAxisPositionInsideRv.toFloat() / recyclerViewScrollLength.toFloat()

            scale =
                    if (itemAxisPositionInsideRv != 0)
                        if (isZoomIn)
                            calculatedToScale - (scalePercentage * fromScale)
                        else
                            fromScale + (scalePercentage * calculatedToScale)
                    else
                        if (isZoomIn) calculatedToScale else fromScale

            // recyclerViewScrollLength = 1944
            //Log.e("Z_", "scalePercentage= ${itemAxisPositionInsideRv.toFloat()} / ${recyclerViewScrollLength.toFloat()} = scalePercentage=$scalePercentage")
            //Log.e("Z_", "newScale=$newScale")
        }
        moveTo(move / 2 - difference / 2, scale, isVertical)
    }

    private fun recomputeImageMatrix(): Float {
        val scale: Float
        val viewWidth = width - paddingLeft - paddingRight
        val viewHeight = height - paddingTop - paddingBottom
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight
        scale = if (drawableWidth * viewHeight > drawableHeight * viewWidth)
            viewHeight.toFloat() / drawableHeight.toFloat()
        else
            viewWidth.toFloat() / drawableWidth.toFloat()

        return scale
    }

    private fun moveTo(move: Float, scale: Float, isVertical: Boolean) {
        val imageMatrix = imageMatrix

        if (withZoomAnimation) {
            if (scale < 1f)
                imageMatrix.setScale(1f, 1f)
            else
                imageMatrix.setScale(scale, scale)
        } else
            if (scale != 1f)
                imageMatrix.setScale(scale, scale)

        val matrixValues = FloatArray(9)
        imageMatrix.getValues(matrixValues)

        if (isVertical)
            imageMatrix.postTranslate(0f, move - matrixValues[Matrix.MTRANS_Y])
        else
            imageMatrix.postTranslate(move - matrixValues[Matrix.MTRANS_X], 0f)

        setImageMatrix(imageMatrix)
        invalidate()
    }
}