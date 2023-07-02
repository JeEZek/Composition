package com.example.composition.presentation

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.Level

class GameFragment : Fragment() {

    private val args by navArgs<GameFragmentArgs>()

    private val viewModelFactory by lazy {
        GameViewModelFactory(args.level, requireActivity().application)
    }

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
    }

    private val tvOptions by lazy {
        with(binding) {
            mutableListOf<TextView>().apply {
                add(tvOption1)
                add(tvOption2)
                add(tvOption3)
                add(tvOption4)
                add(tvOption5)
                add(tvOption6)
            }
        }
    }

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding = null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chooseAnswerClickListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getColorByState(goodState: Boolean): Int {
        val colorResId = if (goodState) {
            android.R.color.holo_green_light
        } else {
            android.R.color.holo_red_light
        }
        return ContextCompat.getColor(requireContext(), colorResId)
    }

    private fun observeViewModel() {
        with(viewModel) {
            with(binding) {
                time.observe(viewLifecycleOwner) {
                    tvTimer.text = it
                }
                question.observe(viewLifecycleOwner) {
                    tvQuestion.text = it.sum.toString()
                    tvNumberLeft.text = it.visibleNumber.toString()
                    for (i in 0 until tvOptions.size) {
                        tvOptions[i].text = it.options[i].toString()
                    }
                }
                percentOfRightAnswers.observe(viewLifecycleOwner) {
                    pbAnswers.setProgress(it, true)
                }
                progressAnswers.observe(viewLifecycleOwner) {
                    tvAnswersProgress.text = it
                }
                enoughCount.observe(viewLifecycleOwner) {
                    val color = getColorByState(it)
                    tvAnswersProgress.setTextColor(color)
                }
                enoughPercent.observe(viewLifecycleOwner) {
                    val color = getColorByState(it)
                    pbAnswers.progressTintList = ColorStateList.valueOf(color)
                }
                minPercent.observe(viewLifecycleOwner) {
                    pbAnswers.secondaryProgress = it
                }
                gameResult.observe(viewLifecycleOwner) {
                    launchGameFinishedFragment(it)
                }
            }
        }
    }

    private fun chooseAnswerClickListeners() {
        for (tvOption in tvOptions) {
            tvOption.setOnClickListener {
                viewModel.chooseAnswer(tvOption.text.toString().toInt())
            }
        }
    }

    private fun launchGameFinishedFragment(gameResult: GameResult) {
        findNavController().navigate(
            GameFragmentDirections.actionGameFragmentToGameFinishedFragment(gameResult)
        )
    }
}