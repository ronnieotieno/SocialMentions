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
    private lateinit var userService: UsersService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val retrofit = createRetrofit()
        customMentionAdapter = UsersAdapter(this)

        userService = retrofit.create(UsersService::class.java)

        binding.EdittextMention.apply {
            mentionAdapter = customMentionAdapter
            setMentionTextChangedListener { _, text ->
                startSearching(text)
            }
            setOnMentionClickListener { _, s ->
                Toast.makeText(this@MainActivity, "You have clicked on $s", Toast.LENGTH_SHORT)
                    .show()
            }
            setOnHashtagClickListener { _, s ->
                Toast.makeText(this@MainActivity, "You have clicked on $s", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun startSearching(queryString: String) {

        if (queryString.endsWith(" ")) return

        //Using coroutine, feel free to use any or even a new background thread.
        //You can use the enqueue method and write the following logic on onsuccesfull method.
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val users = userService.getUsers(
                    queryString
                )
                withContext(Dispatchers.Main) {
                    customMentionAdapter.apply {
                        clear()
                        addAll(users.data)
                        notifyDataSetChanged()
                    }

                    //Workaround method to initiate show adapter with new items
                    //probably not the best, feel free to contribute.

                    binding.EdittextMention.apply {
                        text.insert(
                            text.toString().length,
                            " "
                        )
                        val length: Int = text.length
                        if (length > 0) {
                            text.delete(length - 1, length)
                        }
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