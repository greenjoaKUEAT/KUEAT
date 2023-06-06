package com.example.kueat.ui.appeal

data class AppealArticleInfo(
    var article_id: Int,
    var user_id: Int,
    var restaurant_id: Int,
    var type: Int,//0-리뷰글, 1-청원글
    var title: String,
    var description: String,
    var liked_user_number: Int,
    var comment_number: Int,
    var date: String
)