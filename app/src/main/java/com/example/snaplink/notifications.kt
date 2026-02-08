package com.example.snaplink

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snaplink.models.FollowRequest
import com.example.snaplink.models.NotificationResponse
import com.example.snaplink.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class notifications : AppCompatActivity() {

    private lateinit var rvNotifications: RecyclerView
    private lateinit var rvFollowRequests: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView
    private lateinit var followRequestsSection: LinearLayout
    private lateinit var btnBack: ImageView

    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var followRequestAdapter: FollowRequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        initViews()
        setupAdapters()
        setupListeners()
        loadNotifications()
    }

    private fun initViews() {
        rvNotifications = findViewById(R.id.rvNotifications)
        rvFollowRequests = findViewById(R.id.rvFollowRequests)
        progressBar = findViewById(R.id.progressBar)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        followRequestsSection = findViewById(R.id.followRequestsSection)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupAdapters() {
        // Setup notifications adapter
        notificationAdapter = NotificationAdapter(emptyList())
        rvNotifications.layoutManager = LinearLayoutManager(this)
        rvNotifications.adapter = notificationAdapter

        // Setup follow requests adapter
        followRequestAdapter = FollowRequestAdapter(
            emptyList(),
            onAcceptClick = { request ->
                handleAcceptRequest(request)
            },
            onRejectClick = { request ->
                handleRejectRequest(request)
            }
        )
        rvFollowRequests.layoutManager = LinearLayoutManager(this)
        rvFollowRequests.adapter = followRequestAdapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadNotifications() {
        progressBar.visibility = View.VISIBLE
        tvEmptyState.visibility = View.GONE

        ApiClient.api.getNotifications()
            .enqueue(object : Callback<NotificationResponse> {
                override fun onResponse(
                    call: Call<NotificationResponse>,
                    response: Response<NotificationResponse>
                ) {
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful && response.body()?.success == true) {
                        val notificationResponse = response.body()!!

                        // Update notifications
                        if (notificationResponse.notifications.isNotEmpty()) {
                            notificationAdapter.updateNotifications(notificationResponse.notifications)
                            tvEmptyState.visibility = View.GONE
                        } else {
                            tvEmptyState.visibility = View.VISIBLE
                        }

                        // Update follow requests
                        if (notificationResponse.followRequests.isNotEmpty()) {
                            followRequestAdapter.updateFollowRequests(notificationResponse.followRequests)
                            followRequestsSection.visibility = View.VISIBLE
                        } else {
                            followRequestsSection.visibility = View.GONE
                        }
                    } else {
                        tvEmptyState.visibility = View.VISIBLE
                        Toast.makeText(
                            this@notifications,
                            "Failed to load notifications",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    tvEmptyState.visibility = View.VISIBLE
                    Toast.makeText(
                        this@notifications,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun handleAcceptRequest(request: FollowRequest) {
        ApiClient.api.acceptFollowRequest(request.username)
            .enqueue(object : Callback<com.example.snaplink.network.ApiResponse> {
                override fun onResponse(
                    call: Call<com.example.snaplink.network.ApiResponse>,
                    response: Response<com.example.snaplink.network.ApiResponse>
                ) {
                    if (response.isSuccessful && response.body()?.message != null) {
                        Toast.makeText(
                            this@notifications,
                            "Accepted ${request.username}",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Reload notifications to update the list
                        loadNotifications()
                    } else {
                        Toast.makeText(
                            this@notifications,
                            "Failed to accept request",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<com.example.snaplink.network.ApiResponse>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@notifications,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun handleRejectRequest(request: FollowRequest) {
        ApiClient.api.rejectFollowRequest(request.username)
            .enqueue(object : Callback<com.example.snaplink.network.ApiResponse> {
                override fun onResponse(
                    call: Call<com.example.snaplink.network.ApiResponse>,
                    response: Response<com.example.snaplink.network.ApiResponse>
                ) {
                    if (response.isSuccessful && response.body()?.message != null) {
                        Toast.makeText(
                            this@notifications,
                            "Rejected ${request.username}",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Reload notifications to update the list
                        loadNotifications()
                    } else {
                        Toast.makeText(
                            this@notifications,
                            "Failed to reject request",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<com.example.snaplink.network.ApiResponse>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@notifications,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}