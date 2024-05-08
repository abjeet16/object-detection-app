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
import com.example.itemdetection.databinding.FragmentForgotPasswordBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordFragment : Fragment() {
    private lateinit var binding:FragmentForgotPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.forgotbackButton.setOnClickListener(){
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
        }
        binding.RecoverPasswordButton.setOnClickListener(){
                v:View->
            val EmailInString = binding.ForgotEmail.text.toString().trim()
            if(EmailInString.isEmpty())
                Toast.makeText(requireContext(), "Enter Email", Toast.LENGTH_SHORT).show()
            else{
                //recovering
                firebaseAuth.sendPasswordResetEmail(EmailInString).addOnCompleteListener(){
                        task : Task<Void> ->
                    if(task.isSuccessful){
                        Toast.makeText(requireContext(), "Email send", Toast.LENGTH_SHORT).show()

                        findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
                    }else
                        Toast.makeText(requireContext(), "Failed Try Again", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return binding.root
    }
}