package com.jsdisco.lilhelper.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.jsdisco.lilhelper.R
import com.jsdisco.lilhelper.data.local.models.SettingsIngredient

class SettingsAdapter(
    private var dataset: List<SettingsIngredient>,
    private val onIngCheckboxClick: (setting: SettingsIngredient) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.ItemViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<SettingsIngredient>) {
        dataset = list
        notifyDataSetChanged()
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val cbIng: CheckBox = view.findViewById(R.id.cb_list_settings)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_items_settings, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val setting = dataset[position]

        holder.cbIng.text = setting.si_name
        holder.cbIng.isChecked = setting.si_included

        holder.cbIng.setOnClickListener {
            onIngCheckboxClick(setting)
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

}