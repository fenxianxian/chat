package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.example.myapplication.adapter.DynamicsAdapter
import com.example.myapplication.adapter.PullPeopleAdapter
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Friend
import java.util.ArrayList

class PullPeople : AppCompatActivity() {

    private lateinit var lv_noInGroup: ListView
    var inviteableFriends= ArrayList<Friend>()
    val friendlist=ArrayList<Friend>()
    val groupMembers=ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pull_people)

        lv_noInGroup = findViewById(R.id.lv_noInGroup)

//        //定义数据源
        initData()

        //定义一个适配器
        val determine: Button =findViewById(R.id.determine)
        val pullPeopleAdapter = PullPeopleAdapter(this,R.layout.friend_list,inviteableFriends,determine)
        //给控件加适配器
        lv_noInGroup.adapter=pullPeopleAdapter
        var GroupName = intent.getStringExtra("userNichen")
        //Toast.makeText(this,GroupName,Toast.LENGTH_SHORT).show()
        determine.setOnClickListener{
            val dbHelper= MySqliteHelper(this,"chatHub",2)
            val db=dbHelper.writableDatabase
            var GroupId = intent.getIntExtra("GroupId",-1)
            var backgroundColor = (determine.background as ColorDrawable).color
            val expectedColor = Color.parseColor("#FFA500")
            if (backgroundColor == expectedColor) {
                var selectList = pullPeopleAdapter.getSelectList()
                for (item in selectList) {
                    val values2 = ContentValues().apply {
                        put("groupID",GroupId)
                        put("userID","$item")
                    }
                    val values4 = ContentValues().apply {
                        put("userId","$item")
                        put("friendId",GroupName)
                        put("agree",1)
                        put("is_interact",0)
                        put("friendType",1)
                        put("onlyId",GroupId)
                    }
                    db.insert("GroupMember",null,values2)
                    db.insert("user_friend",null,values4)
                }
                Toast.makeText(this,"拉人成功！！",Toast.LENGTH_SHORT).show()
                val intent = Intent(this,UserSpace::class.java)
                intent.putExtra("friendType",1)
                intent.putExtra("onlyId",GroupId)
                startActivity(intent)
            }
        }
    }
    fun initData(){
        val userPrefs = this.getSharedPreferences("user", Context.MODE_PRIVATE)
        val currentUsername = userPrefs.getString("username", "")
        var GroupId = intent.getIntExtra("GroupId",-1)
        inviteableFriends = getInviteableFriends(GroupId,currentUsername);
    }

    // 根据群组ID和用户ID获取可邀请的好友列表
    fun getInviteableFriends(groupId: Int, userId: String?): ArrayList<Friend> {
        val groupMembers = getGroupMembers(groupId,userId)
        val userFriends = getUserFriends(userId)
        //return userFriends.filter { friendId -> friendId !in groupMembers }
        //ArrayList 的构造函数接受一个 Collection 类型的参数，可以将其他集合类型（如 List）转换为 ArrayList
        val inviteableFriends =ArrayList(userFriends
            //.map { friend -> friend.friendId } // 提取每个好友对象的标识符
            //.filter { friendId -> friendId !in groupMembers }   //这个仅仅只能返回 List<String> ，不能返回 List<Friend>
            .filter { friend -> friend.friendId !in groupMembers })
        return inviteableFriends;
    }

    //查询指定群组的成员列表
    fun getGroupMembers(groupId: Int,currentUsername: String?): ArrayList<String> {
        val dbHelper = MySqliteHelper(this, "chatHub", 2)
        val db = dbHelper.readableDatabase
        val selection = "groupID = ?"
        val selectionArgs = arrayOf(groupId.toString())
        val cursor = db.query("GroupMember", arrayOf("userID"), selection, selectionArgs, null, null, null)
        while (cursor.moveToNext()) {
            val userID = cursor.getString(cursor.getColumnIndex("userID"))
            if(currentUsername != userID){
                groupMembers.add(userID)
            }
        }
        cursor.close()
        db.close()
        return groupMembers
    }


    // 查询用户的好友列表
    fun getUserFriends(currentUsername: String?): ArrayList<Friend> {
        val dbHelper= MySqliteHelper(this,"chatHub",2)
        val db=dbHelper.writableDatabase
        val cursor = db.query("user_friend",null,null,null,null,null,null)
        if(cursor.moveToFirst()) {
            do {
                val id = cursor.getColumnIndex("id")
                val userName = cursor.getString(cursor.getColumnIndex("userId"))
                if (currentUsername == userName) {
                    val friendName = cursor.getString(cursor.getColumnIndex("friendId"))
                    if (currentUsername != friendName) {
                        val agree = cursor.getInt(cursor.getColumnIndex("agree"))
                        val friendType = cursor.getInt(cursor.getColumnIndex("friendType"))
                        val onlyId = cursor.getInt(cursor.getColumnIndex("onlyId"))
                        val friendRemarks = cursor.getString(cursor.getColumnIndex("friendRemarks"))
                        val is_delete = cursor.getInt(cursor.getColumnIndex("is_delete"))
                        if (agree == 1 && friendType == 0 && is_delete == 0) {
                            friendlist.add(
                                Friend(
                                    id,
                                    userName,
                                    friendName,
                                    "",
                                    R.drawable.xiaoai,
                                    friendType,
                                    onlyId,
                                    friendRemarks,
                                    is_delete,
                                    false
                                )
                            );
                        }
                    }
                }
            } while (cursor.moveToNext())
        }
        return friendlist;
    }

}
