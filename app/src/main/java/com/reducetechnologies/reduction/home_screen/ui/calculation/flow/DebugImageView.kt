package com.reducetechnologies.reduction.home_screen.ui.calculation.flow

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import timber.log.Timber

class DebugImageView(context:Context, attrs: AttributeSet, defStyleAttr:Int)
    : androidx.appcompat.widget.AppCompatImageView(context , attrs,  defStyleAttr ) {
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        addOnLayoutChangeListener {v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            Timber.e("onLayoutChangeListener v $v, left $left, top $top, right $right, bottom $bottom, oldLeft $oldLeft, oldTop $oldTop, oldRight $oldRight, oldBottom $oldBottom")
        }
    }
    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        Timber.e("setImageBitmap")
    }

    override fun setMaxWidth(maxWidth: Int) {
        super.setMaxWidth(maxWidth)
        Timber.e("setMaxWidth")

    }

    override fun setMaxHeight(maxHeight: Int) {
        super.setMaxHeight(maxHeight)
        Timber.e("setMaxHeight")

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Timber.e("onAttachedToWindow")
    }

    override fun getRootView(): View {
        return super.getRootView()
        Timber.e("getRootView")

    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        Timber.e("drawableStateChanged")

    }

    override fun getVisibility(): Int {
        return super.getVisibility()
        Timber.e("getVisibility")

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Timber.e("onDetachedFromWindow")

    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        Timber.e("setVisibility")
    }

    override fun setBackgroundDrawable(background: Drawable?) {
        super.setBackgroundDrawable(background)
        Timber.e("setBackgroundDrawable")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Timber.e("onMeasure, widthMeasureSpec: $widthMeasureSpec, heightMeasureSpec: $heightMeasureSpec; measuredHeight: $measuredHeight measuredWidth: $measuredWidth")
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        Timber.e("setImageDrawable")

    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        Timber.e("setImageResource")

    }

    override fun isAttachedToWindow(): Boolean {
        return super.isAttachedToWindow()
        Timber.e("isAttachedToWindow")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Timber.e("onDraw")

    }
}