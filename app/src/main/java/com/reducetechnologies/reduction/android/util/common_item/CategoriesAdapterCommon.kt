package com.reducetechnologies.reduction.android.util.common_item

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.reducetechnologies.reduction.android.util.CategoryAdapterPositionSaver
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.CategoriesAdapter
import com.reduction_technologies.database.databases_utils.CommonItem
import com.reduction_technologies.database.helpers.CategoryTag

class CategoriesAdapterCommon(
    liveData: LiveData<Map<CategoryTag, List<CommonItem>>>,
    lifecycleOwner: LifecycleOwner,
    positionSaver: CategoryAdapterPositionSaver<CategoryTag>
) : CategoriesAdapter<CategoryTag, CommonItem>(
    liveData = liveData,
    lifecycleOwner = lifecycleOwner,
    delegate = CategoriesInfoDelegateCI,
    tagHolderCreator = ScatteredHolderCreator_CommonItem,
    itemHolderCreatorBuilder = HolderBindDelegateBuilder_CommonItem(),
    categoryAdapterPositionSaver = positionSaver
) {

}