package com.reduction_technologies.database.helpers

import android.content.Context
import javax.inject.Inject

/**
 * The purpose of this class is to provide the rest code of application with useful data related
 * to GOST tables, encyclopedia, and user favorite items.
 * Some fields are in
 */
class Repository @Inject constructor(val context: Context,
                                     /**
                                      * THe field is injectable so instances of constant database can be mocked
                                      */
                                     // TODO спрятать зависимость от constant в интерфей
                                     val constantDatabaseHelper: ConstantDatabaseHelper,
                                     /**
                                      * Injectible for the sake of testing and reusability
                                      */
                                     val userDatabaseHelper: UserDatabaseHelper
)