# SnapLink — Single Activity Architecture Refactoring Documentation

## Table of Contents
1. [Overview](#overview)
2. [Architecture Before & After](#architecture-before--after)
3. [File Structure](#file-structure)
4. [Core Components](#core-components)
5. [Fragment Inventory](#fragment-inventory)
6. [Navigation System](#navigation-system)
7. [Data Passing Between Fragments](#data-passing-between-fragments)
8. [Lifecycle Management](#lifecycle-management)
9. [AndroidManifest Changes](#androidmanifest-changes)
10. [Build Configuration Changes](#build-configuration-changes)
11. [Migration Patterns](#migration-patterns)
12. [Performance Optimizations](#performance-optimizations)
13. [Guidelines for Future Development](#guidelines-for-future-development)
14. [Troubleshooting](#troubleshooting)

---

## Overview

This document describes the complete refactoring of the SnapLink Android application from a **multi-activity architecture** (30+ activities) to a **single-activity architecture** using Fragments hosted inside a single `MainActivity`.

### Goals
- **Performance**: Reduce memory overhead by eliminating redundant `Activity` instances and window/view hierarchies.
- **Navigation**: Centralize all navigation logic through `FragmentManager`, enabling smoother transitions and backstack management.
- **Maintainability**: Consolidate UI hosting in one activity, making the codebase easier to reason about and extend.
- **Preserved UX**: Zero changes to the visual UI — every layout, drawable, style, and animation remains untouched.

### What Changed
| Area | Before | After |
|------|--------|-------|
| Activities | 30+ separate `AppCompatActivity` classes | 2 (`Splash_screen` + `MainActivity`) |
| Fragments | 0 | 26 new Fragment classes |
| Navigation | `Intent` + `startActivity()` | `FragmentManager.commit {}` via `MainActivity` |
| Backstack | System-managed activity stack | `FragmentManager` backstack |
| Manifest | 30+ `<activity>` entries | 2 `<activity>` entries |

### What Did NOT Change
- All XML layout files (no modifications)
- All drawable resources
- All styles and themes
- All adapters (FeedAdapter, ProfilePostAdapter, UserAdapter, etc.)
- All data models (FeedModels, NotificationModels, etc.)
- All network layer code (ApiClient, ApiService, AuthInterceptor, TokenManager)
- All API endpoints and responses

---

## Architecture Before & After

### Before: Multi-Activity Architecture
```
Splash_screen (LAUNCHER)
   ├── LoginActivity ─── RegisterActivity
   └── HomeActivityKt
          ├── ExploreActivity ─── SearchActivity
          ├── Messages
          ├── CreatePostActivity
          ├── notifications
          ├── ProfileActivity
          │      ├── EditProfile
          │      ├── PostDetailActivity
          │      ├── Followers
          │      ├── Following
          │      ├── OtherUserProfileActivity
          │      └── setting_menu
          │             ├── PersonalDetails ── ContactInformation ── ChangeEmail/ChangeMobile
          │             ├── PasswordAndSecurity
          │             ├── AccountPrivacy
          │             ├── AccountVerification
          │             ├── Saved
          │             ├── CloseFriends
          │             ├── Blocked
          │             ├── Help
          │             └── About
          └── OtherUserProfileActivity
```

### After: Single Activity Architecture
```
Splash_screen (LAUNCHER) ──→ MainActivity
   └── FragmentContainerView (R.id.fragment_container)
          ├── LoginFragment / RegisterFragment (Auth flow)
          ├── HomeFragment (Main feed + bottom nav)
          ├── ExploreFragment / SearchFragment
          ├── MessagesFragment
          ├── CreatePostFragment
          ├── NotificationsFragment
          ├── ProfileFragment
          │      ├── EditProfileFragment
          │      ├── PostDetailFragment
          │      ├── FollowersFragment / FollowingFragment
          │      └── OtherUserProfileFragment
          └── SettingMenuFragment
                 ├── PersonalDetailsFragment ── ContactInformationFragment
                 │      ├── ChangeEmailFragment
                 │      ├── ChangeMobileFragment
                 │      └── ChangeUsernameFragment
                 ├── PasswordAndSecurityFragment
                 ├── AccountPrivacyFragment
                 ├── AccountVerificationFragment
                 ├── SavedFragment
                 ├── CloseFriendsFragment
                 ├── BlockedFragment
                 ├── HelpFragment
                 └── AboutFragment
```

---

## File Structure

```
app/src/main/java/com/example/snaplink/
├── ui/
│   ├── activities/
│   │   └── MainActivity.kt              ← Single activity host
│   └── fragments/
│       ├── HomeFragment.kt               ← Main feed
│       ├── LoginFragment.kt              ← Authentication
│       ├── RegisterFragment.kt           ← Registration
│       ├── ProfileFragment.kt            ← User profile
│       ├── ExploreFragment.kt            ← Explore grid
│       ├── SearchFragment.kt             ← User search
│       ├── MessagesFragment.kt           ← Messages
│       ├── CreatePostFragment.kt         ← Post creation
│       ├── NotificationsFragment.kt      ← Notifications
│       ├── PostDetailFragment.kt         ← Post detail view
│       ├── OtherUserProfileFragment.kt   ← Other user profile
│       ├── EditProfileFragment.kt        ← Edit profile
│       ├── FollowersFragment.kt          ← Followers list
│       ├── FollowingFragment.kt          ← Following list
│       ├── SettingMenuFragment.kt        ← Settings menu
│       ├── PersonalDetailsFragment.kt    ← Personal details
│       ├── ContactInformationFragment.kt ← Contact info
│       ├── ChangeEmailFragment.kt        ← Change email
│       ├── ChangeMobileFragment.kt       ← Change mobile
│       ├── ChangeUsernameFragment.kt     ← Change username
│       ├── PasswordAndSecurityFragment.kt
│       ├── AccountPrivacyFragment.kt
│       ├── AccountVerificationFragment.kt
│       ├── SavedFragment.kt
│       ├── CloseFriendsFragment.kt
│       ├── BlockedFragment.kt
│       ├── HelpFragment.kt
│       └── AboutFragment.kt
├── network/                              ← Unchanged
├── models/                               ← Unchanged
├── Splash_screen.kt                      ← Updated to route to MainActivity
└── [Original activity files]             ← Retained for reference (unused)
```

---

## Core Components

### 1. `MainActivity.kt`
The **single host activity** for all fragments. Contains three critical methods:

```kotlin
class MainActivity : AppCompatActivity() {

    // Load initial fragment based on auth status
    override fun onCreate(savedInstanceState: Bundle?) {
        val startFragment = if (TokenManager.isLoggedIn()) HomeFragment() else LoginFragment()
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container, startFragment)
        }
    }

    // Standard navigation with backstack support
    fun navigateToFragment(fragment: Fragment, addToBackStack: Boolean = true, tag: String? = null)

    // Clear entire backstack (used for login/logout transitions)
    fun navigateWithClearStack(fragment: Fragment)
}
```

### 2. `activity_main.xml`
A minimal layout with a single `FrameLayout` container:
```xml
<FrameLayout
    android:id="@+id/fragment_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

### 3. `Splash_screen.kt`
Now routes to `MainActivity` instead of directly to `HomeActivityKt` or `LoginActivity`:
```kotlin
Handler(Looper.getMainLooper()).postDelayed({
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    finish()
}, 3000)
```

---

## Fragment Inventory

| Fragment | Original Activity | Layout Used | Category |
|----------|-------------------|-------------|----------|
| `HomeFragment` | `HomeActivityKt` | `activity_home` | Main Nav |
| `ExploreFragment` | `ExploreActivity` | `activity_explore` | Main Nav |
| `MessagesFragment` | `Messages` | `activity_messages` | Main Nav |
| `CreatePostFragment` | `CreatePostActivity` | `activity_create_post` | Main Nav |
| `ProfileFragment` | `ProfileActivity` | `activity_profile` | Main Nav |
| `LoginFragment` | `LoginActivity` | `activity_login` | Auth |
| `RegisterFragment` | `RegisterActivity` | `activity_register` | Auth |
| `NotificationsFragment` | `notifications` | `activity_notifications` | Features |
| `PostDetailFragment` | `PostDetailActivity` | `activity_post_detail` | Features |
| `OtherUserProfileFragment` | `OtherUserProfileActivity` | `activity_other_user_profile` | Features |
| `SearchFragment` | `SearchActivity` | `activity_search` | Features |
| `EditProfileFragment` | `EditProfile` | `activity_edit_profile` | Profile |
| `FollowersFragment` | `Followers` | `activity_followers_page` | Profile |
| `FollowingFragment` | `Following` | `activity_following_page` | Profile |
| `SettingMenuFragment` | `setting_menu` | `activity_setting_menu` | Settings |
| `PersonalDetailsFragment` | `PersonalDetails` | `activity_personal_details` | Settings |
| `ContactInformationFragment` | `ContactInformation` | `activity_contact_information` | Settings |
| `ChangeEmailFragment` | `ChangeEmail` | `activity_change_email` | Settings |
| `ChangeMobileFragment` | `ChangeMobile` | `activity_change_mobile` | Settings |
| `ChangeUsernameFragment` | `ChangeUsername` | `activity_change_username` | Settings |
| `PasswordAndSecurityFragment` | `PasswordAndSecurity` | `activity_password_and_security` | Settings |
| `AccountPrivacyFragment` | `AccountPrivacy` | `activity_account_privacy` | Settings |
| `AccountVerificationFragment` | `AccountVerification` | `activity_account_verification` | Settings |
| `SavedFragment` | `Saved` | `activity_saved` | Settings |
| `CloseFriendsFragment` | `CloseFriends` | `activity_close_friends` | Settings |
| `BlockedFragment` | `Blocked` | `activity_blocked` | Settings |
| `HelpFragment` | `Help` | `activity_help` | Settings |
| `AboutFragment` | `About` | `activity_about` | Settings |

---

## Navigation System

### Pattern 1: Forward Navigation (with backstack)
Used for navigating from one screen to another, allowing the user to press Back to return.

```kotlin
// From inside a fragment:
(activity as? MainActivity)?.navigateToFragment(TargetFragment())
```

### Pattern 2: Back Navigation
Used for "back" buttons and going to the previous screen.

```kotlin
// From inside a fragment:
parentFragmentManager.popBackStack()
```

### Pattern 3: Clear Stack Navigation
Used for login/logout transitions where the entire navigation history should be cleared.

```kotlin
// From inside a fragment:
(activity as? MainActivity)?.navigateWithClearStack(LoginFragment())
```

### Pattern 4: Passing Data to Fragments
Used when navigating to a fragment that needs initial data (like a username or position).

```kotlin
// Use companion object newInstance pattern:
companion object {
    private const val ARG_USERNAME = "USERNAME"

    fun newInstance(username: String): OtherUserProfileFragment {
        return OtherUserProfileFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_USERNAME, username)
            }
        }
    }
}

// Usage:
val fragment = OtherUserProfileFragment.newInstance("john_doe")
(activity as? MainActivity)?.navigateToFragment(fragment)
```

### Bottom Navigation
Bottom navigation items in fragments navigate to sibling fragments:

```kotlin
navHome.setOnClickListener {
    (activity as? MainActivity)?.navigateToFragment(HomeFragment())
}
navSearch.setOnClickListener {
    (activity as? MainActivity)?.navigateToFragment(ExploreFragment())
}
navProfile.setOnClickListener {
    (activity as? MainActivity)?.navigateToFragment(ProfileFragment())
}
```

---

## Data Passing Between Fragments

### 1. Bundle Arguments (Recommended for small data)
```kotlin
// Sender
val fragment = PostDetailFragment.newInstance(position = 5)

// Receiver
val position = arguments?.getInt(ARG_POSITION, 0) ?: 0
```

### 2. PostDataHolder (Singleton for large objects)
Used for passing large `Post` lists between fragments without serialization overhead:
```kotlin
// Before navigation:
PostDataHolder.posts = posts

// In target fragment:
val posts = PostDataHolder.posts
```

### 3. SharedPreferences via TokenManager
Used for auth state and profile data that persists across app sessions.

---

## Lifecycle Management

### Key Differences from Activities

| Activity Pattern | Fragment Equivalent |
|------------------|---------------------|
| `onCreate()` + `setContentView()` | `onCreateView()` returns inflated view |
| `onCreate()` logic | `onViewCreated()` (view is ready) |
| `this` (Context) | `requireContext()` or `context` |
| `this` (Activity) | `requireActivity()` or `activity` |
| `finish()` | `parentFragmentManager.popBackStack()` |
| `startActivity(intent)` | `(activity as? MainActivity)?.navigateToFragment()` |
| `isDestroyed` / `isFinishing` | `isAdded` |
| `registerForActivityResult()` | Same, but called in Fragment |

### Safety Pattern for Async Callbacks
All network callbacks check `isAdded` before updating UI:

```kotlin
ApiClient.api.someCall().enqueue(object : Callback<Response> {
    override fun onResponse(...) {
        if (!isAdded) return  // Fragment detached, don't update UI
        // Safe to update UI here
    }
    override fun onFailure(...) {
        if (!isAdded) return
        // Safe to show error here
    }
})
```

---

## AndroidManifest Changes

### Before (30+ activities)
```xml
<activity android:name=".LoginActivity" android:exported="false" />
<activity android:name=".HomeActivityKt" android:exported="false" />
<activity android:name=".ProfileActivity" android:exported="false" />
<!-- ... 27 more activity entries ... -->
```

### After (2 activities)
```xml
<!-- Splash screen remains as the LAUNCHER activity -->
<activity
    android:name=".Splash_screen"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<!-- Single Activity container for all fragments -->
<activity
    android:name=".ui.activities.MainActivity"
    android:exported="false"
    android:windowSoftInputMode="adjustResize" />
```

---

## Build Configuration Changes

Added `fragment-ktx` dependency for the `commit {}` DSL:

```kotlin
// build.gradle.kts
implementation("androidx.fragment:fragment-ktx:1.6.2")
```

---

## Migration Patterns

### Converting an Activity to a Fragment (Step-by-Step)

1. **Create the Fragment class** in `ui/fragments/`:
   ```kotlin
   class MyFragment : Fragment() {
       override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
           return inflater.inflate(R.layout.activity_my_layout, container, false)
       }
       override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
           super.onViewCreated(view, savedInstanceState)
           // All logic goes here
       }
   }
   ```

2. **Replace `findViewById`** with `view.findViewById`:
   ```kotlin
   // Before (Activity): val btn = findViewById<Button>(R.id.myBtn)
   // After (Fragment):   val btn = view.findViewById<Button>(R.id.myBtn)
   ```

3. **Replace Context references**:
   ```kotlin
   // Before: this, this@MyActivity
   // After:  requireContext(), context
   ```

4. **Replace navigation**:
   ```kotlin
   // Before: startActivity(Intent(this, TargetActivity::class.java))
   // After:  (activity as? MainActivity)?.navigateToFragment(TargetFragment())

   // Before: finish()
   // After:  parentFragmentManager.popBackStack()
   ```

5. **Replace Intent extras** with Bundle arguments:
   ```kotlin
   // Before: intent.getStringExtra("KEY")
   // After:  arguments?.getString("KEY")
   ```

6. **Guard async callbacks**:
   ```kotlin
   if (!isAdded) return  // Add at the start of every callback
   ```

---

## Performance Optimizations

1. **Single Window**: Only one activity window is maintained, reducing memory overhead from multiple `WindowManager` instances.

2. **Fragment Reuse**: Fragment instances share the activity's context and resources, eliminating redundant allocations.

3. **Backstack Management**: `setReorderingAllowed(true)` enables fragment transaction optimizations.

4. **Lazy Initialization**: Fragments inflate views only when visible (`onCreateView` is called when the fragment is attached).

5. **No Redundant Animations**: Activity transitions (which involve window-level animations) are replaced by lightweight fragment transitions.

---

## Guidelines for Future Development

### Adding a New Screen

1. Create a new Fragment class in `ui/fragments/`
2. Create or reuse an XML layout file
3. Follow the lifecycle pattern: `onCreateView` → `onViewCreated`
4. Use `(activity as? MainActivity)?.navigateToFragment()` for navigation
5. Use `parentFragmentManager.popBackStack()` for back navigation
6. Use `isAdded` check in all async callbacks
7. Use `companion object newInstance()` pattern for data passing

### Do NOT
- Create new `Activity` classes (everything should be a Fragment)
- Use `startActivity()` for in-app navigation
- Access `requireContext()` or `requireActivity()` in constructors or `onCreateView`
- Update UI in callbacks without checking `isAdded`

### Naming Conventions
- Fragment classes: `{FeatureName}Fragment.kt` (e.g., `ProfileFragment.kt`)
- Fragment layouts: Reuse existing `activity_*.xml` layouts (no renaming needed)
- Arguments: Use `ARG_` prefix for Bundle keys (e.g., `ARG_USERNAME`)

---

## Troubleshooting

### Common Issues

| Issue | Cause | Fix |
|-------|-------|-----|
| `IllegalStateException: not attached` | Calling `requireContext()` after fragment detached | Check `isAdded` before UI operations |
| Fragment not showing | Missing `setReorderingAllowed(true)` | Always include in commit block |
| Back button exits app | No backstack entries | Ensure `addToBackStack()` is called |
| Views null in callback | Fragment view destroyed | Check `isAdded` and use `view?.` |
| Login loop | Backstack not cleared on login | Use `navigateWithClearStack()` |
| Bottom nav not updating | Fragment recreated each time | Consider caching fragment instances |

### Build Warnings (Acceptable)
- `onBackPressed is deprecated`: Using `@Deprecated` annotation; migration to `OnBackPressedDispatcher` is planned.
- `Condition is always true`: Minor Kotlin smart-cast warning in profile visibility checks.

---

## Summary

The SnapLink application has been successfully refactored from **30+ activities** to a **single-activity architecture** with **26 fragments**, hosted inside `MainActivity`. All UI, functionality, API integrations, and user experience remain identical. The refactoring reduces memory footprint, simplifies navigation, and provides a scalable foundation for future development.

**Build Status**: ✅ BUILD SUCCESSFUL
**Files Modified**: `AndroidManifest.xml`, `Splash_screen.kt`, `build.gradle.kts`
**Files Created**: `MainActivity.kt`, `activity_main.xml`, 26 Fragment files
**Files Unchanged**: All layouts, drawables, styles, adapters, models, network layer
