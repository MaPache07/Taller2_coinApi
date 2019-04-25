package com.naldana.ejemplo10.models

data class Coin (var _id:String,
                 var name: String,
                 var country: String,
                 var value: Int,
                 var values_us: Double,
                 var year: Int,
                 var isAvailable: Boolean,
                 var img: String)