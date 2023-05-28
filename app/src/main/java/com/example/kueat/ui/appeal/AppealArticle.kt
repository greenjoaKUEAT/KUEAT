package com.example.kueat.ui.appeal

data class AppealArticle(
    var type: Int,
    var title: String,
    var description: String,
    var liked_user_number: Int,
    var comment_number: Int
)