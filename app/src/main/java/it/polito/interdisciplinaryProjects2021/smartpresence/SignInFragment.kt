package it.polito.interdisciplinaryProjects2021.smartpresence

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.interdisciplinaryProjects2021.smartpresence.R
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class SignInFragment : Fragment() {

    companion object {
        private const val RC_SIGN_IN = 100
        private const val LOCATION_REQUEST_CODE = 666
    }

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_REQUEST_CODE)
        }

//        val mAuth = FirebaseAuth.getInstance()
//        val user = mAuth.currentUser
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)
        val logInOrNot = sharedPreferences.getString("logInOrNot", "false").toBoolean()
        if (logInOrNot){
            findNavController().navigate(R.id.nav_introduction)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("892318613323-qr7gmle90ur1qh9g52laui0so1583tak.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val googleSignInButton = view.findViewById<SignInButton>(R.id.google_sign_in)
        googleSignInButton.setOnClickListener {
            signIn()
        }

        val signInButton = view.findViewById<TextView>(R.id.signInButton)
        val logIn = view.findViewById<TextView>(R.id.logInButton)
        val signUp = view.findViewById<TextView>(R.id.singUpButton)
        val logInLayout = view.findViewById<LinearLayout>(R.id.logInLayout)
        val signUpLayout = view.findViewById<LinearLayout>(R.id.signUpLayout)
        signUp.setOnClickListener {
            signUp.background = resources.getDrawable(R.drawable.switch_trcks,null)
            signUp.setTextColor(resources.getColor(R.color.textColor,null))
            logIn.background = null
            signUpLayout.visibility = View.VISIBLE
            logInLayout.visibility = View.GONE
            logIn.setTextColor(resources.getColor(R.color.light_blue_900,null))
            signInButton.text = getString(R.string.LOGINPAGE_log_in_and_sign_up_title)
        }
        logIn.setOnClickListener {
            signUp.background = null
            signUp.setTextColor(resources.getColor(R.color.light_blue_900,null))
            logIn.background = resources.getDrawable(R.drawable.switch_trcks,null)
            signUpLayout.visibility = View.GONE
            logInLayout.visibility = View.VISIBLE
            logIn.setTextColor(resources.getColor(R.color.textColor,null))
            signInButton.text = getString(R.string.LOGINPAGE_log_in_title)
        }

        val db = Firebase.firestore
        val registeredUserCollection = db.collection("RegisteredUser")
        signInButton.setOnClickListener {
            when (signInButton.text) {
                getString(R.string.LOGINPAGE_log_in_and_sign_up_title) -> {
                    val emailAsUserNameSetting = view.findViewById<TextInputLayout>(R.id.emailAsUserNameSetting)
                    val passwordSetting = view.findViewById<TextInputLayout>(R.id.passwordSetting)
                    val passwordConfirmSetting = view.findViewById<TextInputLayout>(R.id.passwordConfirmSetting)

                    val emailAsUserNameSettingInput = emailAsUserNameSetting.editText?.text.toString()
                    val passwordSettingInput = passwordSetting.editText?.text.toString()
                    val passwordConfirmSettingInput = passwordConfirmSetting.editText?.text.toString()

                    if (emailAsUserNameSettingInput == "" || passwordSettingInput == "" || passwordConfirmSettingInput == "") {
                        Toast.makeText(requireContext(), getString(R.string.configuration_empty_input_message), Toast.LENGTH_SHORT).show()
                    } else {
                        if (!isEmailValid(emailAsUserNameSettingInput)) {
                            Toast.makeText(requireContext(), getString(R.string.notEmailMessage), Toast.LENGTH_SHORT).show()
                        } else {
                            registeredUserCollection
                                .whereEqualTo("email", emailAsUserNameSettingInput)
                                .get()
                                .addOnSuccessListener { documents ->
                                    var num_ducuments = 0
                                    for (document in documents) {
                                        num_ducuments ++
                                    }
                                    if (num_ducuments != 0) {
                                        Toast.makeText(requireContext(), getString(R.string.userExist), Toast.LENGTH_SHORT).show()
                                    } else {
                                        if (passwordSettingInput != passwordConfirmSettingInput) {
                                            Toast.makeText(requireContext(), getString(R.string.passwordSettingNotSame), Toast.LENGTH_SHORT).show()
                                        } else {
                                            val input = hashMapOf("email" to emailAsUserNameSettingInput, "password" to passwordSettingInput)
                                            registeredUserCollection.add(input)
                                                .addOnSuccessListener { _ ->
                                                    writeSharedPreferences(emailAsUserNameSettingInput)
                                                    Toast.makeText(requireContext(), getString(R.string.logInAndRegisteredSuccess), Toast.LENGTH_SHORT).show()
                                                    findNavController().navigate(R.id.declarationFragment)
                                                }
                                                .addOnFailureListener { _ ->
                                                    Toast.makeText(requireContext(), getString(R.string.logInUnsuccess), Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    }
                                }
                                .addOnFailureListener { _ ->
                                    Toast.makeText(requireContext(), getString(R.string.logInUnsuccess), Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
                getString(R.string.LOGINPAGE_log_in_title) -> {
                    val emailAsUserNameEnter = view.findViewById<TextInputLayout>(R.id.emailAsUserNameEnter)
                    val passwordEnter = view.findViewById<TextInputLayout>(R.id.passwordEnter)

                    val emailAsUserNameEnterInput = emailAsUserNameEnter.editText?.text.toString()
                    val passwordEnterInput = passwordEnter.editText?.text.toString()

                    if (emailAsUserNameEnterInput == "" || passwordEnterInput == "") {
                        Toast.makeText(requireContext(), getString(R.string.configuration_empty_input_message), Toast.LENGTH_SHORT).show()
                    } else {
                        registeredUserCollection
                            .whereEqualTo("email", emailAsUserNameEnterInput)
                            .get()
                            .addOnSuccessListener { documents ->
                                var num_ducuments = 0
                                    for (document in documents) {
                                        num_ducuments ++
                                        if (document.data["password"] != passwordEnterInput) {
                                            Toast.makeText(requireContext(), getString(R.string.passwordNotCorrect), Toast.LENGTH_SHORT).show()
                                        } else {
                                            writeSharedPreferences(emailAsUserNameEnterInput)
                                            Toast.makeText(requireContext(), getString(R.string.logInSuccess), Toast.LENGTH_SHORT).show()
                                            findNavController().navigate(R.id.declarationFragment)
                                        }
                                    }
                                    if (num_ducuments == 0) {
                                        Toast.makeText(requireContext(), getString(R.string.noSuchUser), Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener { _ ->
                                Toast.makeText(requireContext(), getString(R.string.logInUnsuccess), Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                else -> {
                    Toast.makeText(requireContext(), getString(R.string.logInUnsuccess), Toast.LENGTH_SHORT).show()
                }
            }
        }

//        val forgetPassword = view.findViewById<TextView>(R.id.forgetPassword)
//        forgetPassword.setOnClickListener {
//        }

    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful){
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("signInFragment", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.w("signInFragment", "Google sign in failed", e)
                }
            } else {
                Log.w("signInFragment", exception.toString())
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("signInFragment", "signInWithCredential:success")

                    val acc = GoogleSignIn.getLastSignedInAccount(requireContext())
                    if (acc != null) {
                        val currentEmail = acc.email.toString()
                        writeSharedPreferences(currentEmail)
                    }
                    findNavController().navigate(R.id.declarationFragment)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("signInFragment", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun writeSharedPreferences(email: String) {
        val sharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("logInOrNot", "true")
            putString( "keyCurrentAccount", email)
            putString( "setting_start_time", "07:00")
            putString( "setting_stop_time", "23:00")
            putString("workingIntervalSpinnerPosition", "0")
//            putString("languageSpinnerPosition", "0")
            putString("notificationOnOffCondition", "true")
            putString("ssid", "nothing")
            putString("bssid", "nothing")
            putString("address", "nothing")
            putString("maxOccupancy", "nothing")
            putString("latitude", "nothing")
            putString("longitude", "nothing")
            putString("energySavingMode", "nothing")
            putString("wifiCheckingStatus", "false")
            putString("positioningCheckingStatus", "false")
            putString("ssidConfigurationFinished", "false")
            putString("bssidConfigurationFinished", "false")
            putString("addressConfigurationFinished", "false")
            putString("maxOccupancyConfigurationFinished", "false")
//            putString("customizedLanguage", "false")
            putString("professionalOrNot", "false")
            putString("frequentNotificationOnOffCondition", "false")
            commit()
        }
    }

    override fun onResume() {
        super.onResume()

        (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.nav_view).visibility = GONE
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()

        (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.nav_view).visibility = VISIBLE
        (activity as AppCompatActivity).supportActionBar?.show()
    }

}