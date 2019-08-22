package com.dac.roombooking.view.fragmenr


import android.annotation.SuppressLint
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dac.roombooking.R
import kotlinx.android.synthetic.main.done_layout_fragment.view.*

class DoneFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.done_layout_fragment, container, false)
        if (arguments != null) {
            view.success_text_2.text = "date : ${arguments!!.getString("date")} \n " +
                    "time : ${arguments!!.getString("time")} \n" +
                    "please check mail and confirm Rooming \n" +
                    "thanks booking_app team"
        }


        (view.success_image.drawable as Animatable).start()
        view.btn.setOnClickListener {
            activity!!.finish()
        }

        return view
    }


}
