package com.robuxe.robuxtracker.freerobux.Quiz

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.TextView

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.robuxe.robuxtracker.freerobux.R
import com.robuxe.robuxtracker.freerobux.Utils.RC_UtilsClass
import com.robuxe.robuxtracker.freerobux.adsmodule.MyCallback
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdUtil
import com.robuxe.robuxtracker.freerobux.databinding.RcActivityQuizBinding
import org.json.JSONArray
import java.io.InputStream
import java.nio.charset.Charset

class RC_Quiz_Activity : AppCompatActivity() {

    companion object {
        private const val MAX_QUESTIONS = 5
    }

    private val allQuestions = mutableListOf<RC_Quiz_Model>()
    private val quizList = mutableListOf<RC_Quiz_Model>()
    private lateinit var binding: RcActivityQuizBinding
    private var currentIndex = 0
    private var correctCount = 0
    private var totalCoins = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#111111")
            window.navigationBarColor = Color.parseColor("#000000")
        }


        binding = RcActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        AdUtil.getInstance(this@RC_Quiz_Activity).loadNative(binding.frmNative, "small")

        binding.ivback.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        setOptionClicks()
        loadQuizFromAssets()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdUtil.getInstance(this@RC_Quiz_Activity).loadBack(object : MyCallback {
                    override fun onAdCompleted() {
                        finish()
                    }
                })
            }
        })
    }

    private fun setOptionClicks() {
        binding.option1.setOnClickListener { checkAnswer(0) }
        binding.option2.setOnClickListener { checkAnswer(1) }
        binding.option3.setOnClickListener { checkAnswer(2) }
        binding.option4.setOnClickListener { checkAnswer(3) }
    }

    private fun loadQuizFromAssets() {
        Thread {
            try {
                val `is`: InputStream = assets.open("quiz.json")
                val size = `is`.available()
                val buffer = ByteArray(size)
                `is`.read(buffer)
                `is`.close()

                val json = String(buffer, Charset.forName("UTF-8"))
                val array = JSONArray(json)

                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val question = obj.getString("Question")
                    val correct = obj.getInt("correctAnswer")
                    val optArray = obj.getJSONArray("option")
                    val options = Array(4) { j -> optArray.getString(j) }
                    allQuestions.add(RC_Quiz_Model(question, options, correct))
                }

                Handler(Looper.getMainLooper()).post { startNewQuiz() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun startNewQuiz() {
        currentIndex = 0
        correctCount = 0
        totalCoins = 0
        allQuestions.shuffle()
        quizList.clear()
        val limit = MAX_QUESTIONS.coerceAtMost(allQuestions.size)
        quizList.addAll(allQuestions.subList(0, limit))
        showQuestion()
    }

    private fun showQuestion() {
        resetOptions()
        val model = quizList[currentIndex]
        binding.tvQuestion.text = "${currentIndex + 1}. ${model.question}"
        binding.tvProgress.text = "Question ${currentIndex + 1}/${quizList.size}"
        binding.option1.text = model.options[0]
        binding.option2.text = model.options[1]
        binding.option3.text = model.options[2]
        binding.option4.text = model.options[3]
        enableOptions(true)
    }

    private fun checkAnswer(selectedIndex: Int) {
        val model = quizList[currentIndex]
        val correctIndex = model.correctIndex

        if (selectedIndex == correctIndex) {
            getOptionView(selectedIndex).setBackgroundResource(R.drawable.bg_green)
            correctCount++
            totalCoins += 5
        } else {
            getOptionView(selectedIndex).setBackgroundResource(R.drawable.bg_red)
            getOptionView(correctIndex).setBackgroundResource(R.drawable.bg_green)
        }

        enableOptions(false)

        Handler(Looper.getMainLooper()).postDelayed({
            currentIndex++
            if (currentIndex < quizList.size) {
                showQuestion()
            } else {
                openResult()
            }
        }, 900)
    }

    private fun openResult() {
        if (correctCount <= 0) {
            showLossDialog()
        } else {
            val intent = Intent(this, RC_QuizResult_Activity::class.java)
            intent.putExtra("correct", correctCount)
            intent.putExtra("coins", totalCoins)
            RC_UtilsClass.startSpecialActivity(this@RC_Quiz_Activity, intent, false)
            finish()
        }
    }

    private fun showLossDialog() {
        val dialog = RC_UtilsClass.createDialog(this, R.layout.rc_dialog_retry)
        dialog.show()

        val title = dialog.findViewById<TextView>(R.id.tv_title)
        val btnOkay = dialog.findViewById<TextView>(R.id.btnRetry)

        title.text = "All The Answers You Gave Are Wrong."
        btnOkay.text = "Try Again!"

        btnOkay.setOnClickListener {
            AdUtil.getInstance(this@RC_Quiz_Activity).loadInter(object : MyCallback {
                override fun onAdCompleted() {
                    dialog.dismiss()
                    startNewQuiz()
                }
            })
        }
    }

    private fun resetOptions() {
        binding.option1.setBackgroundResource(R.drawable.bg_default)
        binding.option2.setBackgroundResource(R.drawable.bg_default)
        binding.option3.setBackgroundResource(R.drawable.bg_default)
        binding.option4.setBackgroundResource(R.drawable.bg_default)
    }

    private fun enableOptions(enable: Boolean) {
        binding.option1.isEnabled = enable
        binding.option2.isEnabled = enable
        binding.option3.isEnabled = enable
        binding.option4.isEnabled = enable
    }

    private fun getOptionView(index: Int): TextView {
        return when (index) {
            0 -> binding.option1
            1 -> binding.option2
            2 -> binding.option3
            else -> binding.option4
        }
    }
}
