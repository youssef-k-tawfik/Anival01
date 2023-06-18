package com.example.anival01.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.anival01.databinding.FragmentWelcomeScreenBinding


class WelcomeScreenFragment : Fragment() {

    private lateinit var b: FragmentWelcomeScreenBinding
    private lateinit var stroke0: AppCompatImageView
    private lateinit var stroke1: AppCompatImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentWelcomeScreenBinding.inflate(layoutInflater)

        stroke0 = b.strokeBa4a0
        stroke1 = b.strokeBa4a1

        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.btnGetStarted.setOnClickListener {
            findNavController().navigate(com.example.anival01.R.id.action_welcomeScreenFragment_to_loginFragment2)
        }

        //animation
        animateImageView(stroke0, 0)
        stroke1.postDelayed({ animateImageView(stroke1, 1500) }, 1500)
    }

    private fun animateImageView(view: View, delay: Long) {
        view.visibility = View.VISIBLE

        val scaleAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 2f)
        scaleAnimatorX.duration = 1000
        scaleAnimatorX.interpolator = AccelerateDecelerateInterpolator()

        val scaleAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 2f)
        scaleAnimatorY.duration = 1000
        scaleAnimatorY.interpolator = AccelerateDecelerateInterpolator()

        val alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
        alphaAnimator.duration = 1000

        val resetAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", 1f)
        resetAnimatorX.duration = 0

        val resetAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", 1f)
        resetAnimatorY.duration = 0

        val resetAnimatorAlpha = ObjectAnimator.ofFloat(view, "alpha", 1f)
        resetAnimatorAlpha.duration = 0

        val animatorSet = AnimatorSet()
        animatorSet.play(scaleAnimatorX).with(scaleAnimatorY).with(alphaAnimator)
        animatorSet.play(resetAnimatorX).with(resetAnimatorY).with(resetAnimatorAlpha)
            .after(alphaAnimator)
        animatorSet.startDelay = delay
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                animatorSet.start()
            }
        })
        animatorSet.start()
    }

}