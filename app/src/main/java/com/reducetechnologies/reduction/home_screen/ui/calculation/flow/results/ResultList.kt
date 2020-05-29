package com.reducetechnologies.reduction.home_screen.ui.calculation.flow.results

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.reducetechnologies.reduction.CalculationNavigationDirections
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.ResultListContainer
import com.reducetechnologies.reduction.home_screen.ui.common.ResultsActivity
import kotlinx.android.synthetic.main.apptoolbar.*
import timber.log.Timber

class ResultList : AppCompatActivity() {

    val list by navArgs<ResultListArgs>()
    private lateinit var resultContainer : ResultListContainer
    private val gson = GsonBuilder().create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_list)
        setSupportActionBar(toolbar!!)
        val gson = GsonBuilder().create()

        resultContainer = gson.fromJson(list.reducersDataList, ResultListContainer::class.java)
    }

    override fun onStart() {
        super.onStart()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val adapter = ResultsAdapter(LayoutInflater.from(baseContext), resultContainer.list.size) {
            val string = gson.toJson(resultContainer.list[it])
            val action =  CalculationNavigationDirections.actionGlobalResultsActivity(string)
            val bundle = action.arguments
            val intent = Intent(this, ResultsActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        val resultsListView = findViewById<RecyclerView>(R.id.resultsListView)
        Timber.i("resultsListView: $resultsListView")
        resultsListView.adapter = adapter
        resultsListView.layoutManager = LinearLayoutManager(baseContext)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }
}
