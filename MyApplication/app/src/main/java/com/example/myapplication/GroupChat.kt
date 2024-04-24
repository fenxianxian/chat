package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.example.myapplication.adapter.GroupListAdapter
import com.example.myapplication.adapter.LetterAdapter
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Friend
import com.example.myapplication.model.Group
import com.example.myapplication.model.PostingUpdate
import kotlinx.android.synthetic.main.activity_group_chat.*

class GroupChat : AppCompatActivity() {

    val grouplist=ArrayList<Group>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        InitGroupList();

        val group_adapter = GroupListAdapter(this,R.layout.group_list,grouplist)
        //给控件加适配器
        val lv_group: ListView = findViewById(R.id.lv_group)
        lv_group.adapter=group_adapter

        val build_ground =this.findViewById<Button>(R.id.build_ground)
        build_ground.setOnClickListener{
            val intent = Intent(this,BuildGround::class.java)
            startActivity(intent)
        }

        lv_group.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            //Toast.makeText(this@YourActivity, "Item clicked at position $position", Toast.LENGTH_SHORT).show()
            val group = lv_group.adapter.getItem(position) as Group
            //Toast.makeText(this,groupName.toString(),Toast.LENGTH_SHORT).show()

                // 访问该用户的属性
                val userName = group.GroupName
                val GroupId = group.GroupId
                val friendType =1
                // 处理获取到的用户属性
                handleUser(userName,friendType,GroupId)

        }
    }
    private fun handleUser(name: String,friendType:Int,GroupId:Int) {
        // Toast.makeText(getActivity(),name,Toast.LENGTH_SHORT).show()
        //跳到聊天界面
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("friend_name",name);
        intent.putExtra("friend_type",friendType);
        intent.putExtra("onlyId",GroupId);
        intent.putExtra("nichen",name);
        intent.putExtra("requestCode", 3)
        intent.putExtra("is_delete", 0)
        startActivityForResult(intent,1);


    }
    fun InitGroupList(){
        val dbHelper= MySqliteHelper(this,"chatHub",2)
        val db=dbHelper.writableDatabase
        val userPrefs = this.getSharedPreferences("user", Context.MODE_PRIVATE)
        val currentUsername = userPrefs.getString("username", "")
        val query = """
            SELECT MyGroup.groupID,MyGroup.groupName 
            FROM MyGroup 
            JOIN GroupMember ON MyGroup.groupID = GroupMember.groupID 
            WHERE GroupMember.userID = '$currentUsername'
        """.trimIndent()
        val cursor: Cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val groupId = cursor.getInt(cursor.getColumnIndex("groupID"))
            val groupName = cursor.getString(cursor.getColumnIndex("groupName"))
            grouplist.add(
                Group(
                    groupId,groupName
                )
            )
        }
        cursor.close()
        db.close()
    }
}
