package com.example.snaplink

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Help : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            setContentView(R.layout.activity_help)

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            initViews()
            setupListeners()
            setupFAQs()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading help: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun initViews() {
        // Back button
        btnBack = findViewById(R.id.btnBack)

        // Search
        etSearchHelp = findViewById(R.id.etSearchHelp)

        // Quick Actions
        btnContactSupport = findViewById(R.id.btnContactSupport)
        btnReportProblem = findViewById(R.id.btnReportProblem)
        btnGiveFeedback = findViewById(R.id.btnGiveFeedback)

        // Help Topics
        btnGettingStarted = findViewById(R.id.btnGettingStarted)
        btnAccountManagement = findViewById(R.id.btnAccountManagement)
        btnPrivacySecurity = findViewById(R.id.btnPrivacySecurity)
        btnPostsStories = findViewById(R.id.btnPostsStories)
        btnMessagesNotifications = findViewById(R.id.btnMessagesNotifications)
        btnTroubleshooting = findViewById(R.id.btnTroubleshooting)

        // Bottom Contact
        btnContactSupportBottom = findViewById(R.id.btnContactSupportBottom)

        // FAQ Cards
        faqCard1 = findViewById(R.id.faqCard1)
        faqCard2 = findViewById(R.id.faqCard2)
        faqCard3 = findViewById(R.id.faqCard3)
        faqCard4 = findViewById(R.id.faqCard4)
        faqCard5 = findViewById(R.id.faqCard5)

        // FAQ Answers
        faqAnswer1 = findViewById(R.id.faqAnswer1)
        faqAnswer2 = findViewById(R.id.faqAnswer2)
        faqAnswer3 = findViewById(R.id.faqAnswer3)
        faqAnswer4 = findViewById(R.id.faqAnswer4)
        faqAnswer5 = findViewById(R.id.faqAnswer5)

        // FAQ Expand Icons
        iconExpand1 = findViewById(R.id.iconExpand1)
        iconExpand2 = findViewById(R.id.iconExpand2)
        iconExpand3 = findViewById(R.id.iconExpand3)
        iconExpand4 = findViewById(R.id.iconExpand4)
        iconExpand5 = findViewById(R.id.iconExpand5)
    }

    private fun setupListeners() {
        // Back button
        btnBack.setOnClickListener { finish() }

        // Quick Actions
        btnContactSupport.setOnClickListener { openContactSupport() }
        btnContactSupportBottom.setOnClickListener { openContactSupport() }
        btnReportProblem.setOnClickListener { openReportProblem() }
        btnGiveFeedback.setOnClickListener { openFeedback() }

        // Help Topics
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
            Toast.makeText(this, "No email client installed", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "No email client installed", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "No email client installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openHelpTopic(topic: String) {
        // Open help article for the specific topic
        Toast.makeText(this, "Opening help for: $topic", Toast.LENGTH_SHORT).show()

        // Example: Open help center web page
        /*
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://help.snaplink.com/${topic.lowercase().replace(" ", "-")}")
        }
        startActivity(intent)
        */
    }
}