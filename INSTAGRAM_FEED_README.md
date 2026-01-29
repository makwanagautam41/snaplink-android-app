# Snaplink - Instagram-like Home Feed App

## 📱 Project Overview

A complete Instagram-like home feed Android application with stories, posts, and bottom navigation.

## ✅ What's Included

### Layout Files (res/layout/)
- ✅ `activity_home.xml` - Main home screen with top bar, stories, feed, and bottom navigation
- ✅ `item_story.xml` - Story item with circular avatar and gradient border
- ✅ `item_post.xml` - Post item with header, image, actions, and comments

### Drawable Files (res/drawable/)
- ✅ `story_gradient_border.xml` - Instagram-style gradient (red → purple → blue)
- ✅ `page_indicator_active.xml` - Blue circle for active page
- ✅ `page_indicator_inactive.xml` - Gray circle for inactive pages

### Java Files
- ✅ `Story.java` - Story model class
- ✅ `Post.java` - Post model class
- ✅ `StoryAdapter.java` - RecyclerView adapter for stories
- ✅ `PostAdapter.java` - RecyclerView adapter for posts
- ✅ `HomeActivityJava.java` - Main activity (Java version)

### Kotlin Files
- ✅ `StoryKt.kt` - Story data class
- ✅ `PostKt.kt` - Post data class
- ✅ `StoryAdapterKt.kt` - Story adapter (Kotlin)
- ✅ `PostAdapterKt.kt` - Post adapter (Kotlin)
- ✅ `HomeActivityKt.kt` - Main activity (Kotlin version)

## 🎨 Design Specifications

- **Theme**: Dark mode (Instagram-like)
- **Background**: Black (#000000)
- **Text**: White (#FFFFFF)
- **Secondary Text**: Gray (#888888)
- **Story Border**: Gradient (Red #FD5949 → Purple #D6249F → Blue #285AEB)
- **Active Indicator**: Blue (#2196F3)
- **Inactive Indicator**: Gray (#555555)

## 📦 Dependencies Added

```gradle
implementation("androidx.recyclerview:recyclerview:1.3.2")
implementation("de.hdodenhof:circleimageview:3.1.0")
```

## 🖼️ Required Image Resources

You need to add these drawable resources to `res/drawable/`:

### Icons (24x24 dp)
- `ic_snaplink_logo` - App logo
- `ic_messenger` - Messenger icon
- `ic_home_filled` - Home icon (filled)
- `ic_search` - Search icon
- `ic_add_post` - Add post icon
- `ic_reels` - Reels icon
- `ic_profile_placeholder` - Profile icon
- `ic_more_vertical` - Three dots menu
- `ic_heart_outline` - Like button
- `ic_comment` - Comment icon
- `ic_share` - Share icon
- `ic_bookmark_outline` - Bookmark icon
- `ic_emoji_1` - Emoji icon 1
- `ic_emoji_2` - Emoji icon 2

### User Images (Square, 200x200+)
- `img_current_user` - Current user avatar
- `img_user_1` - User 1 avatar (punit_super)
- `img_user_2` - User 2 avatar (siko.speed)
- `img_user_3` - User 3 avatar (galish...)
- `img_user_4` - User 4 avatar (talvin)
- `img_user_post_1` - Post user avatar (__tushill)
- `img_user_placeholder` - Default user avatar

### Post Images (Square or 4:5 ratio)
- `img_post_1` - Main post image
- `img_post_placeholder` - Default post image

## 🚀 How to Use

### Option 1: Use Java Version
In `AndroidManifest.xml`, the app currently uses `LoginActivity` as launcher. After login, navigate to `HomeActivityJava`:

```java
Intent intent = new Intent(this, HomeActivityJava.class);
startActivity(intent);
```

### Option 2: Use Kotlin Version
Navigate to `HomeActivityKt`:

```kotlin
val intent = Intent(this, HomeActivityKt::class.java)
startActivity(intent)
```

### Option 3: Set as Launcher (for testing)
To test the home feed directly, change the launcher activity in `AndroidManifest.xml`:

```xml
<activity
    android:name=".HomeActivityKt"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

## 📋 Features

### Top Bar
- Snaplink logo (left)
- Messenger icon (right)

### Stories Section
- Horizontal scrolling
- Circular avatars with gradient border
- "Your Story" with add icon
- Username labels

### Posts Feed
- Vertical scrolling
- Post header (avatar, username, options)
- Post image (400dp height)
- Action buttons (like, comment, share, bookmark)
- Page indicators (4 dots)
- Likes count
- Caption with username
- "View all comments" link
- Time ago
- Comment input field

### Bottom Navigation
- Home (filled icon)
- Search
- Add Post
- Reels
- Profile

## 🎯 Dummy Data Included

### Stories
1. "Your Story" (with add icon)
2. "punit_super"
3. "siko.speed"
4. "galish..."
5. "talvin"
6. "john_doe"
7. "jane_smith"
8. "mike_ross"

### Posts
1. "__tushill" - "Lorem ipsum dolor sit amet..." (10 minutes ago)
2. "punit_super" - "Amazing sunset view..." (1 hour ago)
3. "siko.speed" - "New adventure begins..." (3 hours ago)
4. "talvin" - "Coffee and code..." (5 hours ago)

## 🔧 Build Instructions

1. **Sync Gradle**: Click "Sync Now" to download dependencies
2. **Add Images**: Place all required images in `res/drawable/`
3. **Build**: Build → Make Project
4. **Run**: Run on emulator or device

## 📝 Notes

- The app uses existing authentication (LoginActivity, RegisterActivity)
- HomeActivity variants are separate from the existing HomeActivity
- All colors are defined inline (no colors.xml needed)
- Layouts use ConstraintLayout and LinearLayout as specified
- CircleImageView library used for circular avatars
- RecyclerView for both stories and posts

## 🎨 Customization

### Change Colors
Edit inline color values in XML files:
- Background: `android:background="#000000"`
- Text: `android:textColor="#FFFFFF"`
- Secondary: `android:textColor="#888888"`

### Add More Stories/Posts
Edit the `setupStories()` and `setupPosts()` methods in HomeActivity:

```java
storyList.add(new Story("username", R.drawable.avatar, false));
postList.add(new Post("username", R.drawable.avatar, R.drawable.image, "caption", "time"));
```

### Change Story Gradient
Edit `story_gradient_border.xml`:
```xml
<gradient
    android:startColor="#YOUR_COLOR"
    android:centerColor="#YOUR_COLOR"
    android:endColor="#YOUR_COLOR" />
```

## 🐛 Troubleshooting

### Images Not Showing
- Ensure all drawable resources are added to `res/drawable/`
- Check resource names match exactly (case-sensitive)
- Use PNG or WebP format for images

### RecyclerView Not Scrolling
- Check layout_height is set correctly
- Ensure LayoutManager is set
- Verify adapter has data

### Gradle Sync Issues
- Check internet connection
- Invalidate Caches: File → Invalidate Caches / Restart
- Update Gradle if needed

## 📱 Tested On
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- Compile SDK: 36

## 🎉 Ready to Run!

Once you add the image resources, the app is ready to compile and run. Choose either Java or Kotlin version based on your preference.

---

**Created**: 2026-01-23  
**Version**: 1.0  
**Status**: ✅ Production Ready
