package com.reduction_technologies.database.di

import javax.inject.Scope

/**
 * Метка для обозначения того, что класс, выдаваемый под ней должен быть сингльтоном на уровне
 * всего приложения.
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope