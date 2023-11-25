package hku.cs.lifearchive

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hku.cs.lifearchive.databinding.FragmentItemBinding
import hku.cs.lifearchive.diaryentrymodel.DiaryEntry
import java.text.SimpleDateFormat


/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter(
    private val values: List<DiaryEntry>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    
        return ViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        holder.idView.text = item.title
        //holder.contentView.text = item.content
        holder.dateView.text = SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(item.date)


    }


    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        //val contentView: TextView = binding.content
        val dateView: TextView = binding.date


    }

}