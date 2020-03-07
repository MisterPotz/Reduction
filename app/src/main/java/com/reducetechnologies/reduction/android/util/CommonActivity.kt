package com.reducetechnologies.reduction.android.util

import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

abstract class CommonActivity(
    @LayoutRes val layoutId: Int
) : AppCompatActivity() {
    private val debugTree = Timber.DebugTree()

    abstract val TAG: String

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> finish().let { true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        commonActionsOnCreate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        commonActionsOnCreate()
    }

    override fun onPause() {
        super.onPause()
        commonActionsOnPause()
    }

    protected fun commonActionsOnCreate() {
        initToolbar()
        Timber.plant(debugTree)
    }

    private fun commonActionsOnPause() {
        Timber.uproot(debugTree)
    }

    protected fun initToolbar() {
        setContentView(layoutId)
        //this.setSupportActionBar(toolbar)
        supportActionBar!!.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.title = TAG
        }
    }
}
