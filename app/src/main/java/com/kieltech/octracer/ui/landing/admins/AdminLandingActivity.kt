package com.kieltech.octracer.ui.landing.admins

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kieltech.octracer.R
import com.kieltech.octracer.base.BaseActivity
import com.kieltech.octracer.databinding.ActivityAdminLandingBinding
import com.kieltech.octracer.utils.Utils

class AdminLandingActivity : BaseActivity<ActivityAdminLandingBinding>(ActivityAdminLandingBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.exampleText.text = getAdminUser().toString()
    }
}