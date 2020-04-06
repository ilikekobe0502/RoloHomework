package com.rolo.homework.pages.chat

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.rolo.homework.R
import com.rolo.homework.repository.data.ContactContent
import kotlinx.android.synthetic.main.item_contact_input.view.*
import kotlinx.android.synthetic.main.item_contacts_content.view.*

class ContactsRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val data: ArrayList<ContactContent> = ArrayList()
    var listener: View.OnClickListener? = null
    var editorActionListener: TextView.OnEditorActionListener? = null
    var focusListener: View.OnFocusChangeListener? = null
    var textWatcherListener: TextWatcher? = null

    enum class Type {
        NORMAL,
        INOUT
    }

    init {
        this.data.add(ContactContent(-100, "", ""))
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return if (p1 == Type.NORMAL.ordinal) {
            val view = LayoutInflater.from(p0.context).inflate(R.layout.item_contacts_content, p0, false)
            NormalViewHolder(view)

        } else {
            val view = LayoutInflater.from(p0.context).inflate(R.layout.item_contact_input, p0, false)
            InputViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == data.size - 1) Type.INOUT.ordinal else Type.NORMAL.ordinal
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {

        if (p0 is NormalViewHolder) {
            val item = data[p1]
            p0.itemView.apply {
                val context = p0.itemView.context
                textView_name.text = if (!TextUtils.isEmpty(item.name)) item.name else item.email
                textView_person.text = textView_name.text.trimStart().substring(0, 1)
                imageView_remove.setOnClickListener {
                    it.tag = item.id
                    listener?.onClick(it)
                }
            }
        } else {
            p0.itemView.apply {
                editText_input.setOnFocusChangeListener { v, hasFocus ->
                    focusListener?.onFocusChange(v, hasFocus)
                }
                editText_input.setOnEditorActionListener { v, keyCode, event ->
                    editorActionListener?.onEditorAction(v, keyCode, event)
                    return@setOnEditorActionListener false
                }
                editText_input.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        textWatcherListener?.afterTextChanged(s)
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        textWatcherListener?.beforeTextChanged(s, start, count, after)
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        textWatcherListener?.onTextChanged(s, start, before, count)
                    }

                })
            }
        }
    }

    fun addData(data: ContactContent): Boolean {
        this.data.add(this.data.size - 1, data)
        notifyDataSetChanged()
        return true
    }

    fun removeSelected(id: Int): ContactContent? {
        var result: ContactContent? = null
        for (item in data) {
            if (item.id == id) {
                data.remove(item)
                result = item
                break
            }
        }

        notifyDataSetChanged()
        return result
    }

    fun contactIsEmpty(): Boolean {
        return data.size <= 1
    }

    class NormalViewHolder(v: View) : RecyclerView.ViewHolder(v) {}

    class InputViewHolder(v: View) : RecyclerView.ViewHolder(v) {}
}