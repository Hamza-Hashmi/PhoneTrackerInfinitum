package com.phone.tracker.locate.number.app.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.phone.tracker.locate.number.app.databinding.RateusLayoutBinding
import com.number.locator.phone.tracker.app.utills.AppPreferences
import com.phone.tracker.locate.number.app.utills.Constants.isFromBackPress
import com.number.locator.phone.tracker.app.utills.rateUs
import com.number.locator.phone.tracker.app.utills.sendMail
import org.koin.android.ext.android.inject

class RateUsDialoge() : DialogFragment() {

    companion object{
        fun showRatingDialoge(): RateUsDialoge{
            return RateUsDialoge()
        }
    }

    var binding : RateusLayoutBinding? = null

    val appPreferences : AppPreferences by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RateusLayoutBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setLayout(layoutParams.width,layoutParams.height)

        initEvents()
    }

    private fun initEvents() {
        binding?.apply {
            cancel.setOnClickListener {

                if (isFromBackPress){
                    appPreferences.putBoolean("ratingCheck",true)
                    activity?.finishAffinity()
                }else{
                    dialog?.dismiss()
                }

            }
            rateUsBtn.setOnClickListener {
                when{
                    ratingBar2.rating>4 -> {
                        appPreferences.putBoolean("ratingCheck",true)
                        context?.rateUs()
                        dismiss()
                    }
                    else -> {
                        activity?.sendMail()
                        dismiss()
                    }
                }
            }
            ratingBar2.setOnRatingBarChangeListener { ratingBar, _, _ ->
                val buttonText = when {
                    ratingBar.rating <= 4 -> {
                        "Feedback"
                    }
                    else -> {
                        "Rate Us"
                    }
                }
                rateUsBtn.text = buttonText
            }

        }
    }


}