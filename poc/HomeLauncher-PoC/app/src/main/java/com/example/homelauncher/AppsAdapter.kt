package com.example.homelauncher

import android.content.ComponentName
import android.graphics.drawable.Drawable
import android.os.UserHandle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.homelauncher.databinding.ItemAppBinding

/** One launchable activity; the same app in a work profile is a separate entry (different [user]). */
data class AppEntry(
    val component: ComponentName,
    val user: UserHandle,
    val label: String,
    val icon: Drawable,
) {
    val key: String get() = "${component.flattenToShortString()}#${user.hashCode()}"
}

class AppsAdapter(
    private val onClick: (AppEntry, View) -> Unit,
    private val onLongClick: (AppEntry, View) -> Unit,
) : ListAdapter<AppEntry, AppsAdapter.Holder>(Diff) {

    class Holder(val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder(ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val app = getItem(position)
        with(holder.binding) {
            icon.setImageDrawable(app.icon)
            label.text = app.label
            root.contentDescription = app.label
            root.setOnClickListener { onClick(app, it) }
            root.setOnLongClickListener { onLongClick(app, it); true }
        }
    }

    private object Diff : DiffUtil.ItemCallback<AppEntry>() {
        override fun areItemsTheSame(old: AppEntry, new: AppEntry) = old.key == new.key
        override fun areContentsTheSame(old: AppEntry, new: AppEntry) = old.label == new.label
    }
}
