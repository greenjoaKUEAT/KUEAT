package com.example.kueat.ui.home

data class Review(var article_id : Int, var comment_number:Int ,var content :String,var date:String ,
                  var liked_user_number:Int,var restaurant_id:Int,var title:String,var type:Int,var uid:String) {
    constructor():this(0,0,"no info","no info",0,
    0,"no info",0,"no info")
}