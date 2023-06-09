package com.example.kueat.ui.home

data class Restaurant(var article_number: Int, var location:String, var name : String,
                      var rating :Double, var restaurant_id:Int,var tag_location:String,var tag_type:String)
{
    constructor():this(0,"no info","no info",0.1,0,"no info","no info")
}