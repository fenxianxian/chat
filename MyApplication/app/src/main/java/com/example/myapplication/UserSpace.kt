package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.myapplication.adapter.GroupListAdapter
import com.example.myapplication.adapter.MemberListAdapter
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Friend
import com.example.myapplication.model.PostingUpdate
import java.text.SimpleDateFormat
import java.util.*

class UserSpace : AppCompatActivity() {

    val Memberlist=ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var friendType = intent.getIntExtra("friendType",-1)
        if(friendType==0){
            setContentView(R.layout.activity_user_space)
            var userName = intent.getStringExtra("userName")
            var friend_name = intent.getStringExtra("friend_name")
            var is_delete = intent.getIntExtra("is_delete",-1)
            var onlyId = intent.getIntExtra("onlyId",-1)
            //Toast.makeText(this,onlyId.toString(),Toast.LENGTH_SHORT).show()
            var findViewById:TextView = findViewById<TextView>(R.id.friend_name)
            var userNichen = intent.getStringExtra("userNichen")
            if(userName == null){
                findViewById.text = userNichen
            }else{
                findViewById.text = userName
                var friend_nichen:TextView = findViewById<TextView>(R.id.friend_nichen)
                friend_nichen.text = userNichen
                val nichen = findViewById<TextView>(R.id.nichen)
                nichen.visibility = View.VISIBLE
            }
//            SELECT COUNT(DISTINCT groupID)
//            FROM GroupMember
//                    WHERE groupID IN (SELECT groupID FROM GroupMember WHERE userID = '我')
//            AND groupID IN (SELECT groupID FROM GroupMember WHERE userID = '对方');
            val dbHelper= MySqliteHelper(this,"chatHub",2)
            val db=dbHelper.writableDatabase
            val userPrefs = this.getSharedPreferences("user", Context.MODE_PRIVATE)
            val currentUsername = userPrefs.getString("username", "")
            val subQuery = "(SELECT groupID FROM GroupMember WHERE userID = ?)"
            val selection = "groupID IN $subQuery AND groupID IN (SELECT groupID FROM GroupMember WHERE userID = ?)"

            val selectionArgs = arrayOf(currentUsername, userNichen)
            val columns = arrayOf("COUNT(DISTINCT groupID)")
            val cursor = db.query("GroupMember", columns, selection, selectionArgs, null, null, null)
            if(cursor != null && cursor.moveToFirst())
            {
                val count = cursor.getInt(0)
                var common_groupNumber:TextView = findViewById<TextView>(R.id.common_groupNumber)
                common_groupNumber.text = count.toString();
            }
            cursor.close()
            db.close()

            var go_send:Button = findViewById<Button>(R.id.go_send)
            go_send.setOnClickListener{
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("friend_name",userName); //备注
                intent.putExtra("nichen",userNichen)  //真实姓名
                intent.putExtra("friend_type",friendType);
                intent.putExtra("onlyId",onlyId);
                intent.putExtra("is_delete",is_delete);
                intent.putExtra("requestCode", 4)

                startActivity(intent);
            }
            var common_group: LinearLayout = findViewById<LinearLayout>(R.id.common_group)
            common_group.setOnClickListener{
                val intent = Intent(this, CommonGroup::class.java)
                intent.putExtra("friend_name",userNichen);
                startActivity(intent);
            }
            var dongtai: LinearLayout = findViewById<LinearLayout>(R.id.dongtai)
            dongtai.setOnClickListener{
                val intent = Intent(this, Dynamics::class.java)
                intent.putExtra("friend_name",userName)
                intent.putExtra("nichen",userNichen)
                startActivity(intent);
            }

            var set_remarks: LinearLayout = findViewById<LinearLayout>(R.id.set_remarks)
            set_remarks.setOnClickListener{
                val intent = Intent(this, SetRemarks::class.java)
                intent.putExtra("friend_name",userName)
                intent.putExtra("nichen",userNichen)
                startActivity(intent);
            }

            var toolbar_back: LinearLayout = findViewById<LinearLayout>(R.id.toolbar_back)
            toolbar_back.setOnClickListener{
                finish()
            }

            var delete_user: LinearLayout = findViewById<LinearLayout>(R.id.delete_user)
            delete_user.setOnClickListener{
                val intent = Intent(this, DeleteUser::class.java)
                intent.putExtra("friend_name",userName)
                intent.putExtra("friend_type",friendType);
                intent.putExtra("nichen",userNichen)
                startActivity(intent);
            }


        }else if(friendType==1){
            setContentView(R.layout.activity_group_details)
            var userNichen = intent.getStringExtra("userNichen")
            var onlyId = intent.getIntExtra("onlyId",-1)
//            intent.putExtra("userNichen",userNichen);
//            intent.putExtra("onlyId",onlyId);
//            Toast.makeText(this,userNichen.toString(),Toast.LENGTH_SHORT).show()
//            Toast.makeText(this,onlyId.toString(),Toast.LENGTH_SHORT).show()

            InitGroupMember(onlyId.toString());

            val groupMember_adapter = MemberListAdapter(this,R.layout.user_list_table1,Memberlist,onlyId,userNichen)
            //给控件加适配器
            val lv_member: ListView = findViewById(R.id.lv_member)
            lv_member.adapter=groupMember_adapter


            var exitGroup: Button = findViewById<Button>(R.id.exitGroup)
            exitGroup.setOnClickListener{
                val user: SharedPreferences = this.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
                var current_username = user.getString("username", "")
                val dbHelper = MySqliteHelper(this, "chatHub", 2)
                val db = dbHelper.writableDatabase
                val whereClause = "groupID = ? AND userID = ?" // 设置条件语句
                val whereArgs = arrayOf(onlyId.toString(), current_username) // 设置条件值
                val deletedRows = db.delete("GroupMember", whereClause, whereArgs)

                val whereClause1 = "userId = ? AND friendId = ? AND onlyId=?" // 设置条件语句
                val whereArgs1 = arrayOf(current_username, userNichen,onlyId.toString()) // 设置条件值
                val deletedRows1 = db.delete("user_friend", whereClause1, whereArgs1)

                if (deletedRows > 0 && deletedRows1 > 0) {
                    val intent = Intent(this,MainActivity::class.java)
                    intent.putExtra("jump_who","please jump MessageFragment");
                    startActivity(intent)
                    Toast.makeText(this,"退出成功",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this,"退出失败",Toast.LENGTH_SHORT).show()
                }
            }


            var pull_people: Button = findViewById<Button>(R.id.pull_people)
            pull_people.setOnClickListener{
                val intent = Intent(this,PullPeople::class.java)
                intent.putExtra("GroupId",onlyId);
                intent.putExtra("userNichen",userNichen);
                startActivity(intent)
            }
        }

    }
    fun InitGroupMember(onlyId: String){
        //初始化群成员
        val dbHelper = MySqliteHelper(this, "chatHub", 2)
        val db = dbHelper.readableDatabase
        val selection = "groupID = ? "
        val selectionArgs = arrayOf(onlyId)
        val cursor = db.query("GroupMember", arrayOf("userID"), selection, selectionArgs, null, null, null)
        while (cursor.moveToNext()) {
//             isCreate

            val userID = cursor.getString(cursor.getColumnIndex("userID"))
            Memberlist.add(userID)
        }
        cursor.close()
        db.close()
    }

}
