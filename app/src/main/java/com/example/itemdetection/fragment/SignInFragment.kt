package com.example.itemdetection.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.itemdetection.R
import com.example.itemdetection.databinding.FragmentSignInBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : Fragment() {

    private lateinit var binding:FragmentSignInBinding
    private lateinit var firebaseAuth:FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(layoutInflater)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.SignInbackButton.setOnClickListener() {
            findNavController().navigate(R.id.action_signInFragment_to_loginFragment)
        }

        binding.LoginWindowClick.setOnClickListener(){
            findNavController().navigate(R.id.action_signInFragment_to_loginFragment)
        }

        binding.SignButton.setOnClickListener() {
            val emailString: String = binding.SignUpEmail.text.toString().trim()
            val PasswordString = binding.SignUpPassword.text.toString().trim()
            if (emailString.isEmpty() && PasswordString.isEmpty())
                Toast.makeText(requireContext(), "Enter info", Toast.LENGTH_SHORT).show()
            else if (emailString.isEmpty())
                Toast.makeText(requireContext(), "enter email", Toast.LENGTH_SHORT).show()
            else if (PasswordString.isEmpty())
                Toast.makeText(requireContext(), "enter password", Toast.LENGTH_SHORT).show()
            else {
                // firebaseAuth means firebaseAuthentication
                firebaseAuth.createUserWithEmailAndPassword(emailString, PasswordString)
                    .addOnCompleteListener() {
                            task: Task<AuthResult> ->
                        if(task.isSuccessful){
                            Toast.makeText(requireContext(), "done", Toast.LENGTH_SHORT).show()
                            SendEmailVerification()
                        }else
                            Toast.makeText(requireContext(), "failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        return binding.root
    }
    private fun SendEmailVerification() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(){
                Toast.makeText(activity,"verify your email by clicking the link in your email",Toast.LENGTH_SHORT).show()
                firebaseAuth.signOut();
               // startActivity(Intent(this, MainActivity::class.java))
            }
        }else{
            Toast.makeText(activity, "Email verification failed", Toast.LENGTH_SHORT).show()
        }
    }
}