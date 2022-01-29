package it.polito.interdisciplinaryProjects2021.smartpresence

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager

class FaqFragment : Fragment() {

    private lateinit var backLinearLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_faq, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backLinearLayout = view.findViewById<LinearLayout>(R.id.backLinearLayout)

        view.findViewById<ImageButton>(R.id.arrowBtn1).setOnClickListener {
            dealWithExpandableView(it as ImageButton, view.findViewById<TextView>(R.id.ans1))
        }

        view.findViewById<ImageButton>(R.id.arrowBtn2).setOnClickListener {
            dealWithExpandableView(it as ImageButton, view.findViewById<TextView>(R.id.ans2))
        }

        view.findViewById<ImageButton>(R.id.arrowBtn3).setOnClickListener {
            dealWithExpandableView(it as ImageButton, view.findViewById<TextView>(R.id.ans3))
        }

    }

    private fun dealWithExpandableView(btn: ImageButton, view: TextView) {
        TransitionManager.beginDelayedTransition(backLinearLayout, AutoTransition())
        if (view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
            btn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
        } else {
            view.visibility = View.GONE
            btn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
        }
    }

}