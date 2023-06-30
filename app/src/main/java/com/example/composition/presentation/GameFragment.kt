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
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.Level

class GameFragment : Fragment() {

    private val viewModel: GameViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[GameViewModel::class.java]
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

    private lateinit var level: Level

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding = null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
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
        viewModel.startGame(level)
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

    private fun parseArgs() {
        requireArguments().getParcelable<Level>(KEY_LEVEL)?.let {
            level = it
        }
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
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, GameFinishedFragment.newInstance(gameResult))
            .addToBackStack(null)
            .commit()
    }

    companion object {

        private const val KEY_LEVEL = "level"
        const val NAME = "GameFragment"

        fun newInstance(level: Level): GameFragment {
            return GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LEVEL, level)
                }
            }
        }
    }
}