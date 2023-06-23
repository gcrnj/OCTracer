package com.kieltech.octracer.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.databinding.LayoutGraduatesListItemBinding
import com.kieltech.octracer.ui.landing.admins.AdminLandingActivity

class GraduatesListAdapter(
    val adminLandingActivity: AdminLandingActivity,
    val graduates: List<Graduate>,
    val hint: String?
) :
    RecyclerView.Adapter<GraduatesListAdapter.GraduatesListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GraduatesListViewHolder {
        return GraduatesListViewHolder(
            LayoutGraduatesListItemBinding.inflate(
                LayoutInflater.from(adminLandingActivity)
            )
        )
    }

    override fun getItemCount(): Int {
        return graduates.size
    }

    override fun onBindViewHolder(holder: GraduatesListViewHolder, position: Int) {
        val item = graduates[position]
        holder.bind(item)
    }

    inner class GraduatesListViewHolder(val binding: LayoutGraduatesListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Graduate) {
            with(binding) {
                fullNameTextView.text = item.fullName()
                root.setOnClickListener {
                    //Go to profile fragment
                    adminLandingActivity.setGraduateProfileFragment(item)
                }
            }

        }
    }
}