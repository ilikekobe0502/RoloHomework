package com.rolo.homework.pages

import android.os.Bundle
import com.rolo.homework.R
import com.rolo.homework.constants.Page
import com.rolo.homework.pages.base.OnPageInteractionListener
import com.rolo.homework.pages.base.PaneViewActivity

class MainActivity : PaneViewActivity(), OnPageInteractionListener.Primary {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        switchPage(R.id.fragment_container, Page.CHAT, Bundle(), true, false)
    }
}

