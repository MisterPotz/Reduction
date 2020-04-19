package com.reducetechnologies.reduction.android.util.common_item

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.CategoriesAdapter
import com.reduction_technologies.database.databases_utils.CommonItem

class CategoriesAdapterCommon(
    liveData: LiveData<List<CommonItem>>,
lifecycleOwner: LifecycleOwner) : CategoriesAdapter<CommonItem>(
    liveData = liveData,
    lifecycleOwner = lifecycleOwner,
    delegate = CategoriesInfoDelegateCI,
    tagHolderCreator = ScatteredHolderCreator_CommonItem,
    itemHolderCreatorBuilder = HolderBindDelegateBuilder_CommonItem()
) {

}