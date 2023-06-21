package com.example.anival01.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.example.anival01.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    private lateinit var stroke0: AppCompatImageView
    private lateinit var stroke1: AppCompatImageView
    private var isRepeatEnabled = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        Timber.plant(Timber.DebugTree())
        stroke0 = b.strokeBa4a0
        stroke1 = b.strokeBa4a1
        // strokes animation
        animateImageView(stroke0, 0)
        stroke1.postDelayed({ animateImageView(stroke1, 1500) }, 1500)

        b.btnGetStarted.setOnClickListener {
            isRepeatEnabled = false

//            val valueAnimator = ValueAnimator.ofInt(120, -450)
//            valueAnimator.addUpdateListener { animator ->
//                val layoutParams = b.imgAnivalLogo.layoutParams as ViewGroup.MarginLayoutParams
//                layoutParams.topMargin = animator.animatedValue as Int
//                b.imgAnivalLogo.layoutParams = layoutParams
//            }
//            valueAnimator.duration = 500 // Set the duration of the animation in milliseconds
//            valueAnimator.start()

        }

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
        animatorSet.startDelay = delay
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (isRepeatEnabled) animatorSet.start()
            }
        })

        animatorSet.start()
    }

}