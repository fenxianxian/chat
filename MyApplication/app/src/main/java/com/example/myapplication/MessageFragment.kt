package com.example.myapplication

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.MessageAdapter
import com.example.myapplication.chat.Msg
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Friend
import kotlinx.android.synthetic.main.user_list.*

class MessageFragment : Fragment() {

    val friendlist=ArrayList<Friend>()
    private lateinit var lv_user: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_message_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val newFriend: LinearLayout = view.findViewById(R.id.newFriend)
        newFriend.setOnClickListener{
            val intent = Intent(getActivity(), HasNewFriendActivity::class.java)
            startActivityForResult(intent,1);
        };

        val receivedLike: LinearLayout = view.findViewById(R.id.receivedLike)
        receivedLike.setOnClickListener{
            val intent = Intent(getActivity(), ReceivedLikeActivity::class.java)
            startActivityForResult(intent,1);
        };


        //        lv_user.setOnClickListener {
//            Toast.makeText(getActivity(),"aa", Toast.LENGTH_SHORT).show()
//        }
//        lv_user.setOnItemClickListener { parent, view, position, id ->
//
////            // 获取 ListView 中指定位置的数据
//            val item = lv_user.adapter.getItem(position) as String
//            // 处理获取到的数据
//            handleItem(item)
//
//            // 处理用户点击事件
//           // handleItemClick(position)
//        }

           // hint.setVisibility(VISIBLE);
//        ================================================================================
//        在获取 ListView 中的数据对象时，通常我们需要将适配器中存储的数据转换为正确的类型。在该示例中，
//        数据类型是 User?，因此需要将其转换为 User 类型才能访问其属性。
//        以下是在 Kotlin 中获取 ListView 中的数据对象并访问其属性的示例代码：
//        在这个例子中，我们获取选定位置的数据对象并将其转换为 User 类型。然后，我们检查 user 是否为空，如果不为空，
//        则可以访问该用户的属性并将其作为参数传递给 handleUser() 方法。 请注意：由于数据可能为空，
//        因此在转换时使用了“as？”语法，这将结果强制转换为可空类型。如果数据为 null，则 as? 的结果也为 null。
        lv_user = view.findViewById(R.id.lv_user)
        lv_user.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            // 获取选定位置的数据对象，将其转换为 User 类型
            val friend = lv_user.adapter.getItem(position) as? Friend
           // var hasHint = friend?.hasHint ?: false

//            var hasHint = friend?.hasHint ?: false
//            if (!hasHint) {
//               //hint.visibility = View.VISIBLE
//                Log.d("aa",friend?.hasHint.toString())
//                friend?.hasHint = true
//            }
            if (friend != null) {
                // 访问该用户的属性
                val userName =  friend.friendRemarks
                val friendType = friend.friendType
                val onlyId = friend.onlyId
                val nichen = friend.friendId
                val is_delete = friend.is_delete
                // 处理获取到的用户属性
                handleUser(userName,friendType,onlyId,nichen,is_delete)
            }


        }
        //聊天窗口长按事件
        lv_user.setOnItemLongClickListener { _, _, position, _ ->
            val selectedItem =  lv_user.adapter.getItem(position) as? Friend
            displayLongPressToast(selectedItem)
            true // 返回 true 表示消费了长按事件
        }




//        ================================================================================

//        =
        //创建数据库连接
//        val dbHelper = activity?.let { MySqliteHelper(it,"chatHub",2) }
//        val db = dbHelper?.writableDatabase
////        // 在按钮上设置点击事件
////        val btn_createDB = view.findViewById<Button>(R.id.btn_createDB)
////        btn_createDB.setOnClickListener{
////            db
////        }
////        val dbHelper= MySqliteHelper(this,"chatHub",8)
////        val db=dbHelper.writableDatabase
//        btn_createDB.setOnClickListener{
//            db
//        }

//        insert_createDB.setOnClickListener{
//            val values = ContentValues().apply {
//                put("id", 3)
//                put("username","王九")
//                put("nickname","wang")
//                put("password","12345")
//                put("sex","男")
//                put("address","广东省深圳市福田区")
//                put("headPhoto",R.drawable.three)
//                put("signature","愿我是阳光，明媚而不忧伤")
//                put("regDate","")
//            }
//            db?.insert("user",null,values)
//        }
        //定义数据源
        initData()

        //定义一个适配器
        val friend_adapter = MessageAdapter(requireActivity(),R.layout.user_list,friendlist)
        //给控件加适配器
        lv_user.adapter=friend_adapter

    }
    private fun displayLongPressToast(item: Friend?) {
        //Toast.makeText(context, "Long pressed on ${item?.friendId}", Toast.LENGTH_SHORT).show()
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("提示")
//            .setMessage("是否删除聊天记录: $item?")
            .setMessage("是否删除与${item?.friendId}的聊天记录？")
            .setPositiveButton("删除") { _, _ ->
                // 在这里处理删除操作
                val dbHelper= MySqliteHelper(requireContext(),"chatHub",2)
                val db=dbHelper.writableDatabase
                val cursor = db.query("chat_history",null,null,null,null,null,null)
                val user: SharedPreferences = requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
                var current_username = user.getString("username", "")

                if(item?.friendType==0){
                    if(cursor.moveToFirst())
                    {
                        do{
                            // val triggeredAt = cursor.getColumnIndex("triggered_at")
                            val sender = cursor.getString(cursor.getColumnIndex("sender"))
                            val receiver = cursor.getString(cursor.getColumnIndex("receiver"))
                            if(current_username==sender){
                                // val receiver = cursor.getString(cursor.getColumnIndex("receiver"))
                                if("${item.friendId}"==receiver){
                                    //val s_show =cursor.getInt(cursor.getColumnIndex("s_show"))
                                    //把s_show置为1
                                    val values = ContentValues()
                                    values.put("s_show", 1)
                                    val whereClause = "sender = ? AND receiver = ?" // 设置条件语句
                                    val whereArgs = arrayOf(current_username, "${item.friendId}") // 设置条件值
                                    db.update(
                                        "chat_history",
                                        values,
                                        whereClause,
                                        whereArgs
                                    )
                                }
                            }
                            if(current_username==receiver){
                                if("${item.friendId}"==sender){
                                    //val r_show =cursor.getInt(cursor.getColumnIndex("r_show"))
                                    //把r_show置为1
                                    val values = ContentValues()
                                    values.put("r_show", 1)
                                    val whereClause = "sender = ? AND receiver = ?" // 设置条件语句
                                    val whereArgs = arrayOf("${item.friendId}", current_username) // 设置条件值
                                    db.update(
                                        "chat_history",
                                        values,
                                        whereClause,
                                        whereArgs
                                    )
                                }
                            }
                        }while (cursor.moveToNext())
                    }
                }else if(item?.friendType==1){
                    //  不能这么搞。。。。。。。。
//                    if(cursor.moveToFirst())
//                    {
//                        do{
//                            val receiver = cursor.getString(cursor.getColumnIndex("receiver"))
//                            if("${item?.friendId}"==receiver){
//                                //val s_show =cursor.getInt(cursor.getColumnIndex("s_show"))
//                                //把s_show置为1
//                                val values = ContentValues()
//                                values.put("s_show", 1)
//                                values.put("r_show", 1)
//                                val whereClause = "receiver = ?" // 设置条件语句
//                                val whereArgs = arrayOf("${item?.friendId}") // 设置条件值
//                                db.update(
//                                    "chat_history",
//                                    values,
//                                    whereClause,
//                                    whereArgs
//                                )
//                            }
//                        }while (cursor.moveToNext())
                    //}
                }
                cursor.close()
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
                val intent = Intent(context,MainActivity::class.java)
                intent.putExtra("jump_who","please jump MessageFragment");
                startActivity(intent)
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
    fun initData() {
        val dbHelper= MySqliteHelper(requireActivity(),"chatHub",2)
        val db=dbHelper.writableDatabase
        val cursor = db.query("user_friend",null,null,null,null,null,null)
//        val user: SharedPreferences = getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
//        var current_username = user.getString("username", "")

        val userPrefs = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
        val currentUsername = userPrefs.getString("username", "")
        if(cursor.moveToFirst())
        {
            do{
                val id = cursor.getColumnIndex("id")
                val userName = cursor.getString(cursor.getColumnIndex("userId"))
                if(currentUsername==userName){
                    val friendName = cursor.getString(cursor.getColumnIndex("friendId"))
                    if(friendName != currentUsername) {
                        val agree = cursor.getInt(cursor.getColumnIndex("agree"))
                        val friendType = cursor.getInt(cursor.getColumnIndex("friendType"))
                        val onlyId = cursor.getInt(cursor.getColumnIndex("onlyId"))
                        val friendRemarks = cursor.getString(cursor.getColumnIndex("friendRemarks"))
                        val is_delete = cursor.getInt(cursor.getColumnIndex("is_delete"))
                        if (agree == 1) {
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
            }while (cursor.moveToNext())
        }
        cursor.close()
    }
    /**
     * 处理用户点击事件
     */
//    private fun handleItemClick(position: Int) {
//       Toast.makeText(getActivity(),"aa",Toast.LENGTH_SHORT).show()
//    }
//
//    /**
//     * 处理获取到的数据
//     */
//    private fun handleItem(item: String) {
//        Toast.makeText(getActivity(),item,Toast.LENGTH_SHORT).show()
//    }
    /**
     * 处理获取到的用户属性
     */
    private fun handleUser(name: String?,friendType:Int,onlyId:Int, nichen: String?,is_delete:Int) {
        // Toast.makeText(getActivity(),name,Toast.LENGTH_SHORT).show()
        //跳到聊天界面
        val intent = Intent(getActivity(), ChatActivity::class.java)
        intent.putExtra("friend_name",name);
        intent.putExtra("friend_type",friendType);
        intent.putExtra("onlyId",onlyId);
        intent.putExtra("nichen",nichen);
        intent.putExtra("is_delete", is_delete)
        intent.putExtra("requestCode", 1)
        startActivityForResult(intent,1);
    }

}
