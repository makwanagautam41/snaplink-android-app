package com.example.snaplink.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.snaplink.R

class HelpFragment : Fragment() {

    // Views
    private lateinit var btnBack: ImageView
    private lateinit var etSearchHelp: EditText

    // Quick Actions
    private lateinit var btnContactSupport: RelativeLayout
    private lateinit var btnReportProblem: RelativeLayout
    private lateinit var btnGiveFeedback: RelativeLayout

    // Help Topics
    private lateinit var btnGettingStarted: RelativeLayout
    private lateinit var btnAccountManagement: RelativeLayout
    private lateinit var btnPrivacySecurity: RelativeLayout
    private lateinit var btnPostsStories: RelativeLayout
    private lateinit var btnMessagesNotifications: RelativeLayout
    private lateinit var btnTroubleshooting: RelativeLayout

    // Bottom Contact
    private lateinit var btnContactSupportBottom: Button

    // FAQ Cards
    private lateinit var faqCard1: CardView
    private lateinit var faqCard2: CardView
    private lateinit var faqCard3: CardView
    private lateinit var faqCard4: CardView
    private lateinit var faqCard5: CardView

    // FAQ Answers
    private lateinit var faqAnswer1: TextView
    private lateinit var faqAnswer2: TextView
    private lateinit var faqAnswer3: TextView
    private lateinit var faqAnswer4: TextView
    private lateinit var faqAnswer5: TextView

    // FAQ Expand Icons
    private lateinit var iconExpand1: ImageView
    private lateinit var iconExpand2: ImageView
    private lateinit var iconExpand3: ImageView
    private lateinit var iconExpand4: ImageView
    private lateinit var iconExpand5: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            initViews(view)
            setupListeners()
            setupFAQs()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error loading help: ${e.message}", Toast.LENGTH_LONG).show()
            parentFragmentManager.popBackStack()
        }
    }

    private fun initViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        etSearchHelp = view.findViewById(R.id.etSearchHelp)

        btnContactSupport = view.findViewById(R.id.btnContactSupport)
        btnReportProblem = view.findViewById(R.id.btnReportProblem)
        btnGiveFeedback = view.findViewById(R.id.btnGiveFeedback)

        btnGettingStarted = view.findViewById(R.id.btnGettingStarted)
        btnAccountManagement = view.findViewById(R.id.btnAccountManagement)
        btnPrivacySecurity = view.findViewById(R.id.btnPrivacySecurity)
        btnPostsStories = view.findViewById(R.id.btnPostsStories)
        btnMessagesNotifications = view.findViewById(R.id.btnMessagesNotifications)
        btnTroubleshooting = view.findViewById(R.id.btnTroubleshooting)

        btnContactSupportBottom = view.findViewById(R.id.btnContactSupportBottom)

        faqCard1 = view.findViewById(R.id.faqCard1)
        faqCard2 = view.findViewById(R.id.faqCard2)
        faqCard3 = view.findViewById(R.id.faqCard3)
        faqCard4 = view.findViewById(R.id.faqCard4)
        faqCard5 = view.findViewById(R.id.faqCard5)

        faqAnswer1 = view.findViewById(R.id.faqAnswer1)
        faqAnswer2 = view.findViewById(R.id.faqAnswer2)
        faqAnswer3 = view.findViewById(R.id.faqAnswer3)
        faqAnswer4 = view.findViewById(R.id.faqAnswer4)
        faqAnswer5 = view.findViewById(R.id.faqAnswer5)

        iconExpand1 = view.findViewById(R.id.iconExpand1)
        iconExpand2 = view.findViewById(R.id.iconExpand2)
        iconExpand3 = view.findViewById(R.id.iconExpand3)
        iconExpand4 = view.findViewById(R.id.iconExpand4)
        iconExpand5 = view.findViewById(R.id.iconExpand5)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }

        btnContactSupport.setOnClickListener { openContactSupport() }
        btnContactSupportBottom.setOnClickListener { openContactSupport() }
        btnReportProblem.setOnClickListener { openReportProblem() }
        btnGiveFeedback.setOnClickListener { openFeedback() }

        btnGettingStarted.setOnClickListener { openHelpTopic("Getting Started") }
        btnAccountManagement.setOnClickListener { openHelpTopic("Account Management") }
        btnPrivacySecurity.setOnClickListener { openHelpTopic("Privacy & Security") }
        btnPostsStories.setOnClickListener { openHelpTopic("Posts & Stories") }
        btnMessagesNotifications.setOnClickListener { openHelpTopic("Messages & Notifications") }
        btnTroubleshooting.setOnClickListener { openHelpTopic("Troubleshooting") }
    }

    private fun setupFAQs() {
        setupFAQClick(faqCard1, faqAnswer1, iconExpand1)
        setupFAQClick(faqCard2, faqAnswer2, iconExpand2)
        setupFAQClick(faqCard3, faqAnswer3, iconExpand3)
        setupFAQClick(faqCard4, faqAnswer4, iconExpand4)
        setupFAQClick(faqCard5, faqAnswer5, iconExpand5)
    }

    private fun setupFAQClick(card: CardView, answer: TextView, icon: ImageView) {
        card.setOnClickListener {
            if (answer.visibility == View.GONE) {
                answer.visibility = View.VISIBLE
                rotateIcon(icon, 0f, 180f)
            } else {
                answer.visibility = View.GONE
                rotateIcon(icon, 180f, 0f)
            }
        }
    }

    private fun rotateIcon(icon: ImageView, fromDegrees: Float, toDegrees: Float) {
        val rotate = RotateAnimation(
            fromDegrees, toDegrees,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 200
            fillAfter = true
        }
        icon.startAnimation(rotate)
    }

    private fun openContactSupport() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:support@snaplink.com")
            putExtra(Intent.EXTRA_SUBJECT, "Support Request - SnapLink")
            putExtra(Intent.EXTRA_TEXT, "Hi SnapLink Support Team,\n\nI need help with:\n\n")
        }

        try {
            startActivity(Intent.createChooser(intent, "Contact Support"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No email client installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openReportProblem() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:support@snaplink.com")
            putExtra(Intent.EXTRA_SUBJECT, "Problem Report - SnapLink")
            putExtra(Intent.EXTRA_TEXT, """
                Problem Description:
                
                
                Steps to Reproduce:
                1. 
                2. 
                3. 
                
                Expected Result:
                
                Actual Result:
                
            """.trimIndent())
        }

        try {
            startActivity(Intent.createChooser(intent, "Report Problem"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No email client installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openFeedback() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:feedback@snaplink.com")
            putExtra(Intent.EXTRA_SUBJECT, "Feedback - SnapLink")
            putExtra(Intent.EXTRA_TEXT, "I'd like to share the following feedback:\n\n")
        }

        try {
            startActivity(Intent.createChooser(intent, "Give Feedback"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No email client installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openHelpTopic(topic: String) {
        Toast.makeText(requireContext(), "Opening help for: $topic", Toast.LENGTH_SHORT).show()
    }
}
