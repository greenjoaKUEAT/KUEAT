package com.example.kueat.`object`

data class Restaurant(val article_number:Long,val location:location, val name:String,
                      val photo:String, val rating:String, val restaurant_id:Long,
                      val tag_location:String, val tag_type:String ){
    constructor():this(0 , location("37.0","137.5"), "알촌", "", "5.0",
        0,"후문","한식"
    )
}

