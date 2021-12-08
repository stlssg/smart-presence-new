package it.polito.interdisciplinaryProjects2021.smartpresence

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class DeclarationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_declaration, container, false)
    }

    @SuppressLint("BatteryLife")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)

        val finishSignIn = view.findViewById<Button>(R.id.finishSignIn)
        finishSignIn.setOnClickListener{
            if (checkBox.isChecked) {
                findNavController().navigate(R.id.nav_introduction)
            } else {
                Toast.makeText(requireContext(), getString(R.string.plzSign), Toast.LENGTH_SHORT).show()
            }
        }

        val packageName = (activity as AppCompatActivity).packageName
        val manuallySettings = view.findViewById<TextView>(R.id.manuallySettings)
        manuallySettings.makeLinks(
            Pair(getString(R.string.pairDeclaration1), View.OnClickListener {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), getString(R.string.alreadyGrantAllTimePositioningPermission), Toast.LENGTH_SHORT).show()
                }
            }),
            Pair(getString(R.string.pairDeclaration2), View.OnClickListener {
                val pm = requireContext().applicationContext.getSystemService(POWER_SERVICE) as PowerManager
                if (pm.isIgnoringBatteryOptimizations(packageName)) {
                    Toast.makeText(requireContext(), getString(R.string.alreadyStopBatteryOptimization), Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:$packageName"))
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }))

    }

    override fun onResume() {
        super.onResume()

        (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.nav_view).visibility = View.GONE
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()

        (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.nav_view).visibility = View.VISIBLE
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    private fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        var startIndexOfLink = -1
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun updateDrawState(textPaint: TextPaint) {
                    textPaint.color = textPaint.linkColor
                    textPaint.isUnderlineText = true
                }

                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)

            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod =
            LinkMovementMethod.getInstance()
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

}