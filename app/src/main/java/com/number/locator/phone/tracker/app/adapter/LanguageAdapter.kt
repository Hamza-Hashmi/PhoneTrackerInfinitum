package com.number.locator.phone.tracker.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.number.locator.phone.tracker.app.model.LanguageModel
import com.number.locator.phone.tracker.app.utills.AppPreferences
import com.phone.tracker.locate.number.app.R
import com.phone.tracker.locate.number.app.databinding.LanguageRowBinding
import java.util.*

class LanguageAdapter(private var langList: MutableList<LanguageModel>, private var onLanguageClick: (String) -> Unit) : RecyclerView.Adapter<LanguageAdapter.MyViewHolder>() {
    var isSelected = -1
    var selectedLangName: String = Locale.getDefault().displayName
    var languageCode: String = Locale.getDefault().language


    inner class MyViewHolder(private val binding: LanguageRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: LanguageModel) {
            binding.name.text = model.name

            if (isSelected != -1) {
                if (adapterPosition == isSelected) {
                    binding.radioBtn.setImageResource(R.drawable.ic_chk)
                } else {
                    binding.radioBtn.setImageResource(R.drawable.ic_un_chk)
                }
            } else {
                if (AppPreferences(binding.root.context).getString("langCode", "en") == model.code) {
                    binding.radioBtn.setImageResource(R.drawable.ic_chk)
                } else {
                    binding.radioBtn.setImageResource(R.drawable.ic_un_chk)
                }
            }

            itemView.setOnClickListener {
                /*isFirst = false
                onLanguageClick.invoke(model.code)
                currentLang = model.code
                // Notify previous selected and newly selected items
                if (previousSelected != -1) {
                    notifyItemChanged(previousSelected)
                }
                notifyItemChanged(adapterPosition)*/

                onLanguageClick.invoke(model.code)
                isSelected = adapterPosition
                selectedLangName = model.name
                languageCode = model.code
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LanguageRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(langList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return langList.size
    }

}