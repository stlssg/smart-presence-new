package it.polito.interdisciplinaryProjects2021.smartpresence

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater

class ManualCheckingFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manual_checking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        val checkIn = view.findViewById<Button>(R.id.checkIn)
        val checkOut = view.findViewById<Button>(R.id.checkOut)

        checkIn.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.checkInMessage), Toast.LENGTH_SHORT).show()
        }

        checkOut.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.checkOutMessage), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.configuration_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.configuration_button -> {
                findNavController().navigate(R.id.manualConfigurationFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}