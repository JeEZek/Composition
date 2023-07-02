package com.example.composition.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.composition.R
import com.example.composition.databinding.FragmentGameFinishedBinding
import com.example.composition.domain.entity.GameResult

class GameFinishedFragment : Fragment() {

    private val args by navArgs<GameFinishedFragmentArgs>()

    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding ?: throw RuntimeException("FragmentGameFinishedBinding = null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListeners()
        bindViews()
    }

    private fun bindViews() {
        val minCountOfRightAnswers = args.gameResult.gameSettings.minCountOfRightAnswers
        val countRightOfAnswers = args.gameResult.countOfRightAnswers
        val minPercentOfRightAnswers = args.gameResult.gameSettings.minPresentOfRightAnswers
        val countOfQuestions = args.gameResult.countOfQuestions
        val percentOfRightAnswers = getPercentOfRightAnswers(
            countRightOfAnswers,
            countOfQuestions
        )

        with(binding) {
            tvCountRightAnswer.text = String.format(
                getString(R.string.count_right_answer),
                countRightOfAnswers,
                minCountOfRightAnswers
            )
            tvPercentRightAnswers.text = String.format(
                getString(R.string.percent_right_answers),
                percentOfRightAnswers,
                minPercentOfRightAnswers
            )
            tvCountRightAnswer.setBackgroundColor(
                getColorCountRightAnswerResId(
                    countRightOfAnswers,
                    minCountOfRightAnswers
                )
            )
            tvPercentRightAnswers.setBackgroundColor(
                getColorPercentRightAnswerResId(
                    percentOfRightAnswers,
                    minPercentOfRightAnswers
                )
            )
        }
    }

    private fun getColorCountRightAnswerResId(
        countRightOfAnswers: Int,
        minCountOfRightAnswers: Int
    ): Int {
        val colorResId = if (countRightOfAnswers >= minCountOfRightAnswers) {
            android.R.color.holo_green_light
        } else (
                android.R.color.holo_red_light
                )
        return ContextCompat.getColor(requireContext(), colorResId)
    }

    private fun getColorPercentRightAnswerResId(
        percentOfRightAnswers: Int,
        minPercentOfRightAnswers: Int
    ): Int {
        val colorResId = if (percentOfRightAnswers >= minPercentOfRightAnswers) {
            android.R.color.holo_green_light
        } else {
            android.R.color.holo_red_light
        }
        return ContextCompat.getColor(requireContext(), colorResId)
    }

    private fun getPercentOfRightAnswers(countRightOfAnswers: Int, countOfQuestions: Int): Int {
        return 100 * countRightOfAnswers / countOfQuestions
    }

    private fun setUpClickListeners() {
        binding.bTryAgain.setOnClickListener {
            retryGame()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun retryGame() {
        findNavController().popBackStack()
    }
}