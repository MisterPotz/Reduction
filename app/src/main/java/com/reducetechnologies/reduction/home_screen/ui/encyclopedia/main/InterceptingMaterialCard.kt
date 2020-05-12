package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.material.card.MaterialCardView

class InterceptingMaterialCardView : MaterialCardView {
    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    constructor(ctx: Context, attrs: AttributeSet, dAttr: Int) : super(ctx, attrs, dAttr)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        // intercept all movements - not a single click goes to webview or any
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
}