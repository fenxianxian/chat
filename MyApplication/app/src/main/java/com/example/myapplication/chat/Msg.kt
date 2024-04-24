package com.example.myapplication.chat

class Msg(val content: String, val type: Int?, val senderNickname: String?) {
    companion object {
        const val TYPE_RECEIVED = 0
        const val TYPE_SENT = 1
    }
}