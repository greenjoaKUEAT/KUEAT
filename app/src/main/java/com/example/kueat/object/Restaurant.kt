package com.example.kueat.`object`

data class Restaurant(val article_number:Long,val latitude:String,val longitude:String, val name:String,
                      val photo:String, val rating:String, val restaurant_id:Long,
                      val tag_location:String, val tag_type:String ,val signature:String){
    constructor():this(0 , "37.541701","127.078775","알촌", "", "5.0",
        0,"후문","한식","다"
    )
}

