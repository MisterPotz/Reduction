package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.item

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.navigation.navArgs
import com.reducetechnologies.command_infrastructure.PField
import com.reducetechnologies.command_infrastructure.PScreen
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.PScreenSimpleeInflater
import kotlinx.android.synthetic.main.apptoolbar.*

class ItemActivity : AppCompatActivity() {
    private lateinit var container : FrameLayout
    private val args by navArgs<ItemActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        setSupportActionBar(toolbar!!)

        container = findViewById(R.id.itemContainer)
        showPScreen()
    }

    override fun onStart() {
        super.onStart()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun showPScreen() {
        val gson = PField.makeGson()
        val pscreen = gson.fromJson(args.itemPScreen, PScreen::class.java)
//        pscreen.fields.filter { it.pFieldType == PFieldType.MATH_TEXT }.forEach {
//            (it.typeSpecificData as MathTextSpec).text
//            (it.typeSpecificData as MathTextSpec).text = addBackslashes((it.typeSpecificData as MathTextSpec).text)
//        }
        PScreenSimpleeInflater.inflatPScreen(pscreen, container, resources.displayMetrics, windowManager, layoutInflater, false, null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }
}
