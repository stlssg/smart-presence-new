package it.polito.interdisciplinaryProjects2021.smartpresence

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ProActionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pro_action, container, false)
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val currentNumberOccupants = view.findViewById<TextView>(R.id.currentNumberOccupants)
//        val selectedHourlyOccupancy = view.findViewById<TextView>(R.id.selectedHourlyOccupancy)
//        val allHourlyOccupancy = view.findViewById<TextView>(R.id.allHourlyOccupancy)
//    }

}