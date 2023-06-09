package com.example.kueat.ui.home

data class Menu(var content :String, var name : String, var price:Int,var restaurant_id:Int,
                var restaurant_name:String,var signature:Int)  {
    constructor():this("no info","no info",0,0,"no info",0)
}