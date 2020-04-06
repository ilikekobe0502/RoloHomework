package com.rolo.homework.pages.chat

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rolo.homework.R
import com.rolo.homework.repository.data.ContactContent
import kotlinx.android.synthetic.main.item_contacts_suggstion_content.view.*

class ContactsSuggestionRecyclerViewAdapter : RecyclerView.Adapter<ContactsSuggestionRecyclerViewAdapter.ViewHolder>() {
    private var keyword = ""

    val data: ArrayList<ContactContent> = ArrayList()
    var listener: View.OnClickListener? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view: View = LayoutInflater.from(p0.context).inflate(R.layout.item_contacts_suggstion_content, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return getData().size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        val item = getData()[p1]
        p0.itemView.apply {
            val context = p0.itemView.context
            textView_name.text = item.name
            textView_mail.text = item.email
            textView_person.text = item.name?.substring(0, 1)
            layout_suggestion.setOnClickListener {
                it.tag = item.id
                listener?.onClick(it)
            }
        }
    }

    private fun getData(): List<ContactContent> {
        return if (!TextUtils.isEmpty(keyword.trim())) this.data.filter {
            it.name!!.contains(keyword.trim(), ignoreCase = true)
        } else this.data
    }

    fun selectToContacts(id: Int): ContactContent? {
        var result: ContactContent? = null
        for (item in data) {
            if (item.id == id) {
                result = item
                data.remove(item)
                break
            }
        }

        notifyDataSetChanged()
        return result
    }

    fun addData(data: ContactContent) {
        this.data.add(data)
        notifyDataSetChanged()
    }

    fun filterData(s: String) {
        keyword = s
        notifyDataSetChanged()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {}
}