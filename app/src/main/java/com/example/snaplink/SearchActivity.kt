package com.example.snaplink

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.OtherUserResponse
import com.example.snaplink.network.RecentSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var btnBack: ImageView
    private lateinit var tvRecentHeader: TextView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var userAdapter: UserAdapter

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setupRecyclerView()
        setupSearchListener()
        
        // Initial state: show recent searches
        loadRecentSearches()
        
        // Auto focus input
        etSearch.requestFocus()
    }

    private fun initViews() {
        etSearch = findViewById(R.id.etSearch)
        btnBack = findViewById(R.id.btnBack)
        tvRecentHeader = findViewById(R.id.tvRecentHeader)
        rvSearchResults = findViewById(R.id.rvSearchResults)

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        rvSearchResults.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(emptyList()) { username ->
            val intent = Intent(this, OtherUserProfileActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }
        rvSearchResults.adapter = userAdapter
    }

    private fun setupSearchListener() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                
                if (query.isEmpty()) {
                    loadRecentSearches()
                } else {
                    searchRunnable = Runnable {
                        performSearch(query)
                    }
                    searchHandler.postDelayed(searchRunnable!!, 500)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = etSearch.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchRunnable?.let { searchHandler.removeCallbacks(it) }
                    performSearch(query)
                }
                true
            } else {
                false
            }
        }
    }

    private fun loadRecentSearches() {
        ApiClient.api.getPastSearchedUsers().enqueue(object : Callback<RecentSearchResponse> {
            override fun onResponse(call: Call<RecentSearchResponse>, response: Response<RecentSearchResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val searches = response.body()?.recentSearches
                    if (!searches.isNullOrEmpty()) {
                        tvRecentHeader.visibility = View.VISIBLE
                        userAdapter.updateUsers(searches)
                    } else {
                        tvRecentHeader.visibility = View.GONE
                        userAdapter.updateUsers(emptyList())
                    }
                } else {
                     tvRecentHeader.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<RecentSearchResponse>, t: Throwable) {
                tvRecentHeader.visibility = View.GONE
            }
        })
    }

    private fun performSearch(query: String) {
        tvRecentHeader.visibility = View.GONE
        
        ApiClient.api.getOtherUserProfile(query).enqueue(object : Callback<OtherUserResponse> {
            override fun onResponse(call: Call<OtherUserResponse>, response: Response<OtherUserResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val users = response.body()?.users
                    if (users != null) {
                        userAdapter.updateUsers(users)
                    } else {
                        userAdapter.updateUsers(emptyList())
                    }
                } else {
                    userAdapter.updateUsers(emptyList())
                }
            }

            override fun onFailure(call: Call<OtherUserResponse>, t: Throwable) {
                Toast.makeText(this@SearchActivity, "Search failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
