package com.rolo.homework.pages.base

import android.os.Bundle

abstract class PaneView<T : OnPageInteractionListener.Pane> : InteractionView<T>() {

    companion object {
        val ARG_CONTAINER = "com.jkopay.homework.pages.base.ARG_CONTAINER";
    }

    protected fun switchPage(page: Int, args: Bundle, addToBackStack: Boolean, withAnimation: Boolean = true) {
        var container = arguments?.getInt(ARG_CONTAINER, 0)
        container?.let { getInteractionListener().switchPage(it, page, args, addToBackStack, withAnimation) }
    }

    protected fun addPage(page: Int, args: Bundle, addToBackStack: Boolean, withAnimation: Boolean = true) {
        var container = arguments?.getInt(ARG_CONTAINER, 0)
        container?.let { getInteractionListener().addPage(it, page, args, addToBackStack, withAnimation) }
    }

}