import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Comment

class CommentAdapter(private var dynamicID: Int?, private val context: Context, private val items: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    var number = 0;

    // 创建ViewHolder
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var user_name: TextView = itemView.findViewById(R.id.username)
        var username_comment: TextView = itemView.findViewById(R.id.username_comment)
    }

    // 更新动态ID
// 更新动态ID
    fun updateDynamicID(newDynamicID: Int?) {
        val oldDynamicID = dynamicID
        dynamicID = newDynamicID
        if (oldDynamicID != newDynamicID) {
            // 找到旧动态ID对应的位置
            val oldPosition = items.indexOfFirst { it.dynamicID == oldDynamicID }
            if (oldPosition != -1) {
                // 通知旧位置的评论项发生了变化
                notifyItemChanged(oldPosition)
            }
            // 找到新动态ID对应的位置
            val newPosition = items.indexOfFirst { it.dynamicID == newDynamicID }
            if (newPosition != -1) {
                // 通知新位置的评论项发生了变化
                notifyItemChanged(newPosition)
            }
        }
    }


    // 创建新的ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_comment, parent, false)
        return ViewHolder(view)
    }

    // 将数据绑定到ViewHolder
    // 将数据绑定到ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        if (dynamicID == item.dynamicID) {
            holder.user_name.text = item.userID
            holder.username_comment.text = item.message
            holder.itemView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            holder.itemView.visibility = View.VISIBLE // 显示评论项
        } else {
            // 清空不需要显示的项的文本内容
            holder.user_name.text = ""
            holder.username_comment.text = ""
            // 使用布局参数设置高度为0，让隐藏的项不占据空间
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            holder.itemView.visibility = View.GONE // 隐藏评论项
        }
    }


    // 返回数据项数量
    override fun getItemCount(): Int {
        return items.size
    }


}
