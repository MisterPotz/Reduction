package com.reducetechnologies.reduction.android.util

import dagger.Module

/**
 * Закрепляет за одним холдером массив-набор элементов, которые отображаются этим холдером
 */
data class ScatteredItemData<T>(val list: List<SavedIdItem<T>>)

/**
 * Отображает сущность, у которой есть определенный айдишник в дб, и item, который может быть
 * подгружен впоследствии.
 */
data class SavedIdItem<T>(val id: Int, var item : T?)