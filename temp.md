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

<br/>

*Native Android Client Documentation*

</div>

---

## ✦ Overview

SnapLink is a native Android application built entirely in **Kotlin** using a **Single-Activity Architecture**. This documentation focuses explicitly on the Android client, covering everything from the deep feature set, internal flow patterns, data passing, and UI management. The mobile application heavily leverages Material 3, advanced `RecyclerView` components, and complex multi-part media uploads.

---

## ◈ Comprehensive Feature Set

SnapLink implements a massive set of features, strictly separated into dedicated UI Fragments.

### 🔐 1. Authentication & Security Flow
*   **Sign In / Sign Up:** Clean forms supporting both email and username logins. Connects via Retrofit to retrieve the auth token.
*   **Encrypted Sessions:** Tokens are injected globally via `AuthInterceptor.kt` and stored securely using `androidx.security:security-crypto` (`TokenManager.kt`).
*   **Forgot Password:** Multi-step OTP (One Time Password) email verification flow to securely reset passwords.
*   **Deactivation & Deletion Hub:** Users can temporarily deactivate accounts, or schedule permanent deletion.
*   **Reactivation:** Abort a scheduled deletion or wake up a deactivated account by logging in and verifying an OTP.

### 📱 2. Core Social & Feed Experience
*   **Home Feed & Pagination:** Infinite-scrolling feed implemented via `RecyclerView` paired with a custom `FeedAdapter.kt` and `Pagination` API models.
*   **Deep Media Support:** Posts can contain up to **5 items** (Images or Videos) leveraging `MediaSliderAdapter` and `ImageSliderAdapter` within `ViewPager2`.
*   **Double-Tap to Like:** Custom `DoubleTapListener.kt` mimics Instagram-style rapid interactions directly on the images.
*   **Commenting System:** Real-time adding and deleting of comments tightly tied to the current authenticated `User`.
*   **Story Ecosystem:** 
    *   Upload images to your personal story with captions.
    *   View your own active stories, or view your followers' stories automatically.
    *   Delete active stories individually.
*   **Explore Tab:** Grid layout discovery feed to find new content globally.
*   **Global Search:** Debounced profile searching with a "Recent Searches" persistence layer.

### 👤 3. Profile & Network Management
*   **Personal Profile View:** Renders bio, dynamic post grids (`ProfilePostAdapter`), follower/following counts, and profile imagery. Instantly update/remove your profile image with a long-press.
*   **Other User Profiles:** Dynamically loads other users' data. Toggles buttons smartly based on relationships ("Follow", "Following", or "Requested").
*   **Follow Request System:** Users with private accounts accrue requests. `FollowRequestAdapter` allows the user to individually "Accept" or "Reject".
*   **Followers & Following Lists:** Dedicated lists to view relationships.
*   **Close Friends:** Add users to a specialized trusted tier.
*   **Notifications:** Dedicated bell tab (`NotificationAdapter.kt`) routing to all recent likes, comments, and follows.
*   **About This Account:** An informative modal (`UsersAboutSection.kt`) formatting the exact UTC timestamp of when a user joined the platform.
    
### ⚙️ 4. Advanced User Settings
Orchestrated through `SettingMenuFragment.kt` and mapped via `SettingsManager.kt`.
*   **Contact Information:** Manage and swap the active Email, Mobile Number, and **Date of Birth**.
*   **Username Overrides:** Change display tags safely.
*   **Password Security:** Authenticated localized password updates.
*   **Account Privacy:** One-tap toggle between **Public** and **Private** visibility schemas.
*   **Account Status & Guidelines Hub:** Nested info fragments (`AccountStatusFragment.kt`, `AccountStatusCommunityFragment.kt`, etc.) to check platform standing, view restricted features, and verify age limits.
*   **Blocked Users:** View and manage the localized deny-list.
*   **Saved Posts:** Bookmark posts in the feed and view them all privately in a `SavedFragment` collection.
*   **Account Verification:** Request platform blue-check verification seamlessly via OTP validation (`AccountStatusVerificationOtpFragment.kt`).

---

## ◈ Mobile Architecture (Inside the App)

### 🧱 Single Activity Architecture (SAA)
Previously spanned across 30+ separate Activities, the app was heavily refactored down to:
1.  **Splash_screen.kt** (Launch router).
2.  **MainActivity.kt** (The single container).

All UI transitions happen across **26+ Fragments** within a single `FrameLayout`. This provides massive performance benefits:
*   **Zero Window Overheads:** Transitions are instantaneous. `supportFragmentManager` swaps the views.
*   **Predictable Backstack:** Standardizing the back button prevents "looping" issues or memory leaks. Calling `navigateWithClearStack()` securely wipes history after a logout.
*   **Context Safety:** Fragments require strict bindings. Network callbacks proactively check `if (!isAdded) return` to avoid Null Pointer Exceptions.

### 📡 API Communication Layer
Network logic is fully isolated into the `network/` package.
*   **ApiClient.kt:** Instantiates Retrofit and OkHttp. Configured with extended timeouts for media uploads and attaches logging interceptors.
*   **AuthInterceptor.kt:** An OkHttp chain interceptor. If `TokenManager.isLoggedIn()` is true, it silently attaches the `Bearer {JWT}` header to every request, completely decoupling UI logic from Auth state.
*   **ApiService.kt:** Contains over 50+ `@GET`, `@POST`, `@PUT`, and `@DELETE` method signatures cleanly mapping remote routes to Kotlin domain classes (`FeedResponse`, `User`, `ApiResponse`).

### 📦 Media Upload & Multipart Engineering
To support Android's scoped storage and file limits, SnapLink executes advanced media uploads:
1.  `CreatePostFragment` queries the Android OS `PhotoPicker` for images or videos.
2.  The application enforces a **5-attachment limit**.
3.  The local URIs are decoded, streamed into temp files, and packaged dynamically into an array of `MultipartBody.Part` types.
4.  Retrofit safely pipes these raw byte-streams to the server (`@Part images: List<MultipartBody.Part>`), where the backend resolves them to cloud URIs.

### 🧠 Android State & Caching Patterns
*   **PostDataHolder.kt:** A singleton cache map. Passing arrays of 500+ complicated `Post` objects via Android `Bundle Args` throws `TransactionTooLargeException`. We bypass this by pointing to a static memory reference when transitioning between the Feed and `PostDetailFragment.kt`.
*   **Glide Management:** `CircleImageView` and standard `<ImageView>` tags are bound securely through the Glide SDK to cache massive grids in the `ExploreFragment` efficiently into disk/memory.

---

## ◈ Directory & Code Map

```text
app/src/main/java/com/example/snaplink/
├── 📁 network/
│   ├── ApiClient.kt             # OkHttp & Retrofit Builder
│   ├── ApiService.kt            # 50+ REST endpoints defined
│   ├── AuthInterceptor.kt       # JWT Header injection mapping
│   ├── SettingsManager.kt       # Repository for settings endpoint calls
│   └── TokenManager.kt          # Encrypted token & session persistence
│
├── 📁 models/
│   ├── FeedModels.kt            # Data objects: Post, Comment, PostMedia, Pagination
│   ├── NotificationModels.kt    # Structure for the alerts center
│   └── SettingsModels.kt        # Setup payloads for profile modifications
│
├── 📁 adapters/                 # Recycler View Transformers
│   ├── FeedAdapter.kt           # Main scrolling timeline, supports likes/media 
│   ├── MediaSliderAdapter.kt    # ViewPager2 adapter for multi-photo posts
│   ├── NotificationAdapter.kt   # Renders distinct dynamic notification rows
│   ├── FollowRequestAdapter.kt  # Accept/Reject UI logic
│   └── UserAdapter.kt           # Grid layouts for profiles and searches
│
└── 📁 ui/
    ├── MainActivity.kt          # The Single Activity Host Layer
    └── 📁 fragments/            # Features
        ├── HomeFragment.kt          # Standard feed pulling
        ├── LoginFragment.kt / RegisterFragment.kt
        ├── CreatePostFragment.kt    # Multipart uploader logic 
        ├── ProfileFragment.kt       # User metrics and posts
        ├── SettingsMenuFragment.kt  # Route hub for all sub-settings
        ├── AccountPrivacyFragment.kt 
        └── NotificationsFragment.kt

---

<div align="center">
<i>Every component respects lifecycle boundaries, prioritizes deep encryption, and ensures network safety. Focus strictly on building the perfect UX.</i>
</div>