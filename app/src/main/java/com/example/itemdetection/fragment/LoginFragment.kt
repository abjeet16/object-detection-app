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
import com.example.itemdetection.databinding.FragmentLoginBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var binding:FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser =  firebaseAuth.currentUser

        if (firebaseUser != null) {
            findNavController().navigate(R.id.action_loginFragment_to_selectImageMainFragment)
        }

        binding.SignUPWindowClick.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signInFragment)
        }
        binding.forgotPasswordButton.setOnClickListener(){
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
        binding.LoginButton.setOnClickListener(){
            val EmailInString: String = binding.loginEmail.text.toString().trim()
            val passwordInString: String = binding.loginPassword.text.toString().trim()
            if(EmailInString.isEmpty()&&passwordInString.isEmpty())
                Toast.makeText(requireContext(), "Enter info", Toast.LENGTH_SHORT).show()
            else if (EmailInString.isEmpty())
                Toast.makeText(requireContext(), "Enter Email", Toast.LENGTH_SHORT).show()
            else if(passwordInString.isEmpty())
                Toast.makeText(requireContext(), "Enter password", Toast.LENGTH_SHORT).show()
            else{
                binding.progressBarLogin.visibility = View.VISIBLE
                // logging in
                firebaseAuth.signInWithEmailAndPassword(EmailInString,passwordInString).addOnCompleteListener(){
                        task: Task<AuthResult> ->
                    if(task.isSuccessful)
                        chickMailVerification()
                    else{
                        binding.progressBarLogin.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "account doesn't exist", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return binding.root
    }
    fun chickMailVerification() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            if (firebaseUser.isEmailVerified) {
                findNavController().navigate(R.id.action_loginFragment_to_selectImageMainFragment)
            } else {
                binding.progressBarLogin.visibility = View.INVISIBLE
                Toast.makeText(requireContext(), "verify your email", Toast.LENGTH_SHORT).show()
                firebaseAuth.signOut()
            }
        }
    }
}