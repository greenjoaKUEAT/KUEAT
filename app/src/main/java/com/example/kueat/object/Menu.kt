package com.example.kueat.`object`

data class Menu(var content :String, var name : String, var price:String,var restaurant_id:String,
                var restaurant_name:String,var signature:String)  {
    constructor():this("no info","no info","no info","no info","no info","no info")
}