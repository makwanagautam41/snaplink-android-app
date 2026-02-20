package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snaplink.R
import com.example.snaplink.UserAdapter
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.OtherUserResponse
import com.example.snaplink.network.RecentSearchResponse
import com.example.snaplink.ui.activities.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private lateinit var etSearch: EditText
    private lateinit var btnBack: ImageView
    private lateinit var tvRecentHeader: TextView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var userAdapter: UserAdapter

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
        setupSearchListener()

        loadRecentSearches()

        etSearch.requestFocus()
    }

    private fun initViews(view: View) {
        etSearch = view.findViewById(R.id.etSearch)
        btnBack = view.findViewById(R.id.btnBack)
        tvRecentHeader = view.findViewById(R.id.tvRecentHeader)
        rvSearchResults = view.findViewById(R.id.rvSearchResults)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        rvSearchResults.layoutManager = LinearLayoutManager(requireContext())
        userAdapter = UserAdapter(emptyList()) { username ->
            val fragment = OtherUserProfileFragment.newInstance(username)
            (activity as? MainActivity)?.navigateToFragment(fragment)
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
                if (!isAdded) return
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
                if (!isAdded) return
                tvRecentHeader.visibility = View.GONE
            }
        })
    }

    private fun performSearch(query: String) {
        tvRecentHeader.visibility = View.GONE

        ApiClient.api.getOtherUserProfile(query).enqueue(object : Callback<OtherUserResponse> {
            override fun onResponse(call: Call<OtherUserResponse>, response: Response<OtherUserResponse>) {
                if (!isAdded) return
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
                if (!isAdded) return
                Toast.makeText(requireContext(), "Search failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
