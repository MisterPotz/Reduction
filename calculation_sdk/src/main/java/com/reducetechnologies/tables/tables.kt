package com.reducetechnologies.tables

/**
 * [PEDS] - соответствие оборотов мощностям
 */
val PEDS: Map<Float, Array<Int>> = mapOf(0.75f to arrayOf(2840, 1390, 915, 700),
    1.1f to arrayOf(2810, 1420, 920, 700), 1.5f to arrayOf(2850, 1415, 935, 700), 2.2f to
arrayOf(2850, 1425, 935, 700), 3.0f to arrayOf(2840, 1435, 955, 700), 4f to arrayOf(2880, 1430, 950,
        720), 5.5f to arrayOf(2880, 1445, 965, 720), 7.5f to arrayOf(2900, 1455, 970, 730),
    11f to arrayOf(2900, 1460, 975, 730), 15f to arrayOf(2940, 1465, 975, 730))
//Дописать все массивы или закинуть их в таблички
val TTEDS: Map<Float, Array<Float>> = mapOf(0.75f to arrayOf(2.2f, 2.2f, 2.2f, 1.9f))

val D1EDS: Map<Float, Array<Int>> = mapOf(0.75f to arrayOf(19, 19, 22, 22))

val L1ES: Map<Float, Array<Int>> = mapOf(0.75f to arrayOf(40, 40, 50, 50))

val H1EDS: Map<Float, Array<Int>> = mapOf(0.75f to arrayOf(71, 71, 80, 80))

val MAEDS: Map<Float, Array<Float>> = mapOf(0.75f to arrayOf(15.1f, 15.1f, 17.4f, 29.7f))

val SGTT: Map<Float, Array<Int>> = mapOf(24.8f to arrayOf(590, 590), 28.5f to arrayOf(700, 700),
    49f to arrayOf(780, 780), 59f to arrayOf(800, 780) )