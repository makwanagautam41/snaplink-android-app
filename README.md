<div align="center">

```
███████╗███╗   ██╗ █████╗ ██████╗ ██╗     ██╗███╗   ██╗██╗  ██╗
██╔════╝████╗  ██║██╔══██╗██╔══██╗██║     ██║████╗  ██║██║ ██╔╝
███████╗██╔██╗ ██║███████║██████╔╝██║     ██║██╔██╗ ██║█████╔╝ 
╚════██║██║╚██╗██║██╔══██║██╔═══╝ ██║     ██║██║╚██╗██║██╔═██╗ 
███████║██║ ╚████║██║  ██║██║     ███████╗██║██║ ╚████║██║  ██╗
╚══════╝╚═╝  ╚═══╝╚═╝  ╚═╝╚═╝     ╚══════╝╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝
```

**A high-performance Android social media experience — built to scale.**

[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Android SDK](https://img.shields.io/badge/Android_SDK-34-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Architecture-Single_Activity-FF6B6B?style=for-the-badge)](https://developer.android.com/guide/navigation)
[![Material Design](https://img.shields.io/badge/Material_Design-3-757575?style=for-the-badge&logo=materialdesign&logoColor=white)](https://m3.material.io)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

<br/>

*Inspired by industrial leaders · Built for modern Android · Designed for humans*

</div>

---

<br/>

## ✦ What is SnapLink?

SnapLink is a feature-rich social media application for Android, architected around a modern **Single Activity Architecture**. Taking inspiration from platforms like Instagram, SnapLink delivers a seamless experience for sharing posts, exploring feeds, and managing accounts — all with a relentless focus on security, performance, and clean code.

<br/>

---

<br/>

## ◈ Key Features

<br/>

### 🔐 Authentication & Security

> Secure by design, smooth in practice.

| Feature | Description |
|--------|-------------|
| **Smart Login / Register** | Seamless onboarding supporting both email and username flows |
| **Forgot Password** | Secure recovery powered by OTP-based verification |
| **Temporary Deactivation** | Hide your profile and content — reactivate whenever you're ready |
| **Scheduled Deletion** | Permanent removal with a configurable grace period |
| **Instant Reactivation** | Recover deactivated accounts via OTP from the login screen |
| **Cancel Deletion** | Abort scheduled deletions in one tap — right from the login screen |

<br/>

### 📱 Core Social Experience

> Everything users expect, nothing they don't.

| Feature | Description |
|--------|-------------|
| **Dynamic Feed** | High-performance scrolling feed for posts and stories |
| **Story Integration** | Share temporary moments with your followers |
| **Explore & Search** | Discover content and find friends via debounced live search |
| **Profiles** | Post counts, follower/following lists, bios, and more |
| **Interactions** | Like, comment, and follow with real-time UI updates |

<br/>

### ⚙️ Advanced Settings

> Total control over your digital identity.

| Feature | Description |
|--------|-------------|
| **Personal Details** | Manage email, phone number, and date of birth |
| **Privacy Controls** | Toggle between Public / Private profiles, manage blocked users |
| **Security** | Update passwords and manage account-level permissions |
| **Customization** | Long-press profile images to change or remove them instantly |

<br/>

---

<br/>

## ◈ Technical Architecture

SnapLink follows a production-grade **Single Activity Architecture (SAA)** — dramatically reducing memory overhead and providing a battle-tested navigation system across 30+ fragments.

<br/>

### 🧱 Tech Stack

```
┌─────────────────────────────────────────────────────────────┐
│  Layer             │  Technology                            │
├────────────────────┼────────────────────────────────────────┤
│  Language          │  Kotlin (100%)                         │
│  Architecture      │  Single Activity + Fragment-based UI   │
│  Networking        │  Retrofit 2 + OkHttp 4                 │
│  UI Components     │  Material Design 3, CoordinatorLayout  │
│                    │  ConstraintLayout                      │
│  Data Persistence  │  SharedPreferences (Session & Tokens)  │
│  Image Loading     │  Efficient remote asset handling       │
└─────────────────────────────────────────────────────────────┘
```

<br/>

### 🗂️ Project Structure

```text
app/src/main/java/com/example/snaplink/
│
├── 📁 ui/
│   ├── 📁 activities/
│   │   └── MainActivity.kt          ← The single host for all Fragments
│   │
│   └── 📁 fragments/                ← 30+ dedicated UI modules
│       ├── HomeFragment.kt          ← Main social feed
│       ├── LoginFragment.kt         ← Auth entry with redirect logic
│       ├── ProfileFragment.kt       ← User profile & metrics
│       ├── AccountOwnership.kt      ← Deactivation / Deletion hub
│       └── ...                      ← Feature-specific fragments
│
├── 📁 network/
│   ├── ApiClient.kt                 ← Retrofit configuration
│   ├── ApiService.kt                ← 50+ API endpoint definitions
│   └── TokenManager.kt             ← Secure token & session storage
│
└── 📁 models/                       ← Strongly typed data classes
```

<br/>

---

<br/>

## ◈ Installation & Setup

<br/>

**Step 1 — Prerequisites**

Ensure your environment meets the following requirements before building:

→ Android Studio **Hedgehog** or newer  
→ Android **SDK 34** (UpsideDownCake) or higher  
→ **Java 17**

<br/>

**Step 2 — Clone & Build**

```bash
git clone https://github.com/makwanagautam41/snaplink-android-app.git
cd snaplink-android-app
# Open in Android Studio and sync Gradle
```

<br/>

**Step 3 — API Configuration**

The app communicates with a backend REST API. Make sure your `baseUrl` inside `network/ApiClient.kt` is pointing to your active server instance before running the app.

<br/>

---

<br/>

## ◈ Navigation & Flow

SnapLink uses a centralized navigation system orchestrated by `MainActivity`.

```
Forward Navigation
  ╰─▶  (activity as? MainActivity)?.navigateToFragment(TargetFragment())

Back Navigation
  ╰─▶  Managed automatically via FragmentManager (standard Back behavior)

Root Transitions (Login / Logout)
  ╰─▶  navigateWithClearStack() — prevents back-navigation into secure sessions
```

<br/>

---

<br/>

## ◈ Security Best Practices

<br/>

**→ Async Safety**  
Every network callback validates the `isAdded` state of the Fragment before touching the UI — eliminating "Fragment not attached" crashes in production.

**→ OTP Safeguards**  
OTP screens enforce a 30-second resend cooldown timer and use automatic focus-shifting for a frictionless yet secure verification experience.

**→ Identity Verification**  
Account deactivation and deletion require password re-verification along with optional user feedback, enabling data-driven product improvement.

<br/>

---

<br/>

## ◈ Roadmap

> Contributions will be available soon. Stay tuned.

<br/>

---

<br/>

<div align="center">

```
Built with ❤️ by the SnapLink Team
```

[![Gautam Makwana](https://img.shields.io/badge/Gautam_Makwana-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/makwanagautam41)
[![Dhruvraj Zala](https://img.shields.io/badge/Dhruvraj_Zala-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/DhruvrajZala46)

<br/>

*If you find SnapLink useful, consider giving it a ⭐ on GitHub!*

</div>