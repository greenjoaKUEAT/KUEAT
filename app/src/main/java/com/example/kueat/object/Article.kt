package com.example.kueat.`object`

data class Article(
    var article_id: String,
    var user_id: String,
    var restaurant_id: Int,
    var type: Int,//0-리뷰글, 1-청원글
    var title: String,
    var content: String,
    var liked_user_number: Int,
    var comment_number: Int,
    var date: String
) {
    constructor() : this(
        "no info", "no info", -1, -1, "no info",
        "no info", -1, -1, "no info"
    )
}