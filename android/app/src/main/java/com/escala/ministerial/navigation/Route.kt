package com.escala.ministerial.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Dashboard : Route
    @Serializable data object Ministros : Route
    @Serializable data object Eventos : Route
    @Serializable data object Escalas : Route
    @Serializable data object Feedback : Route
    @Serializable data object Auditoria : Route
}
