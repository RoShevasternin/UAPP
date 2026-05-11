package com.robuxe.robuxtracker.freerobux.Memee

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.robuxe.robuxtracker.freerobux.R

class RC_Adapter_Meme(
    private val activity: Activity,
    private val memeList: List<RC_Model_MemeItem>,
    private val onItemClickListener: (RC_Model_MemeItem) -> Unit
) : RecyclerView.Adapter<RC_Adapter_Meme.MemeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.rc_item_meme, parent, false)
        return MemeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        val model = memeList[position]

        holder.title.text = model.title
        holder.description.text = model.description

        holder.share.setOnClickListener { onItemClickListener(model) }
        holder.copy.setOnClickListener {
            copyText("${model.title}\n\n${model.description}")
        }
    }

    override fun getItemCount(): Int = memeList.size

    private fun copyText(text: String) {
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Meme", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(activity, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    class MemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val description: TextView = itemView.findViewById(R.id.tvDescription)
        val copy: ImageView = itemView.findViewById(R.id.copy)
        val share: ImageView = itemView.findViewById(R.id.share)
    }
}
