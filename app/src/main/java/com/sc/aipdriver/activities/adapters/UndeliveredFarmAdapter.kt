package com.sc.aipdriver.activities.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sc.aipdriver.R
import com.sc.aipdriver.activities.models.UndeliveredFarmData

class UndeliveredFarmAdapter(
    private val context: Context,
    private val farmList: List<UndeliveredFarmData>,
    private val onSelectionChanged: (() -> Unit)? = null
) : RecyclerView.Adapter<UndeliveredFarmAdapter.FarmViewHolder>() {

    class FarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbSelect: CheckBox = itemView.findViewById(R.id.cb_farm_select)
        val tvFarmName: TextView = itemView.findViewById(R.id.tv_farm_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_undelivered_farm, parent, false)
        return FarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FarmViewHolder, position: Int) {
        val farm = farmList[position]
        holder.tvFarmName.text = farm.farmName ?: ""

        // Unbind listener before changing checked state to avoid listener loops
        holder.cbSelect.setOnCheckedChangeListener(null)
        holder.cbSelect.isChecked = farm.isSelected

        holder.cbSelect.setOnCheckedChangeListener { _, isChecked ->
            farm.isSelected = isChecked
            onSelectionChanged?.invoke()
        }

        holder.itemView.setOnClickListener {
            val newState = !farm.isSelected
            farm.isSelected = newState
            holder.cbSelect.isChecked = newState
            onSelectionChanged?.invoke()
        }
    }

    override fun getItemCount(): Int = farmList.size

    fun getSelectedFarms(): List<UndeliveredFarmData> {
        return farmList.filter { it.isSelected }
    }

    fun selectAll(select: Boolean) {
        farmList.forEach { it.isSelected = select }
        notifyDataSetChanged()
        onSelectionChanged?.invoke()
    }
}
