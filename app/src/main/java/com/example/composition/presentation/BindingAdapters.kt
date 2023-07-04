package com.example.composition.presentation

import android.content.Context
import android.content.res.ColorStateList
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.composition.R
import com.example.composition.domain.entity.GameResult

interface OnOptionClickListener {

    fun onOptionClick(option: Int)
}

@BindingAdapter("countOfAnswers")
fun bindCountOfAnswers(textView: TextView, gameResult: GameResult) = with(gameResult) {
    textView.text = String.format(
        textView.context.getString(R.string.count_right_answer),
        countOfRightAnswers,
        gameSettings.minCountOfRightAnswers
    )
    textView.setBackgroundColor(
        getColorCountRightAnswerResId(
            textView.context,
            gameResult.countOfRightAnswers,
            gameResult.gameSettings.minCountOfRightAnswers
        )
    )
}

@BindingAdapter("percentOfAnswers")
fun bindPercentOfAnswers(textView: TextView, gameResult: GameResult) = with(gameResult) {
    textView.text = String.format(
        textView.context.getString(R.string.percent_right_answers),
        getPercentOfRightAnswers(gameResult),
        gameSettings.minPresentOfRightAnswers
    )
    textView.setBackgroundColor(
        getColorPercentRightAnswerResId(
            textView.context,
            getPercentOfRightAnswers(gameResult),
            gameResult.gameSettings.minPresentOfRightAnswers
        )
    )
}

@BindingAdapter("percentOfRightAnswers")
fun bindPercentOfRightAnswers(progressBar: ProgressBar, percentOfRightAnswers: Int) {
    progressBar.setProgress(percentOfRightAnswers, true)
}

@BindingAdapter("enoughCount")
fun bindEnoughCount(textView: TextView, enoughCount: Boolean) {
    val color = getColorByState(textView.context, enoughCount)
    textView.setTextColor(color)
}

@BindingAdapter("enoughPercent")
fun bindEnoughPercent(progressBar: ProgressBar, enoughPercent: Boolean) {
    val color = getColorByState(progressBar.context, enoughPercent)
    progressBar.progressTintList = ColorStateList.valueOf(color)
}

@BindingAdapter("numberAsText")
fun bindNumberAsText(textView: TextView, number: Int) {
    textView.text = number.toString()
}

@BindingAdapter("onOptionClickListener")
fun bindOnOptionClickListener(textView: TextView, clickListener: OnOptionClickListener) {
    textView.setOnClickListener {
        clickListener.onOptionClick(textView.text.toString().toInt())
    }
}


private fun getColorByState(context: Context, goodState: Boolean): Int {
    val colorResId = if (goodState) {
        android.R.color.holo_green_light
    } else {
        android.R.color.holo_red_light
    }
    return ContextCompat.getColor(context, colorResId)
}

private fun getColorCountRightAnswerResId(
    context: Context,
    countOfRightAnswers: Int,
    minCountOfRightAnswers: Int
): Int {
    val colorResId = if (countOfRightAnswers >= minCountOfRightAnswers) {
        android.R.color.holo_green_light
    } else (
            android.R.color.holo_red_light
            )
    return ContextCompat.getColor(context, colorResId)
}

private fun getColorPercentRightAnswerResId(
    context: Context,
    percentOfRightAnswers: Int,
    minPercentOfRightAnswers: Int
): Int {
    val colorResId = if (percentOfRightAnswers >= minPercentOfRightAnswers) {
        android.R.color.holo_green_light
    } else {
        android.R.color.holo_red_light
    }
    return ContextCompat.getColor(context, colorResId)
}

private fun getPercentOfRightAnswers(gameResult: GameResult) = with(gameResult) {
    if (countOfQuestions == 0) {
        0
    } else {
        ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }
}