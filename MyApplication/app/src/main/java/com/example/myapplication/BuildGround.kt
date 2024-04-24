package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.adapter.LetterAdapter
import com.example.myapplication.adapter.MessageAdapter
import com.example.myapplication.adapter.SelectFriendsAdapter
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Friend


class BuildGround : AppCompatActivity() {

    val letterlist=ArrayList<String>()
    val friendlist=ArrayList<Friend>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_build_ground)

        InitLetter()
        InitFriends()

        val letter_adapter = LetterAdapter(this,R.layout.letter_list,letterlist)
        //给控件加适配器
        val lv_letter: ListView = findViewById(R.id.lv_letter)
        lv_letter.adapter=letter_adapter

        val determine:Button=findViewById(R.id.determine)
        val selectFriends_adapter = SelectFriendsAdapter(this,R.layout.friend_list,friendlist,determine)
        val lv_friend: ListView = findViewById(R.id.lv_friend)
        lv_friend.adapter=selectFriends_adapter

        lv_letter.onItemClickListener = AdapterView.OnItemClickListener { parent, view, _, _ ->
            // 获取被点击的项的视图
            val selectedItemView = view
            // 设置被点击项的背景颜色
            selectedItemView.setBackgroundColor(Color.parseColor("#8BC34A"))
            // 遍历所有项，重置其他项的背景颜色
            for (i in 0 until parent.childCount) {
                val item = parent.getChildAt(i)
                if (item != selectedItemView) {
                    item.setBackgroundColor(Color.TRANSPARENT) // 或者设置为默认颜色
                }
            }
            // 获取被点击的项的文本内容
           // val selectedItem = parent.getItemAtPosition(position).toString()
            // Toast.makeText(this, "你点击了 $selectedItem", Toast.LENGTH_SHORT).show()

        }

        determine.setOnClickListener{
            var backgroundColor = (determine.background as ColorDrawable).color
            val expectedColor = Color.parseColor("#FFA500")
            if (backgroundColor == expectedColor) {
                val selectListSize = selectFriends_adapter.getSelectListSize()
                if(selectListSize==1){
                    Toast.makeText(this, "请再选1人，才能创建群", Toast.LENGTH_SHORT).show()
                }else{
                    //开始创建群逻辑
                    // 创建一个 AlertDialog.Builder 对象
                    val builder = AlertDialog.Builder(this)
                    // 设置对话框的标题
                    builder.setTitle("请输入群名")
                    // 创建一个 EditText 用于文本输入
                    val input = EditText(this)
                    // 设置 EditText 在对话框中的布局参数
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    // 设置 EditText 的布局参数
                    input.layoutParams = layoutParams
                    // 将 EditText 添加到对话框中
                    builder.setView(input)
                    // 设置对话框的确认按钮
                    builder.setPositiveButton("确定") { _, _ ->
                        // 在这里处理确认按钮的点击事件
                        val text = input.text.toString()
                        // 可以在这里对输入的文本进行处理
                        // 例如，显示一个 Toast 提示用户输入的文本
                        //Toast.makeText(this, "你输入的文本是：$text", Toast.LENGTH_SHORT).show()
                        if(text.trim()!=""){
                            val dbHelper= MySqliteHelper(this,"chatHub",2)
                            val db=dbHelper.writableDatabase
                            //核心代码
                            //新增群记录
                            val values1 = ContentValues().apply {
                                put("groupName",text)
                            }
                            val groupId =  db.insert("MyGroup",null,values1)
                            if (groupId != -1L) {
                                // 插入成功，groupId 是自动生成的行 ID
                                //Toast.makeText(this, "${groupId}", Toast.LENGTH_SHORT).show()
                                //新增群成员记录
                                var selectList = selectFriends_adapter.getSelectList()
                                for (item in selectList) {
                                    // 处理每个元素
                                    // Toast.makeText(this, "$item", Toast.LENGTH_SHORT).show()
                                    val values2 = ContentValues().apply {
                                        put("groupID",groupId)
                                        put("userID","$item")
                                        put("isCreate",0)
                                    }
                                    val values4 = ContentValues().apply {
                                        put("userId","$item")
                                        put("friendId",text)
                                        put("agree",1)
                                        put("is_interact",0)
                                        put("friendType",1)
                                        put("onlyId",groupId)
                                    }
                                    db.insert("GroupMember",null,values2)
                                    db.insert("user_friend",null,values4)
                                }
                                val userPrefs = this.getSharedPreferences("user", Context.MODE_PRIVATE)
                                val currentUsername = userPrefs.getString("username", "")
                                val values3 = ContentValues().apply {
                                    put("groupID",groupId)
                                    put("userID",currentUsername)
                                    put("isCreate",1)
                                }
                                val values5 = ContentValues().apply {
                                    put("userId",currentUsername)
                                    put("friendId",text)
                                    put("agree",1)
                                    put("is_interact",0)
                                    put("friendType",1)
                                    put("onlyId",groupId)
                                }
                                db.insert("GroupMember",null,values3)
                                db.insert("user_friend",null,values5)
                                Toast.makeText(this, "群创建成功", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, GroupChat::class.java)
                                startActivity(intent);
                               // Log.d("InsertData", "Inserted row ID: $groupId")
                                //dialog.dismiss() // 关闭对话框
                            } else {
                                // 插入失败
                                Toast.makeText(this, "创建失败，请联系客服", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this, "请输入群名", Toast.LENGTH_SHORT).show()
                        }
                    }
                    // 设置对话框的取消按钮
                    builder.setNegativeButton("取消") { dialog, _ ->
                        // 在这里处理取消按钮的点击事件
                        dialog.cancel() // 取消对话框
                    }
                    // 创建并显示对话框
                    val dialog = builder.create()
                    dialog.show()

                }
            }
        }
    }

    fun InitLetter(){
        letterlist.add("↑");
        letterlist.add("A");
        letterlist.add("B");
        letterlist.add("C");
        letterlist.add("D");
        letterlist.add("E");
        letterlist.add("F");
        letterlist.add("G");
        letterlist.add("H");
        letterlist.add("I");
        letterlist.add("J");
        letterlist.add("K");
        letterlist.add("L");
        letterlist.add("M");
        letterlist.add("N");
        letterlist.add("O");
        letterlist.add("P");
        letterlist.add("Q");
        letterlist.add("R");
        letterlist.add("S");
        letterlist.add("T");
        letterlist.add("U");
        letterlist.add("V");
        letterlist.add("W");
        letterlist.add("X");
        letterlist.add("Y");
        letterlist.add("Z");
        letterlist.add("#");
    }
    fun InitFriends(){
        val dbHelper= MySqliteHelper(this,"chatHub",2)
        val db=dbHelper.writableDatabase
        val cursor = db.query("user_friend",null,null,null,null,null,null)
        val userPrefs = this.getSharedPreferences("user", Context.MODE_PRIVATE)
        val currentUsername = userPrefs.getString("username", "")
        if(cursor.moveToFirst())
        {
            do{
                val id = cursor.getColumnIndex("id")
                val userName = cursor.getString(cursor.getColumnIndex("userId"))
                if(currentUsername==userName){
                    val friendName = cursor.getString(cursor.getColumnIndex("friendId"))
                    if(currentUsername != friendName ){
                        val agree = cursor.getInt(cursor.getColumnIndex("agree"))
                        val friendType = cursor.getInt(cursor.getColumnIndex("friendType"))
                        val onlyId = cursor.getInt(cursor.getColumnIndex("onlyId"))
                        val friendRemarks = cursor.getString(cursor.getColumnIndex("friendRemarks"))
                        val is_delete = cursor.getInt(cursor.getColumnIndex("is_delete"))
                        if(agree==1 && friendType==0 && is_delete == 0){
                            friendlist.add(
                                Friend(
                                    id,userName,friendName,"",R.drawable.xiaoai,friendType,onlyId,friendRemarks,is_delete,false
                                )
                            );
                        }
                    }
                }
            }while (cursor.moveToNext())
        }
        cursor.close()
    }
}
