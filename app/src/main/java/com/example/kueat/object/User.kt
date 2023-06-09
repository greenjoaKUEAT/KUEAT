package com.example.kueat.`object`


data class User(val uid:String,val email:String,var name:String,var nickname: String,val id:String){
    constructor():this("noinfo","noinfo","noinfo","noinfo","noinfo")
}

