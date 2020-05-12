package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main

import android.util.AttributeSet
import android.util.Xml
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.reducetechnologies.reduction.R
import org.junit.Test
import org.junit.runner.RunWith
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class CommonItemEncyclopediaViewBinderTest {
    /**
     * Playing with parsing attributes of xml layout
     */
    @Test
    fun parsingXmlParams(){
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val parser = appContext.resources.getLayout(R.layout.closed_card_item_text)
        var state = 0
        var attributes: AttributeSet? = null
        do {
            try {
                state = parser.next()
            } catch (e1: XmlPullParserException) {
                e1.printStackTrace()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
            if (state == XmlPullParser.START_TAG) {
                if (parser.name == "com.google.android.material.card.MaterialCardView") {
                    attributes = Xml.asAttributeSet(parser)
                    break
                }
            }
        } while (state != XmlPullParser.END_DOCUMENT)
    }
}