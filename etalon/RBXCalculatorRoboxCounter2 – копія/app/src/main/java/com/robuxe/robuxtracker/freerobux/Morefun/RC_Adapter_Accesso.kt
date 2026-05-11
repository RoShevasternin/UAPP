package com.robuxe.robuxtracker.freerobux.Morefun

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.robuxe.robuxtracker.freerobux.R
import com.robuxe.robuxtracker.freerobux.Utils.RC_UtilsClass

class RC_Adapter_Accesso(
    private val context: Activity,
    private val emoteList: List<RC_Model_DataCommon>
) : RecyclerView.Adapter<RC_Adapter_Accesso.MapViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rc_item_accesso, parent, false)
        return MapViewHolder(view)
    }

    override fun onBindViewHolder(holder: MapViewHolder, position: Int) {
        val model = emoteList[position]
        holder.ivData.setImageResource(model.adapterImage)

        holder.ivData.setOnClickListener {
            val intent = Intent(context, RC_DataDetail_Activity::class.java)
            intent.putExtra("name", model.name)
            intent.putExtra("image", model.imageRes)
            intent.putExtra("desc", model.descRes)
            intent.putExtra("padding", model.topPadding)
            RC_UtilsClass.startSpecialActivity(context, intent, true)
        }
    }

    override fun getItemCount(): Int = emoteList.size

    class MapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivData: ImageView = itemView.findViewById(R.id.ivData)
    }
}
