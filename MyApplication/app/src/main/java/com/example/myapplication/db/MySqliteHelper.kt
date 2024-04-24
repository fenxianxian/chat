package com.example.myapplication.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class MySqliteHelper (val context:Context,name:String,version:Int):SQLiteOpenHelper(context,name,null,version){

    val createUser="create table user(id integer primary key autoincrement,"+
            "username text,nickname text,password text,salt text,sex text,address text,headPhoto integer,signature text,regDate LocalDate)"
//    id:用户id
//    username:用户名
//    nickname:昵称
//    password：密码
//    sex：性别
//    address：地址
//    headPhoto：头像地址
//    signature：个性签名
//    regDate：注册时间
    val createUserAndFriend="create table user_friend(id integer primary key autoincrement,"+
            "userId text,friendId text,agree INT,apply_message text,is_interact INT,friendType INT,onlyId INT,friendRemarks text,is_delete INT)"
    //user_friend表可以看看哪个用户关注了哪个用户，类似添加了哪些好友
    //friendType :  0代表个人   1代表群
    //Only_Id: 只针对群类型的，表示群的唯一id
    //    is_delete: 表示对方是否把你删除 0代表无删除 1代表删除

    val chat_history = "CREATE TABLE chat_history (triggered_at TIMESTAMP PRIMARY KEY,"+
    "sender VARCHAR(50),receiver VARCHAR(50),message_type VARCHAR(50),message_content TEXT,session_id INT,letter INT,s_show INT,r_show INT,onlyId INT);"
//    触发时间：记录每条消息的发送时间，以便通过时间戳进行排序和查询。
//    发送方：记录消息发送方的身份，例如用户或机器人。
//    接收方：记录消息的接收方身份，例如机器人或用户。
//    消息类型：是私聊还是群聊   0代码私聊  1代表群聊
//    消息内容：存储消息内容，例如文本、图像或文件的URL链接。
//    会话ID：记录会话的唯一标识符，以便将来对会话进行分析。


    val say_something="CREATE TABLE say_something (say_ID INTEGER PRIMARY KEY AUTOINCREMENT,triggered_at TIMESTAMP,"+
            "userId VARCHAR(50),friendAndMeId VARCHAR(50),message_content TEXT,imgURL TEXT);"

    //群表
    val MyGroup="CREATE TABLE MyGroup (\n" +
            "    groupID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    groupName TEXT NOT NULL\n" +
            ");"

    //群成员表

    val GroupMember="CREATE TABLE GroupMember (\n" +
            "    groupID INTEGER,\n" +
            "    userID VARCHAR(50),\n" +
            "    isCreate INT,\n" +
            "    PRIMARY KEY (groupID, userID),\n" +
            "    FOREIGN KEY (groupID) REFERENCES MyGroup(groupID),\n" +
            "    FOREIGN KEY (userID) REFERENCES user(username)\n" +
            ");"

    //点赞表
    val Mylike = "CREATE TABLE LikeTable (\n" +
            "    dynamicID INTEGER,\n" +
            "    userID VARCHAR(50),\n" +
            "    PRIMARY KEY (dynamicID, userID),\n" +
            "    FOREIGN KEY (dynamicID) REFERENCES say_something(say_ID),\n" +
            "    FOREIGN KEY (userID) REFERENCES user(username)\n" +
            ");"

    //评论表
    val comment = "CREATE TABLE CommentTable (\n" +
            "    triggered_at TIMESTAMP,\n" +
            "    dynamicID INTEGER,\n" +
            "    userID VARCHAR(50),\n" +
            "    message VARCHAR(50),\n" +
            "    PRIMARY KEY (dynamicID, userID,message),\n" +
            "    FOREIGN KEY (dynamicID) REFERENCES say_something(say_ID),\n" +
            "    FOREIGN KEY (userID) REFERENCES user(username)\n" +
            ");"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createUser)
        db.execSQL(createUserAndFriend)
        db.execSQL(chat_history)
        db.execSQL(say_something)
        db.execSQL(MyGroup)
        db.execSQL(GroupMember)
        db.execSQL(Mylike)
        db.execSQL(comment)
        Toast.makeText(context,"成功的创建了数据库",Toast.LENGTH_SHORT).show()
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists user")
        db.execSQL("drop table if exists user_friend")
        db.execSQL("drop table if exists chat_history")
        db.execSQL("drop table if exists say_something")
        db.execSQL("drop table if exists MyGroup")
        db.execSQL("drop table if exists GroupMember")
        db.execSQL("drop table if exists LikeTable")
        db.execSQL("drop table if exists CommentTable")
        onCreate(db)
    }
}
