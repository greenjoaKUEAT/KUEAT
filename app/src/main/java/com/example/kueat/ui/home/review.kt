package com.example.kueat.ui.home

data class review(var restName : String, var content :String, var likenum:Int,var commentnum:Int) {
    constructor():this("no info","no info",0,0)
}