package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.settings

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.reducetechnologies.reduction.R

data class ItemToDestination(
    // Title of the settings item
    @StringRes val title: Int,
    // Id of action
    @IdRes val actionId: Int)

/**
 * Used in settings fragment
 */
object ItemToDestinationProvider {
    val items : List<ItemToDestination> = listOf(
        ItemToDestination(R.string.settings_about, R.id.action_settingsFragment_to_aboutFragment),
        ItemToDestination(R.string.testing_animation, R.id.action_settingsFragment_to_testingAnimationFragment)
    )
}