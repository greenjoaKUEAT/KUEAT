package com.example.kueat.`object`

data class Comment(
    var comment_id: Int,
    var user_id: Int,
    var article_id: Int,
    var content: String,
    var liked_user_number: Int,
    var type: Int, //0-댓글, 1-대댓글
    var date: String
) {
    constructor() : this(-1, -1, -1, "no info", -1, -1, "no info")
}
