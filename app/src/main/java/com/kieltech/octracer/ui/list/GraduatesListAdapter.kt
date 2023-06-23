package com.kieltech.octracer.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.databinding.LayoutGraduatesListItemBinding
import com.kieltech.octracer.ui.landing.admins.AdminLandingActivity
import com.kieltech.octracer.ui.verification.VerificationListener
import com.kieltech.octracer.utils.OCTracerFunctions.disabled
import com.kieltech.octracer.utils.OCTracerFunctions.visibleOrGone

class GraduatesListAdapter(
    val adminLandingActivity: AdminLandingActivity,
    val graduates: List<Graduate>,
    val hint: String?,
    val showVerificationButtons: Boolean = false,
    val verificationListener: VerificationListener? = null,
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
        holder.bind(item, position)
    }

    inner class GraduatesListViewHolder(val binding: LayoutGraduatesListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(item: Graduate, position: Int) {
            val graduateId = item.id
            with(binding) {
                if (!graduateId.isNullOrBlank()) {
                    adminLandingActivity.setProfileImageView(
                        graduateId,
                        binding.profilePicImageView
                    )
                }
                verificationButtonsLinearLayout.visibleOrGone(showVerificationButtons)
                fullNameTextView.text = item.fullName()
                root.setOnClickListener {
                    //Go to profile fragment
                    adminLandingActivity.setGraduateProfileFragment(item)
                }
                if (position == graduates.size) {
                    // Last item
                    root.setContentPadding(0, 0, 0, 0)
                }
                //set click listeners
                verifyButton.setOnClickListener {
                    declineButton.disabled()
                    verifyButton.disabled()
                    verificationListener?.onVerify(item.id ?: "")
                }
                declineButton.setOnClickListener {
                    verifyButton.disabled()
                    declineButton.disabled()
                    verificationListener?.onDecline(item.id ?: "")
                }
            }
        }
    }
}