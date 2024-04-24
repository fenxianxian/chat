package com.example.myapplication

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.example.myapplication.adapter.GroupListAdapter
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Group

//共同群聊
class CommonGroup : AppCompatActivity() {

    val grouplist=ArrayList<Group>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_group)

        InitMeAndfriendCommentGroupList();

        val group_adapter = GroupListAdapter(this,R.layout.group_list,grouplist)
        //给控件加适配器
        val lv_group: ListView = findViewById(R.id.lv_group)
        lv_group.adapter=group_adapter

    }
    fun InitMeAndfriendCommentGroupList(){

//        SELECT DISTINCT g.groupName
//        FROM MyGroup g
//        JOIN GroupMember m1 ON g.groupID = m1.groupID
//        JOIN GroupMember m2 ON g.groupID = m2.groupID
//        WHERE m1.userID = '我'
//        AND m2.userID = '对方';
        val dbHelper= MySqliteHelper(this,"chatHub",2)
        val db=dbHelper.writableDatabase
        val user: SharedPreferences = this.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        var current_username = user.getString("username", "")
        var friend_name = intent.getStringExtra("friend_name")
        val query = ("SELECT g.groupID,g.groupName "
                + "FROM MyGroup g "
                + "JOIN GroupMember m1 ON g.groupID = m1.groupID "
                + "JOIN GroupMember m2 ON g.groupID = m2.groupID "
                + "WHERE m1.userID = ? AND m2.userID = ?")
        val selectionArgs = arrayOf(current_username, friend_name)
        val cursor = db.rawQuery(query, selectionArgs)
        if(cursor.moveToFirst())
        {
            do {
                val groupID = cursor.getInt(cursor.getColumnIndex("groupID"))
                val groupName = cursor.getString(cursor.getColumnIndex("groupName"))
                grouplist.add(
                    Group(
                        groupID,groupName
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

}
