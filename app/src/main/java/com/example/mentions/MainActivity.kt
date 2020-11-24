package com.example.mentions

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mentions.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var customMentionAdapter: ArrayAdapter<String>
    private lateinit var userClass: Users
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val retrofit = createRetrofit()
        customMentionAdapter = UsersAdapter(this)


        userClass = retrofit.create(Users::class.java)


        binding.EdittextMention.mentionAdapter = customMentionAdapter
        binding.EdittextMention.setMentionTextChangedListener { _, text ->
            startSearching(text)
        }

    }

    private fun startSearching(text: String) {

        if (text.endsWith(" ")) return

        //Using courotine, feel free to use any or even a new background thread.
        //You can use the enqueue method and write the following logic on onsuccesfull method.
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val users = userClass.getUsers(
                    text
                )
                withContext(Dispatchers.Main) {
                    customMentionAdapter.clear()
                    customMentionAdapter.addAll(users.data)
                    customMentionAdapter.notifyDataSetChanged()

                    //Workaround method to initiate show adapter with new items
                    //probably not the best, feel free to contribute.

                    binding.EdittextMention.text.insert(
                        binding.EdittextMention.text.toString().length,
                        " "
                    )
                    val length: Int = binding.EdittextMention.text.length
                    if (length > 0) {
                        binding.EdittextMention.text.delete(length - 1, length)
                    }
                }

            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, t.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                }

            }
        }
    }
}