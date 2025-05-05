package com.example.playlistmaker.utils

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.IdlingResource

class RecyclerViewItemCountIdlingResource(
    private val recyclerView: RecyclerView,
    private val minCount: Int
) : IdlingResource {

    @Volatile
    private var callback: IdlingResource.ResourceCallback? = null

    private val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            checkIdle()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            checkIdle()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            checkIdle()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            checkIdle()
        }
    }

    override fun getName(): String = "RecyclerViewItemCountIdlingResource"

    override fun isIdleNow(): Boolean {
        val idle = (recyclerView.adapter?.itemCount ?: 0) >= minCount
        if (idle) callback?.onTransitionToIdle()
        return idle
    }

    override fun registerIdleTransitionCallback(cb: IdlingResource.ResourceCallback?) {
        callback = cb
        recyclerView.adapter?.registerAdapterDataObserver(adapterDataObserver)
        checkIdle()
    }

    private fun checkIdle() {
        if (isIdleNow) {
            recyclerView.adapter?.unregisterAdapterDataObserver(adapterDataObserver)
        }
    }
}
