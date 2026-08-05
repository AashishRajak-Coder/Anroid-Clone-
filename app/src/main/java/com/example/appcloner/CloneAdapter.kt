package com.example.appcloner
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CloneAdapter(private val onAction: (Clone, Action) -> Unit) : RecyclerView.Adapter<CloneAdapter.ViewHolder>() {
    private var clones = listOf<Clone>()
    enum class Action { LAUNCH, DELETE, SETTINGS }
    fun submitList(list: List<Clone>) { clones = list; notifyDataSetChanged() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_clone, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clone = clones[position]
        holder.bind(clone)
        holder.btnLaunch.setOnClickListener { onAction(clone, Action.LAUNCH) }
        holder.btnDelete.setOnClickListener { onAction(clone, Action.DELETE) }
        holder.btnSettings.setOnClickListener { onAction(clone, Action.SETTINGS) }
    }
    override fun getItemCount() = clones.size
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvApp: TextView = view.findViewById(R.id.tvApp)
        val tvDevice: TextView = view.findViewById(R.id.tvDevice)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnLaunch: Button = view.findViewById(R.id.btnLaunch)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
        val btnSettings: Button = view.findViewById(R.id.btnSettings)
        fun bind(clone: Clone) {
            tvName.text = clone.cloneName
            tvApp.text = clone.appName
            tvDevice.text = "${clone.deviceProfile.manufacturer} ${clone.deviceProfile.model}"
            tvStatus.text = clone.status
            tvStatus.setTextColor(when (clone.status) { "RUNNING" -> Color.GREEN; "STOPPED" -> Color.RED; else -> Color.GRAY })
        }
    }
}
