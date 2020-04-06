package com.rolo.homework.pages.chat

import android.animation.Animator
import android.animation.ValueAnimator
import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayoutManager
import com.rolo.homework.AppInjector
import com.rolo.homework.R
import com.rolo.homework.pages.base.InteractionView
import com.rolo.homework.pages.base.OnPageInteractionListener
import com.rolo.homework.repository.data.ContactContent
import com.rolo.homework.utils.MiscUtils
import kotlinx.android.synthetic.main.fragment_chat.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener


const val TAG_ONLY_EMAIL: Int = -99
const val TAG_MAX_SUBJECT: Int = 85
const val TAG_MAX_MESSAGE: Int = 5000

class ChatFragment : InteractionView<OnPageInteractionListener.Primary>(), View.OnClickListener, TextView.OnEditorActionListener, View.OnFocusChangeListener, View.OnTouchListener {

    private lateinit var mViewModel: ChatViewModel

    private val suggestionAdapter = ContactsSuggestionRecyclerViewAdapter()
    private val contactsAdapter = ContactsRecyclerViewAdapter()
    private var originalMessageFieldValue = 0
    private var isBig = false
    private var horizontalDownTime: Long = 0
    private var isMoving = false
    private var isSubjectFocus = false

    companion object {
        fun newInstance(): ChatFragment = ChatFragment()
        private val TAG = ChatFragment::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = AppInjector.obtainViewModel(this)
        mViewModel.contactsResult.observe(this, Observer {
            it?.let { data -> getContactsSuccess(data) }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView_contracts.let {
            it.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            it.adapter = suggestionAdapter
        }

        recyclerView_to_contracts_horizontal.let {
            it.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            it.adapter = contactsAdapter
            it.setOnTouchListener(this)
        }

        recyclerView_to_contracts.let {
            it.layoutManager = FlexboxLayoutManager(context)
            it.adapter = contactsAdapter
            it.setOnTouchListener(this)
        }

        textView_subject_count.text = getString(R.string.message_counter_hint, editText_subject.text.length, TAG_MAX_SUBJECT)
        textView_message_count.text = getString(R.string.message_counter_hint, editText_subject.text.length, TAG_MAX_MESSAGE)

        editText_message.onFocusChangeListener = this

        editText_subject.apply {
            setOnEditorActionListener(this@ChatFragment)
            onFocusChangeListener = this@ChatFragment
            imeOptions = EditorInfo.IME_ACTION_DONE
            setRawInputType(InputType.TYPE_CLASS_TEXT)
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val length = editText_subject.text.length
                    textView_subject_count.apply {
                        text = getString(R.string.message_counter_hint, length, TAG_MAX_SUBJECT)
                        if (TAG_MAX_SUBJECT - length < 10) {
                            setTextColor(Color.RED)
                        } else {
                            context?.let { setTextColor(ContextCompat.getColor(it, R.color.color_7C8494)) }
                        }
                    }

                    checkSend()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })
        }

        suggestionAdapter.listener = this
        contactsAdapter.apply {
            listener = this@ChatFragment
            focusListener = this@ChatFragment
            editorActionListener = this@ChatFragment
            textWatcherListener = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    suggestionAdapter.filterData(s.toString())
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

            }
        }

        imageView_leave_focus.setOnClickListener(this)
        imageView_scale.setOnClickListener(this)

        editText_message.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val length = editText_message.text.length
                textView_message_count.apply {
                    text = getString(R.string.message_counter_hint, length, TAG_MAX_MESSAGE)
                    if (TAG_MAX_MESSAGE - length < 100) {
                        setTextColor(Color.RED)
                    } else {
                        context?.let { setTextColor(ContextCompat.getColor(it, R.color.color_7C8494)) }
                    }
                }
                checkSend()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        frame_message.post {
            originalMessageFieldValue = frame_message.measuredHeight
        }
    }


    override fun onDetach() {
        mViewModel.destroy()
        super.onDetach()
    }

    override fun onStart() {
        super.onStart()
        activity?.let {
            setEventListener(it, object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) { // some code depending on keyboard visiblity status
                    if (!isOpen && isSubjectFocus) {
                        frame_message.visibility = View.VISIBLE
                    }
                }
            })
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.layout_suggestion -> {
                val id = v.tag as Int
                suggestionAdapter.selectToContacts(id)?.let { contactsAdapter.addData(it) }
                recyclerView_to_contracts.scrollToPosition(contactsAdapter.data.size - 1)
                focusToFieldEdit()
            }
            R.id.imageView_leave_focus -> {
                recyclerView_to_contracts.background = ContextCompat.getDrawable(recyclerView_to_contracts.context, R.drawable.rectangle_input_bg)
                switchToFieldOrientation(true)
                toFieldState(false)
                MiscUtils.hideSoftKeyboard(editText_message)
                moveToHint(contactsAdapter.data.size <= 1, true)
                checkSend()
            }
            R.id.imageView_remove -> {
                val id = v.tag as Int
                val item = contactsAdapter.removeSelected(id)
                if (id != TAG_ONLY_EMAIL)
                    item?.let { suggestionAdapter.addData(it) }
                if (recyclerView_to_contracts.isShown) {
                    recyclerView_to_contracts.scrollToPosition(contactsAdapter.data.size - 1)
                    focusToFieldEdit()
                }
                checkSend()

            }
            R.id.imageView_scale -> {
                scaleMessageField()
            }
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        when (v?.id) {
            R.id.editText_subject -> {
                if (event != null || actionId == EditorInfo.IME_ACTION_DONE) {
                    editText_subject.clearFocus()
                    frame_message.visibility = View.VISIBLE
                }
            }
            R.id.editText_input -> {
                if (!TextUtils.isEmpty(v.text.trim()) && actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!MiscUtils.isValidEmail(v.text.toString().trimStart().trimEnd())) {
                        context?.let {
                            AlertDialog.Builder(it)
                                    .setMessage(R.string.email_verify_error_message)
                                    .setPositiveButton("ok") { dialog, which ->
                                        toFieldState(true)
                                        focusToFieldEdit()
                                    }.show()
                        }
                    } else {
                        val email = v.text.toString()
                        contactsAdapter.addData(ContactContent(TAG_ONLY_EMAIL, "", email))
                        v.text = " "
                        v.requestFocus()
                        MiscUtils.showSoftKeyboard(v as EditText)
                        recyclerView_to_contracts.scrollToPosition(contactsAdapter.data.size - 1)
                    }
                    checkSend()
                    return true
                } else {
                    switchToFieldOrientation(true)
                    toFieldState(false)
                    moveToHint(contactsAdapter.data.size <= 1, true)
                    checkSend()
                }
            }
        }

        return false
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v?.id) {
            R.id.editText_input -> {
                recyclerView_to_contracts.background = ContextCompat.getDrawable(v.context, R.drawable.rectangle_input_focused_bg)
            }
            R.id.editText_subject -> {
                isSubjectFocus = hasFocus
                if (hasFocus)
                    frame_message.visibility = View.INVISIBLE
                //else frame_message.visibility = View.VISIBLE
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (v?.id) {
            R.id.recyclerView_to_contracts -> {
                if (!recyclerView_contracts.isShown) {
                    switchToFieldOrientation(false)
                    recyclerView_to_contracts.background = v.context?.let { ContextCompat.getDrawable(it, R.drawable.rectangle_input_focused_bg) }
                    toFieldState(true)
                    moveToHint(contactsAdapter.data.size <= 1)
                }
                focusToFieldEdit()

                return false
            }
            R.id.recyclerView_to_contracts_horizontal -> {
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    horizontalDownTime = System.currentTimeMillis()
                } else if (event?.action == MotionEvent.ACTION_UP) {
                    if (System.currentTimeMillis() - horizontalDownTime < 85) {
                        moveToHint(contactsAdapter.data.size <= 1)
                        switchToFieldOrientation(false)
                        toFieldState(true)
                        focusToFieldEdit()
                    }
                }
                return false
            }
        }
        return super.onTouch(v, event)
    }

    private fun getContactsSuccess(it: ArrayList<ContactContent>) {
        recyclerView_contracts.visibility = View.VISIBLE
        suggestionAdapter.data.clear()
        suggestionAdapter.data.addAll(it)
        suggestionAdapter.notifyDataSetChanged()
    }

    private fun hideContactsSuggestion() {
        recyclerView_contracts.visibility = View.GONE
    }

    private fun toFieldState(focus: Boolean) {
        if (focus) {
            layout_leave_focus.visibility = View.VISIBLE
            frame_message.visibility = View.GONE
            if (suggestionAdapter.data.size == 0)
                mViewModel.getContacts()
            else {
                recyclerView_contracts.visibility = View.VISIBLE
                suggestionAdapter.notifyDataSetChanged()
            }
            recyclerView_to_contracts.scrollToPosition(contactsAdapter.data.size - 1)
        } else {
            layout_leave_focus.visibility = View.GONE
            frame_message.visibility = View.VISIBLE
            hideContactsSuggestion()
        }
    }

    private fun checkSend() {
        if (TextUtils.isEmpty(editText_message.text) || TextUtils.isEmpty(editText_subject.text) || contactsAdapter.contactIsEmpty()) {
            imageView_send.background = ContextCompat.getDrawable(imageView_send.context, R.drawable.ic_send_disable_24dp)
        } else {
            imageView_send.background = ContextCompat.getDrawable(imageView_send.context, R.drawable.ic_send_enable_24dp)
        }
    }

    private fun scaleMessageField() {
        val targetValue = if (isBig) {
            originalMessageFieldValue
        } else {
            frame_message.measuredHeight + frame_message.y.toInt() - recyclerView_to_contracts.y.toInt()
        }
        isBig = !isBig
        val anim = ValueAnimator.ofInt(frame_message.measuredHeight, targetValue)
        anim.addUpdateListener { valueAnimator ->
            val value: Int = valueAnimator.animatedValue as Int
            val layoutParams: ConstraintLayout.LayoutParams = frame_message.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.height = value
            frame_message.layoutParams = layoutParams

            val editLayoutParams: ConstraintLayout.LayoutParams = editText_message.layoutParams as ConstraintLayout.LayoutParams
            editLayoutParams.height = value
            editText_message.layoutParams = editLayoutParams
        }
        anim.duration = 150
        anim.start()
    }

    private fun moveToHint(listEmpty: Boolean, moveBack: Boolean = false) {
        if (!listEmpty || isMoving) {
            return
        }

        val anim = if (moveBack) ValueAnimator.ofInt(8, (recyclerView_to_contracts_horizontal.measuredHeight - text_hint_label.measuredHeight) / 2) else ValueAnimator.ofInt(17, 8)
        val sizeAnim = if (moveBack) ValueAnimator.ofFloat(14f, 18f) else ValueAnimator.ofFloat(18f, 14f)

        anim.addUpdateListener { valueAnimator ->
            val value: Int = valueAnimator.animatedValue as Int
            val layoutParam: ConstraintLayout.LayoutParams = text_hint_label.layoutParams as ConstraintLayout.LayoutParams
            layoutParam.topMargin = value
            text_hint_label.layoutParams = layoutParam
        }
        sizeAnim.addUpdateListener { valueAnimator ->
            val value: Float = valueAnimator.animatedValue as Float
            text_hint_label.textSize = value
        }
        anim.duration = 150
        sizeAnim.duration = 150
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                isMoving = false
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {

                isMoving = true
            }

        })
        anim.start()
        sizeAnim.start()
    }

    private fun switchToFieldOrientation(horizontal: Boolean) {
        if (horizontal) {
            recyclerView_to_contracts_horizontal.visibility = View.VISIBLE
            recyclerView_to_contracts.visibility = View.INVISIBLE
            recyclerView_to_contracts.adapter = null
        } else {
            recyclerView_to_contracts_horizontal.visibility = View.INVISIBLE
            recyclerView_to_contracts.visibility = View.VISIBLE
            recyclerView_to_contracts.adapter = contactsAdapter
        }
    }

    private fun focusToFieldEdit() {
        recyclerView_to_contracts.post {
            var view = recyclerView_to_contracts.getChildAt(recyclerView_to_contracts.childCount - 1)
            if (view != null)
                view = (view as ViewGroup).getChildAt(0)
            if (view != null && view is EditText) {
                view.requestFocus()
                MiscUtils.showSoftKeyboard(view)
            }
        }
    }
}