package hku.cs.lifearchive

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import hku.cs.lifearchive.databinding.FragmentListViewEntryBinding
import hku.cs.lifearchive.diaryentrymodel.DiaryEntry
import java.text.SimpleDateFormat


/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */



class MyItemRecyclerViewAdapter(
    private val values: List<DiaryEntry>,
    private var context1: FragmentActivity?,


    ) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    
        return ViewHolder(
            FragmentListViewEntryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }
    override fun getItemCount(): Int = values.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        fun goToDetails(view: View, item: DiaryEntry) {
            val intent = Intent(context1, EntryDetailsActivity::class.java)
            intent.putExtra("id", item.id)
            intent.putExtra("title", item.title)
            context1?.startActivity(intent)
        }

        holder.idView.text = item.title
        //holder.contentView.text = item.content
        holder.dateView.text = SimpleDateFormat("dd/MM/yyyy hh:mm").format(item.date)
        holder.detailview.setOnClickListener {
            goToDetails(it, item)
        }
        holder.card.setOnClickListener {
            goToDetails(it, item)
        }

        println("picturePaths: ${item.picturePaths}")
        if (item.picturePaths.isNotEmpty()) {
            val photoUri = item.picturePaths[0]
            holder.photoView.setImageURI(Uri.parse(photoUri))
            holder.photoView.visibility = View.VISIBLE
        }
    }
    inner class ViewHolder(binding: FragmentListViewEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        //val contentView: TextView = binding.content
        val dateView: TextView = binding.date
        val detailview: Button = binding.entryViewBtn
        val photoView: ImageView = binding.photoView
        val card: LinearLayout = binding.root
    }




}