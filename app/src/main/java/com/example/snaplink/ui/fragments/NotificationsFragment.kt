package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snaplink.FollowRequestAdapter
import com.example.snaplink.NotificationAdapter
import com.example.snaplink.R
import com.example.snaplink.models.FollowRequest
import com.example.snaplink.models.NotificationResponse
import com.example.snaplink.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsFragment : Fragment() {

    private lateinit var rvNotifications: RecyclerView
    private lateinit var rvFollowRequests: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView
    private lateinit var followRequestsSection: LinearLayout
    private lateinit var btnBack: ImageView

    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var followRequestAdapter: FollowRequestAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupAdapters()
        setupListeners()
        loadNotifications()
    }

    private fun initViews(view: View) {
        rvNotifications = view.findViewById(R.id.rvNotifications)
        rvFollowRequests = view.findViewById(R.id.rvFollowRequests)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)
        followRequestsSection = view.findViewById(R.id.followRequestsSection)
        btnBack = view.findViewById(R.id.btnBack)
    }

    private fun setupAdapters() {
        notificationAdapter = NotificationAdapter(emptyList())
        rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        rvNotifications.adapter = notificationAdapter

        followRequestAdapter = FollowRequestAdapter(
            emptyList(),
            onAcceptClick = { request ->
                handleAcceptRequest(request)
            },
            onRejectClick = { request ->
                handleRejectRequest(request)
            }
        )
        rvFollowRequests.layoutManager = LinearLayoutManager(requireContext())
        rvFollowRequests.adapter = followRequestAdapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
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
                    if (!isAdded) return
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful && response.body()?.success == true) {
                        val notificationResponse = response.body()!!

                        if (notificationResponse.notifications.isNotEmpty()) {
                            notificationAdapter.updateNotifications(notificationResponse.notifications)
                            tvEmptyState.visibility = View.GONE
                        } else {
                            tvEmptyState.visibility = View.VISIBLE
                        }

                        if (notificationResponse.followRequests.isNotEmpty()) {
                            followRequestAdapter.updateFollowRequests(notificationResponse.followRequests)
                            followRequestsSection.visibility = View.VISIBLE
                        } else {
                            followRequestsSection.visibility = View.GONE
                        }
                    } else {
                        tvEmptyState.visibility = View.VISIBLE
                        Toast.makeText(
                            requireContext(),
                            "Failed to load notifications",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                    if (!isAdded) return
                    progressBar.visibility = View.GONE
                    tvEmptyState.visibility = View.VISIBLE
                    Toast.makeText(
                        requireContext(),
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
                    if (!isAdded) return
                    if (response.isSuccessful && response.body()?.message != null) {
                        Toast.makeText(
                            requireContext(),
                            "Accepted ${request.username}",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadNotifications()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to accept request",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<com.example.snaplink.network.ApiResponse>,
                    t: Throwable
                ) {
                    if (!isAdded) return
                    Toast.makeText(
                        requireContext(),
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
                    if (!isAdded) return
                    if (response.isSuccessful && response.body()?.message != null) {
                        Toast.makeText(
                            requireContext(),
                            "Rejected ${request.username}",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadNotifications()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to reject request",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<com.example.snaplink.network.ApiResponse>,
                    t: Throwable
                ) {
                    if (!isAdded) return
                    Toast.makeText(
                        requireContext(),
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
