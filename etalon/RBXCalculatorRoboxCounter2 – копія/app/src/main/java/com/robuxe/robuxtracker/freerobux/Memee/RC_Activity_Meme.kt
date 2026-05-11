package com.robuxe.robuxtracker.freerobux.Memee

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.robuxe.robuxtracker.freerobux.R
import com.robuxe.robuxtracker.freerobux.adsmodule.MyCallback
import com.robuxe.robuxtracker.freerobux.adsmodule.adscode.AdUtil
import com.robuxe.robuxtracker.freerobux.databinding.RcActivityMemeBinding
import org.json.JSONObject
import java.io.InputStream
import java.io.InputStreamReader

class RC_Activity_Meme : AppCompatActivity() {
    private lateinit var binding: RcActivityMemeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#111111")
            window.navigationBarColor = Color.parseColor("#000000")
        }



        binding = RcActivityMemeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        AdUtil.getInstance(this@RC_Activity_Meme).loadNative(binding.frmNative, "small")
        binding.ivback.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        loadMemesFromAssets()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdUtil.getInstance(this@RC_Activity_Meme).loadBack(object : MyCallback {
                    override fun onAdCompleted() {
                        finish()
                    }
                })
            }
        })
    }

    private fun loadMemesFromAssets() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE

        try {
            val inputStream: InputStream = assets.open("meme.json")
            val reader = InputStreamReader(inputStream)
            val builder = StringBuilder()
            val buffer = CharArray(1024)
            var read: Int
            while (reader.read(buffer).also { read = it } != -1) {
                builder.append(buffer, 0, read)
            }
            reader.close()
            inputStream.close()

            val jsonObject = JSONObject(builder.toString())
            val memesArray = jsonObject.getJSONArray("memes").toString()

            val listType = object : TypeToken<List<RC_Model_MemeItem>>() {}.type
            val memeList: List<RC_Model_MemeItem> = Gson().fromJson(memesArray, listType)

            binding.progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE

            setupRecyclerView(memeList)
        } catch (e: Exception) {
            e.printStackTrace()
            binding.progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView(memeList: List<RC_Model_MemeItem>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = RC_Adapter_Meme(this, memeList) { meme ->
            shareMeme(meme)
        }
    }

    private fun shareMeme(meme: RC_Model_MemeItem) {
        val shareText = "${meme.title}\n\n${meme.description}"
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, shareText)
        startActivity(Intent.createChooser(intent, "Share this meme via"))
    }
}
