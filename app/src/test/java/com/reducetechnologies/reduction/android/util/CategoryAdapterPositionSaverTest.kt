package com.reducetechnologies.reduction.android.util

import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView
import com.reduction_technologies.database.helpers.CategoryTag
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


internal class CategoryAdapterPositionSaverTest {

    @Test
    fun plainSave() {
        val saver = CategoryAdapterPositionSaver<CategoryTag>()
        val layoutManager = mockk<RecyclerView.LayoutManager>()
        val saved = mockk<Parcelable>()

        every { layoutManager.onSaveInstanceState() } answers {
            saved
        }

        val slot = slot<Parcelable>()
        every { layoutManager.onRestoreInstanceState(capture(slot)) } answers { }

        saver.saveState(layoutManager)

        verify { layoutManager.onSaveInstanceState() }

        saver.restoreState(layoutManager)

        assertEquals(slot.captured, saved)
    }

    @Test
    fun outerSave() {
        val tag1 = CategoryTag.TABLE
        val tag2 = CategoryTag.VARIABLE

        val layoutManager1 = mockk<RecyclerView.LayoutManager>()
        val ans1 = mockk<Parcelable>()
        every { layoutManager1.onSaveInstanceState() } answers {
            ans1
        }
        val slot1 = slot<Parcelable>()
        every { layoutManager1.onRestoreInstanceState(capture(slot1)) } answers {}

        val layoutManager2 = mockk<RecyclerView.LayoutManager>()
        val ans2 = mockk<Parcelable>()
        every { layoutManager2.onSaveInstanceState() } answers {
            ans2
        }
        val slot2 = slot<Parcelable>()
        every { layoutManager2.onRestoreInstanceState(capture(slot2)) } answers {}


        val categorySaver = CategoryAdapterPositionSaver<CategoryTag>()
        val saver1 = categorySaver.getSaverForTag(tag1)
        val saver2 = categorySaver.getSaverForTag(tag2)

        saver1.saveState(layoutManager1)
        saver2.saveState(layoutManager2)

        saver1.restoreState(layoutManager1)
        saver2.restoreState(layoutManager2)

        assertEquals(ans1, slot1.captured)
        assertEquals(ans2, slot2.captured)
    }
}