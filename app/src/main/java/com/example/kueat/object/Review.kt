package com.example.kueat.`object`

data class Review(var article_id : String, var comment_number:String ,var content :String,var date:String ,
                  var liked_user_number:String,var restaurant_id:String,var title:String,var type:String,var uid:String) {
    constructor():this("no info","no info","no info","no info","no info",
        "no info","no info","no info","no info")
}