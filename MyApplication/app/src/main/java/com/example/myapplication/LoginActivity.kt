package com.example.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import android.text.*
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.exit.BaseActivity
import com.example.myapplication.model.User
import java.security.MessageDigest


class LoginActivity : BaseActivity() , TextWatcher ,View.OnClickListener{
    var flag = false;
    var eyeflag = false;
    override fun onClick(v: View) {
        when(v.id) {
            R.id.forget_password->{
                //跳到忘记密码界面
                val intent = Intent(this,ForgetPassword::class.java)
                startActivity(intent);
            }
            R.id.btn_reg->{
                val intent = Intent(this,Register::class.java)
                startActivity(intent);
            }
//            R.id.wx_photo->{
//                Toast.makeText(this,"微信", Toast.LENGTH_SHORT).show()
//            }
//            R.id.qq_photo->{
//                Toast.makeText(this,"QQ", Toast.LENGTH_SHORT).show()
//            }
            R.id.kb_photo->{
                val username: EditText = findViewById(R.id.username)
                var str_username:String = username.text.toString()
                if(!str_username.isEmpty()){
                    username.setText("")
                }
            }
            R.id.kb_photo1->{
                val password: EditText = findViewById(R.id.password)
                var str_password:String = password.text.toString()
                if(!str_password.isEmpty()){
                    password.setText("")
                }
            }
            R.id.eyes->{
                var eyes: ImageView =findViewById(R.id.eyes)
                var password:EditText = findViewById(R.id.password)
                if(!eyeflag){
                    eyes.setImageResource(R.drawable.eyes)
                    eyeflag = true;
                    //转为明文
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    eyes.setImageResource(R.drawable.close_eys)
                    eyeflag = false;
                    //转为密文
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
            R.id.userServiceTerms->{
                val intent = Intent(this,UserServiceTerms::class.java)
                startActivity(intent);
            }
            R.id.privacyAgreement->{
                val intent = Intent(this,PrivacyAgreement::class.java)
                startActivity(intent);
            }
        }
    }
    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val username: EditText = findViewById(R.id.username)
        val str_username:String = username.text.toString()
        val kb_photo: ImageView =findViewById(R.id.kb_photo)
        val regex = "\\S*".toRegex()
        if(str_username.matches(regex)){
            kb_photo.setImageResource(R.drawable.cha)
        }
        if(str_username.isEmpty()){
            kb_photo.setImageResource(R.drawable.kb)
        }
        //以下操作跟上面一样
        val password: EditText = findViewById(R.id.password)
        val str_password:String = password.text.toString()
        val kb_photo1: ImageView =findViewById(R.id.kb_photo1)
        val regex1 = "\\S*".toRegex()
        println(str_password)
        if(str_password.matches(regex1)){
            kb_photo1.setImageResource(R.drawable.cha)
        }
        if(str_password.isEmpty()){
            kb_photo1.setImageResource(R.drawable.kb)
        }
        //如果账号和密码都写了，执行以下逻辑
        if(str_username.isNotEmpty() ){
            if(str_password.isNotEmpty()) {
               // btn_login.setBackgroundColor(Color.argb(180, 0,0,255)) //深蓝色
                updateColorAndRadius(180,0,0,255)
                flag = true;
            }
        }
        //账号和密码只要有一个为空
        if(str_username.isEmpty() || str_password.isEmpty()){
            //btn_login.setBackgroundColor(Color.argb(41,0,0,100)) //浅蓝色
            updateColorAndRadius(41,0,0,100)
            flag = false;
        }
    }
    //更新按钮颜色和边角
    fun updateColorAndRadius(a:Int,red:Int,green:Int,blue:Int){
        val btn_login: Button =findViewById(R.id.btn_login)
        val gradientDrawable = GradientDrawable()
       // gradientDrawable.setShape(GradientDrawable.RECTANGLE);//形状
        gradientDrawable.setCornerRadius(100f);//设置圆角Radius
        gradientDrawable.setColor(Color.argb(a,red,green,blue));//颜色
        btn_login.setBackground(gradientDrawable)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btn_login: Button =findViewById(R.id.btn_login)
        //先初始化登录按钮的背景颜色，不要在布局文件加android:background进行设置，直接在这里设置
       // btn_login.setBackgroundColor(Color.argb(41,0,0,100)) //setBackgroundColor会影响shape失效
        updateColorAndRadius(41,0,0,100)
        btn_login.setOnClickListener(this)

        val username:EditText = findViewById(R.id.username)
        username.addTextChangedListener(this)
        val password:EditText = findViewById(R.id.password)
        password.addTextChangedListener(this)


        val forget_password: TextView =findViewById(R.id.forget_password)
        forget_password.setOnClickListener(this)

//        val wx_photo: ImageView = findViewById(R.id.wx_photo)
//        wx_photo.setOnClickListener(this)
//
//        val qq_photo: ImageView = findViewById(R.id.qq_photo)
//        qq_photo.setOnClickListener(this)

        val kb_photo: ImageView = findViewById(R.id.kb_photo)
        kb_photo.setOnClickListener(this)

        val eyes: ImageView = findViewById(R.id.eyes)
        eyes.setOnClickListener(this)

        val kb_photo1: ImageView = findViewById(R.id.kb_photo1)
        kb_photo1.setOnClickListener(this)

        val btn_reg: TextView =findViewById(R.id.btn_reg)
        btn_reg.setOnClickListener(this)




        //获取用户条款
        val userServiceTerms:TextView = findViewById(R.id.userServiceTerms)
        userServiceTerms.setOnClickListener(this)
        userServiceTerms.setText("<<用户服务条款>>")

        //获取隐私协议
        val privacyAgreement:TextView = findViewById(R.id.privacyAgreement)
        privacyAgreement.setText("<<隐私协议>>")
        privacyAgreement.setOnClickListener(this)


        val checkBox2: CheckBox =findViewById(R.id.checkBox2)

        //记住密码功能代码
/*       Sharedpreferences是Android平台上一个轻量级的存储类，
        用来保存应用程序的各种配置信息，其本质是一个以“键-值”对的方式保存数据的xml文件，其文件保存在/data/data//shared_prefs目录下。*/
        val user: SharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        val isRemember:Boolean = user.getBoolean("remember_password",false)
        val remember: CheckBox = findViewById(R.id.checkBox1);//获取checkbox
        if(isRemember){//检查remember_password是true还是false，如果是true表示用户上一次点击了记住密码
            username.setText(user.getString("username",""))
            password.setText(user.getString("password",""))
            remember.setChecked(true); //让checkbox选中
        }
        btn_login.setOnClickListener{
                if (flag) {
                    if(checkBox2.isChecked){
                        var str_username:String = username.text.toString()
                        var str_password:String = password.text.toString()
                        //模拟查询数据库
                        val find = findUser(str_username, str_password)

                        if(find){//找到了
                            var editor = user.edit()
                            if (remember.isChecked()) {//判断是否选中记住密码选项
                                editor.putBoolean("remember_password", true);

                            } else {
                                //editor.clear();
                                editor.putBoolean("remember_password", false);
                            }
                            editor.putString("username", username.text.toString());
                            editor.putString("password", password.text.toString());
                            editor.apply();
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent);
                            Toast.makeText(this,"登录成功", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this,"账号或密码错误", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this,"请先勾选同意 用户协议、隐私协议", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    //模拟数据库
//    fun initData():ArrayList<User>{
//        val userlist=ArrayList<User>()
//        userlist.add(
//            User(
//                "xiaochen","1234"
//            )
//        )
//        userlist.add(
//            User(
//                "小明","abc@123"
//            )
//        )
//        userlist.add(
//            User(
//                "小王","Abc1234"
//            )
//        )
//        return userlist;
//    }
    fun findUser(username:String,password:String):Boolean{

        val dbHelper= MySqliteHelper(this,"chatHub",2)
        val db=dbHelper.writableDatabase
        val cursor = db.query("user",null,null,null,null,null,null)
        if(cursor.moveToFirst())
        {
            do{
                val name = cursor.getString(cursor.getColumnIndex("username"))
                if(username==name)
                {
                    val passWord = cursor.getString(cursor.getColumnIndex("password"))
                    val salt = cursor.getString(cursor.getColumnIndex("salt"))
                    val hashedPassword = hashPassword(password, salt);
                    if(passWord==hashedPassword){
                        return true;
                    }
                }
            }while (cursor.moveToNext())
        }
        cursor.close()
        return false
    }
    fun hashPassword(password: String, salt: String): String {
        val bytes = (password + salt).toByteArray()
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

}
