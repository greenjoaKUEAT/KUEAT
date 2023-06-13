package com.example.kueat.ui.home


data class Restaurant(val article_number:Int,val location:location, val name:String,
                      val photo:String, val rating:Float, val restaurant_id:Int,
                      val tag:String, val tag_location:String, val tag_type:String ){
    constructor():this(0 , location(37.0,137.5), "알촌", "", 0.0F,
        0, "후문한식","후문","한식"
    )
}

data class location(val Latitude:Double, val Longitude:Double){
    constructor():this(37.0,137.5)
}
