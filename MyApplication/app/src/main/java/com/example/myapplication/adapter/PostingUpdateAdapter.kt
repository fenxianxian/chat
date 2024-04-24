import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.FriendSay
import com.example.myapplication.R
import com.example.myapplication.UserServiceTerms

import com.example.myapplication.chat.Msg
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Comment
import com.example.myapplication.model.PostingUpdate


class PostingUpdateAdapter( activity: Activity, val resId: Int, data: List<PostingUpdate>) : ArrayAdapter<PostingUpdate>(activity, resId, data) {

    val dbHelper = MySqliteHelper(activity, "chatHub", 2)
    val db = dbHelper.writableDatabase
    private val commentList = ArrayList<Comment>()
   var flag = true


    //核心方法，将数据源转为view
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //convertView: 可以被复用的view，滑出屏幕老的view
        val myview: View
        // 为视图添加布局文件
        //优化：将convertView利用起来，如果convertView不为null，则直接使用，否则新建一个view
//        如果convertView为空，说明当前没有可重用的视图对象，需要通过LayoutInflater去创建一个新的视图对象，并将其设置为myview。
//        如果convertView不为空，说明有可重用的视图对象，直接将其赋值给myview，无需重新创建新的视图对象。
        if(convertView == null){
            myview = LayoutInflater.from(context).inflate(resId, parent, false)
        }else{
            myview = convertView
        }
        // 为框架添加具体的控件
        val userImage: ImageView = myview.findViewById(R.id.touxian)
        val user_nikeName: TextView = myview.findViewById(R.id.user_nikeName)
        val content: TextView = myview.findViewById(R.id.content)
        val send_date: TextView = myview.findViewById(R.id.send_date)
        val imgCamera: ImageView = myview.findViewById(R.id.imgCamera)
        val likes: ImageView = myview.findViewById(R.id.likes)
        val likesNumber: TextView = myview.findViewById(R.id.likesNumber)

        val commentIcon: ImageView = myview.findViewById(R.id.commentIcon)

        commentIcon.setOnClickListener {
            val comment_send: LinearLayout = myview.findViewById(R.id.comment_send)
            comment_send.visibility  = View.VISIBLE
        }



        // 为每一个控件加载数据
        val postingUpdate: PostingUpdate? = getItem(position) // 获取每一个控件的位置
//        val postingUpdate1: PostingUpdate = data[position]
        if (postingUpdate != null) {
            userImage.setImageResource(postingUpdate.headPhoto)
            user_nikeName.text = postingUpdate.userId
            content.text = postingUpdate.content
            send_date.text = postingUpdate.triggered_at

            // 请求访问相册权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val permission = Manifest.permission.READ_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context as Activity, arrayOf(permission), 1)
                }
            }
            if(postingUpdate.imgURL != ""){
//                        if(imgCamera!=null){
                            val newWidth = 200 // 新的宽度值
                            val newHeight = 200 // 新的高度值
                            val layoutParams = imgCamera.layoutParams
                            layoutParams.width = newWidth
                            layoutParams.height = newHeight
                            imgCamera.layoutParams = layoutParams
                        //}
            }else{
                //if(imgCamera!=null){
                    val newWidth = 0 // 新的宽度值
                    val newHeight = 0 // 新的高度值
                    val layoutParams = imgCamera.layoutParams
                    layoutParams.width = newWidth
                    layoutParams.height = newHeight
                    imgCamera.layoutParams = layoutParams
               // }
            }
            // 加载图片
            imgCamera.setImageURI(Uri.parse(postingUpdate.imgURL))

            //初始化点赞数
            InitLikeNumber(postingUpdate,likesNumber)

            likes.setImageResource(R.drawable.nolike)
            InitRedLike(postingUpdate,likes)



            val comment_button: Button = myview.findViewById(R.id.comment_button)
            comment_button.setOnClickListener{
                val commentEditText: EditText = myview.findViewById(R.id.commentEditText)
                val commentText = commentEditText.text.toString()
                val userPrefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
                val currentUsername = userPrefs.getString("username", "")
                if(commentText.trim()==""){
                    Toast.makeText(context,"不能发送空文本",Toast.LENGTH_SHORT).show()
                }else{
                    val currentTimestamp =System.currentTimeMillis()
                    val values = ContentValues().apply {
                        put("triggered_at",currentTimestamp)
                        put("dynamicID",postingUpdate?.say_id.toString())
                        put("userID",currentUsername)
                        put("message",commentText)
                    }
                    var rowEffect = db.insert("CommentTable", null, values)
                    if(rowEffect > 0 ){
                        val intent = Intent(context, FriendSay::class.java)
                        context.startActivity(intent);
                        Toast.makeText(context,"评论成功",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context,"评论失败！！",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        // 用户点击图片时调用放大图片的方法
        imgCamera.setOnClickListener {
            zoomImageFromUri(Uri.parse(postingUpdate?.imgURL))
        }


            val commentRecyclerView: RecyclerView = myview.findViewById(R.id.commentRecyclerView)
//        val adapter = CommentAdapter(context,commentRecyclerView, R.layout.activity_comment, commentList)
            commentRecyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = CommentAdapter(postingUpdate?.say_id?.toInt(),context,commentList)
            commentRecyclerView.adapter = adapter
        loadNewDynamicContent(adapter,postingUpdate?.say_id?.toInt())





        likes.setOnClickListener {

            //val currentResourceId = likes.drawable?.constantState?.resourceId ?: R.drawable.nolike

            //在这判断是点赞状态还是非点赞状态，如果是点赞状态，就让红色的手变成白色的手
            //如果是非点赞的状态，就让白色的手变成红色的手
//            val nolikeId = R.drawable.nolike
//            val likeId = R.drawable.like
//            Toast.makeText(context,nolikeId.toString(),Toast.LENGTH_SHORT).show()
            val resourceId = likes.drawable?.constantState?.toString()
            val userPrefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
            val currentUsername = userPrefs.getString("username", "")
            val dbHelper= MySqliteHelper(context,"chatHub",2)
            val db=dbHelper.writableDatabase


            if (resourceId == ContextCompat.getDrawable(context, R.drawable.like)?.constantState?.toString()) {

                val selection = "dynamicID = ? AND userID = ?"
                val selectionArgs1 = arrayOf(postingUpdate?.say_id.toString(),currentUsername)
                val deletedRows = db.delete("LikeTable", selection, selectionArgs1)
                if(deletedRows > 0 ){
                    likes.setImageResource(R.drawable.nolike)
                    InitLikeNumber(postingUpdate,likesNumber)
                    Toast.makeText(context,"已取消点赞",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context,"取消点赞失败",Toast.LENGTH_SHORT).show()
                }
            }
            // 如果点击的是 like 图像
            else if (resourceId == ContextCompat.getDrawable(context, R.drawable.nolike)?.constantState?.toString()) {
                val values = ContentValues().apply {
                    put("dynamicID",postingUpdate?.say_id.toString())
                    put("userID",currentUsername)
                }
                var rowEffect = db.insert("LikeTable", null, values)
                if(rowEffect > 0 ){
                    likes.setImageResource(R.drawable.like)
                    InitLikeNumber(postingUpdate,likesNumber)
                    Toast.makeText(context,"点赞成功",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context,"点赞失败！！",Toast.LENGTH_SHORT).show()
                }
            }

            //Toast.makeText(context,postingUpdate?.say_id.toString(),Toast.LENGTH_SHORT).show()


        }
        return myview
    }

    fun InitLikeNumber(postingUpdate:PostingUpdate?,likesNumber: TextView){

        val dbHelper= MySqliteHelper(context,"chatHub",2)
        val db=dbHelper.writableDatabase
        val selection = "dynamicID = ?"
        val selectionArgs = arrayOf(postingUpdate?.say_id.toString())
        val columns = arrayOf("COUNT(userID)")
        val cursor = db.query("LikeTable", columns, selection, selectionArgs, null, null, null)
        if(cursor != null && cursor.moveToFirst())
        {
            val count = cursor.getInt(0)
            likesNumber.text = count.toString()
        }
        cursor.close()
        db.close()
    }

    fun InitRedLike(postingUpdate:PostingUpdate?,likes: ImageView){

        val dbHelper= MySqliteHelper(context,"chatHub",2)
        val db=dbHelper.writableDatabase
        val userPrefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val currentUsername = userPrefs.getString("username", "")
        val selection = "dynamicID = ? AND userID = ?"
        val selectionArgs = arrayOf(postingUpdate?.say_id.toString(),currentUsername)
        val cursor = db.query("LikeTable", null, selection, selectionArgs, null, null, null)
        if(cursor != null && cursor.moveToFirst())
        {
            likes.setImageResource(R.drawable.like)
        }
        cursor.close()
        db.close()
    }

    private fun zoomImageFromUri(selectedImageUri: Uri) {
        // 获取选定图片的路径
        val imagePath: String? = getImagePath(selectedImageUri)

        // 如果路径为空，说明获取图片路径失败
        if (imagePath.isNullOrEmpty()) {
            Toast.makeText(context, "无法获取图片路径", Toast.LENGTH_SHORT).show()
            return
        }

        // 创建一个 Dialog
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_zoom_image)

        val imageView = dialog.findViewById<ImageView>(R.id.ivZoomedImage)

        // 设置点击 Dialog 区域外部可关闭 Dialog
        dialog.setCanceledOnTouchOutside(true)

        // 使用图片路径加载并设置放大后的图片
        val bitmap = BitmapFactory.decodeFile(imagePath)
        imageView.setImageBitmap(bitmap)

        // 设置图片点击事件，点击图片时关闭 Dialog
        imageView.setOnClickListener {
            dialog.dismiss()
        }

        // 显示 Dialog
        dialog.show()
    }

    // 根据 URI 获取图片路径
    private fun getImagePath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val imagePath = cursor?.getString(columnIndex ?: 0)
        cursor?.close()
        return imagePath
    }

    private fun loadNewDynamicContent(adapter: CommentAdapter, dynamicID: Int?) {
        // 检查动态ID是否为null
        if (dynamicID == null) {
            return
        }
        // 获取数据库实例
        val dbHelper = MySqliteHelper(context, "chatHub", 2)
        val db = dbHelper.writableDatabase
        // 查询数据库获取新的评论数据
        val selection = "dynamicID = ?"
        val selectionArgs = arrayOf(dynamicID.toString())
        val columns = arrayOf("triggered_at","dynamicID", "userID", "message")
        val cursor = db.query("CommentTable", columns, selection, selectionArgs, null, null, null)
        // 清空现有的评论列表数据
        commentList.clear()
        // 遍历查询结果并添加到评论列表中
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val triggered_at = cursor.getInt(cursor.getColumnIndex("triggered_at"))
                val dynamicID = cursor.getInt(cursor.getColumnIndex("dynamicID"))
                val userID = cursor.getString(cursor.getColumnIndex("userID"))
                val message = cursor.getString(cursor.getColumnIndex("message"))
                commentList.add(
                    Comment(
                        triggered_at,
                        dynamicID,
                        userID,
                        message
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor?.close()
        db.close()
        commentList.sortByDescending { it.id }
        // 通知适配器数据已更改
        adapter.notifyDataSetChanged()
    }

}