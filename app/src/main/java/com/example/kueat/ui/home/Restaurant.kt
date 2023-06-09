package com.example.kueat.ui.home

import com.google.android.gms.maps.model.LatLng

data class Restaurant(val photo:String, val name:String, val article_number:Int,
                      val location:LatLng, val rating:Float, val restaurant_id:Int,
                      val tag_location:String, val tag_type:String){
    constructor():this("0", "name", 0,LatLng(37.0,137.5), 0.0F,
    0, "위치 필터","메뉴 필터"
    )
}