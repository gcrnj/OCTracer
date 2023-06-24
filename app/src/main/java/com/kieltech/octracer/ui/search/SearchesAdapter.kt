package com.kieltech.octracer.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kieltech.octracer.base.BaseActivity
import com.kieltech.octracer.data.SpannableGraduate
import com.kieltech.octracer.databinding.LayoutSearchedGraduateItemBinding

class SearchesAdapter(
    private val baseActivity: BaseActivity<*>,
    private val spannableGraduateList: List<SpannableGraduate>,
    val onCLickListener: (SpannableGraduate) -> Unit
) : RecyclerView.Adapter<SearchesAdapter.SearchesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchesViewHolder {
        return SearchesViewHolder(
            LayoutSearchedGraduateItemBinding.inflate(
                LayoutInflater.from(baseActivity)
            )
        )
    }

    override fun getItemCount(): Int {
        return spannableGraduateList.size
    }

    override fun onBindViewHolder(holder: SearchesViewHolder, position: Int) {
        val item = spannableGraduateList[position]
        holder.bind(item)
    }

    inner class SearchesViewHolder(val binding: LayoutSearchedGraduateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SpannableGraduate) {
            with(binding) {
                fullNameTextView.text = item.nameSpannableString
                occupationTextView.text = item.occupationSpannableString
                root.setOnClickListener{
                    onCLickListener.invoke(item)
                }
            }
        }

    }
}