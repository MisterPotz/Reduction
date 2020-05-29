package com.reducetechnologies.reduction.home_screen.ui.common

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.webkit.WebView
import com.google.gson.GsonBuilder
import com.reducetechnologies.calculations_entity.OneStepData
import com.reducetechnologies.calculations_entity.ReducerData
import com.x5.template.Chunk
import com.x5.template.Theme
import com.x5.template.providers.AndroidTemplates

class ResultsView : WebView {
    val reducersData = "ReducersData"
    val resultsPage = "ResultPage"
    val gson = GsonBuilder().create()

    private lateinit var loader: AndroidTemplates
    private lateinit var theme: Theme

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        settings.javaScriptEnabled = true
        isVerticalScrollBarEnabled = true;
        requestFocus()
        setBackgroundColor(Color.TRANSPARENT)
        loader = AndroidTemplates(context)
        theme = Theme(loader)
    }

    private fun getResultPageChunk(): Chunk {
        val chunk: Chunk = theme.makeChunk("ResultPage")
//        Toast.makeText(context, chunk.toString(), Toast.LENGTH_SHORT).show();
        return chunk
    }

    fun setResults(results: String) {
        val reducersData = gson.fromJson(results, ReducerData::class.java)
        val chunk = getResultPageChunk()
        chunk.setResultsData(reducersData)

        loadDataWithBaseURL(null, chunk.toString(), "text/html", "utf-8", "about:blank")
    }

    private fun Chunk.setStageData(data: OneStepData) : Chunk {
        set("uStep", data.uStep)
        set("PSIBA", data.PSIBA)
        set("AW", data.AW)
        set("ALF", data.ALF)
        set("BET", data.BET)
        set("M", data.M)
        set("FT", data.FT)
        set("FR", data.FR)
        set("FA", data.FA)
        set("SGH", data.SGH)
        set("SGHD", data.SGHD)
        set("SGHM", data.SGHM)
        set("SGHMD", data.SGHMD)
        // arrays
        set("Z", data.Z)
        set("X", data.X)

        set("D", data.D)
        set("DW", data.DW)
        set("DF", data.DF)
        set("DA", data.DA)
        set("BW", data.BW)
        set("HRC", data.HRC)

        set("SGF", data.SGF)
        set("SGFD", data.SGFD)

        set("SGFM", data.SGFM)
        set("SGFMD", data.SGFMD)
        return this
    }

    private fun Chunk.setResultsData(data: ReducerData) {
        val firstStageData = data.lowSpeedStepData
        val secondStageData = data.highSpeedStepData
        set("U", data.commonData.U)
        set("THighSpeedStep", data.commonData.THighSpeedStep)
        set("TLowSpeedStep", data.commonData.TLowSpeedStep)
        set("NHighSpeedStep", data.commonData.NHighSpeedStep)
        set("NLowSpeedStep", data.commonData.NLowSpeedStep)
        set("mechanismsMass", data.commonData.mechanismsMass)
        set("wheelsMass", data.commonData.wheelsMass)
        set("degreeOfAccuracy", data.commonData.degreeOfAccuracy)

        val firstChunk = theme.makeChunk("ReducersData")
        val firstString = firstChunk.setStageData(firstStageData).toString()
        val secondString = if (secondStageData != null) {
            val secondChunk = theme.makeChunk("ReducersData")
            secondChunk.setStageData(secondStageData).toString()
        } else ""
        set("firstStage", firstString)
        set("secondStage", secondString)
    }
}