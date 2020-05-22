package com.reducetechnologies.command_infrastructure

/**
 * Contains fields that must be displayed to user
 * [title] - how title is displayed
 * [fields] - what must be presented, sorted in order of the appearance
 */
data class PScreen(
    val title: String,
    val fields: List<PField>){

}