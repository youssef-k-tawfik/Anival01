package com.example.anival01.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.anival01.R
import com.example.anival01.databinding.FragmentWelcomeScreenBinding

class WelcomeScreenFragment : Fragment() {

    private lateinit var b: FragmentWelcomeScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentWelcomeScreenBinding.inflate(layoutInflater)

        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.btnGetStarted.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeScreenFragment_to_loginFragment2)
        }

    }
}