package com.rolo.homework.pages.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(), OnPageInteractionListener.Base {

    private val TAG = BaseActivity::class.simpleName

//    private var mFullScreenMessage: FullScreenMessage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun pressBack() {
        if (isActivityDestroying())
            return
        onBackPressed()
    }

    override fun showFullScreenLoading() {
    }

    override fun hideFullScreenOverlay() {
    }

    /*--------------------------------------------------------------------------------------------*/
    /* Internal helpers */
    fun isActivityDestroying(): Boolean {
        if (isFinishing)
            return true
        return false
    }
}