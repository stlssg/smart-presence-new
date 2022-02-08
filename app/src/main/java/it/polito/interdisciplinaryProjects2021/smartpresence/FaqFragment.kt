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

        backLinearLayout = view.findViewById(R.id.backLinearLayout)

        // this is not clever but it works

        // 1
        view.findViewById<CardView>(R.id.card1).setOnClickListener {
            dealWithExpandableView(view.findViewById(R.id.arrowBtn1), view.findViewById(R.id.ans1))
        }
        view.findViewById<ImageButton>(R.id.arrowBtn1).setOnClickListener {
            dealWithExpandableView(it as ImageButton, view.findViewById(R.id.ans1))
        }

        // 2
        view.findViewById<CardView>(R.id.card2).setOnClickListener {
            dealWithExpandableView(view.findViewById(R.id.arrowBtn2), view.findViewById(R.id.ans2))
        }
        view.findViewById<ImageButton>(R.id.arrowBtn2).setOnClickListener {
            dealWithExpandableView(it as ImageButton, view.findViewById(R.id.ans2))
        }

        // 3
        view.findViewById<CardView>(R.id.card3).setOnClickListener {
            dealWithExpandableView(view.findViewById(R.id.arrowBtn3), view.findViewById(R.id.ans3))
        }
        view.findViewById<ImageButton>(R.id.arrowBtn3).setOnClickListener {
            dealWithExpandableView(it as ImageButton, view.findViewById(R.id.ans3))
        }

        // 4
        view.findViewById<CardView>(R.id.card4).setOnClickListener {
            dealWithExpandableView(view.findViewById(R.id.arrowBtn4), view.findViewById(R.id.ans4))
        }
        view.findViewById<ImageButton>(R.id.arrowBtn4).setOnClickListener {
            dealWithExpandableView(it as ImageButton, view.findViewById(R.id.ans4))
        }

        // 5
        view.findViewById<CardView>(R.id.card5).setOnClickListener {
            dealWithExpandableView(view.findViewById(R.id.arrowBtn5), view.findViewById(R.id.ans5))
        }
        view.findViewById<ImageButton>(R.id.arrowBtn5).setOnClickListener {
            dealWithExpandableView(it as ImageButton, view.findViewById(R.id.ans5))
        }

        // 6
        view.findViewById<CardView>(R.id.card6).setOnClickListener {
            dealWithExpandableView(view.findViewById(R.id.arrowBtn6), view.findViewById(R.id.ans6))
        }
        view.findViewById<ImageButton>(R.id.arrowBtn6).setOnClickListener {
            dealWithExpandableView(it as ImageButton, view.findViewById(R.id.ans6))
        }

        // 7
        view.findViewById<CardView>(R.id.card7).setOnClickListener {
            dealWithExpandableView(view.findViewById(R.id.arrowBtn7), view.findViewById(R.id.ans7))
        }
        view.findViewById<ImageButton>(R.id.arrowBtn7).setOnClickListener {
            dealWithExpandableView(it as ImageButton, view.findViewById(R.id.ans7))
        }

        // 8
        view.findViewById<CardView>(R.id.card8).setOnClickListener {
            dealWithExpandableView(view.findViewById(R.id.arrowBtn8), view.findViewById(R.id.ans8))
        }
        view.findViewById<ImageButton>(R.id.arrowBtn8).setOnClickListener {
            dealWithExpandableView(it as ImageButton, view.findViewById(R.id.ans8))
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