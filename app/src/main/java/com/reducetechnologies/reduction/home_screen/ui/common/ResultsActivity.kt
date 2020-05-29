package com.reducetechnologies.reduction.home_screen.ui.common

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavArgs
import androidx.navigation.navArgs
import com.google.gson.GsonBuilder
import com.reducetechnologies.calculations_entity.ReducerData
import com.reducetechnologies.reduction.R
import kotlinx.android.synthetic.main.activity_results.*
import kotlinx.android.synthetic.main.apptoolbar.*


class ResultsActivity : AppCompatActivity() {
    val results by navArgs<ResultsActivityArgs>()
    // how to use a string of HTML as the source of a WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        setSupportActionBar(toolbar!!)
        supportActionBar?.title = getString(R.string.results)
    }

    private fun showResults() {
        resultsView.setResults(results.reducersData)
    }

    override fun onStart() {
        super.onStart()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        showResults()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }
}
