package com.kieltech.octracer.ui.home

import com.kieltech.octracer.data.Graduate

interface GetGraduateListener {

    fun onGetStart()

    fun onGetSuccess(graduates: List<Graduate>)

    fun onGetProcessDone()

}