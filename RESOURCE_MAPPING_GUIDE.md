# 📋 Snaplink - Resource Mapping Reference

## Complete Guide: Icons, Images & Layouts

This document maps all drawable resources (icons and images) to their usage locations in layout files.

---

## 🎨 **ICONS** (Vector Drawables)

### **Top Bar Icons**

| Icon File | Size | Used In | Element ID | Purpose |
|-----------|------|---------|------------|---------|
| `ic_messenger.xml` | 26dp | `activity_home.xml` | `@+id/ivMessenger` | Messenger/DM button in top bar |

### **Bottom Navigation Icons**

| Icon File | Size | Used In | Element ID | Purpose |
|-----------|------|---------|------------|---------|
| `ic_home_filled.xml` | 26dp | `activity_home.xml` | `@+id/navHome` | Home tab (active/filled) |
| `ic_search.xml` | 26dp | `activity_home.xml` | `@+id/navSearch` | Search tab |
| `ic_add_post.xml` | 26dp | `activity_home.xml` | `@+id/navAdd` | Create post tab |
| `ic_reels.xml` | 26dp | `activity_home.xml` | `@+id/navReels` | Reels/Video tab |

### **Post Action Icons**

| Icon File | Size | Used In | Element ID | Purpose |
|-----------|------|---------|------------|---------|
| `ic_heart_outline.xml` | 26dp | `item_post.xml` | `@+id/ivLike` | Like button |
| `ic_comment.xml` | 26dp | `item_post.xml` | `@+id/ivComment` | Comment button |
| `ic_share.xml` | 26dp | `item_post.xml` | `@+id/ivShare` | Share button |
| `ic_bookmark_outline.xml` | 24dp | `item_post.xml` | `@+id/ivBookmark` | Save/Bookmark button |

### **Post Header Icons**

| Icon File | Size | Used In | Element ID | Purpose |
|-----------|------|---------|------------|---------|
| `ic_more_vertical.xml` | 20dp | `item_post.xml` | `@+id/ivPostOptions` | More options (3 dots) menu |

### **Comment Section Icons**

| Icon File | Size | Used In | Element ID | Purpose |
|-----------|------|---------|------------|---------|
| `ic_emoji_1.xml` | 18dp | `item_post.xml` | `@+id/ivEmoji1` | Emoji picker 1 |
| `ic_emoji_2.xml` | 18dp | `item_post.xml` | `@+id/ivEmoji2` | Emoji picker 2 |

### **Story Add Icon**

| Icon File | Size | Used In | Element ID | Purpose |
|-----------|------|---------|------------|---------|
| `ic_add_post.xml` | 20dp | `item_story.xml` | `@+id/ivAddStory` | Add story button (on "Your Story") |

---

## 🖼️ **IMAGES** (User Avatars & Posts)

### **User Avatar Images**

| Image File | Color | Size | Used In | Element ID | Purpose |
|------------|-------|------|---------|------------|---------|
| `img_current_user.xml` | Blue | 62dp | `item_story.xml` | `@+id/ivStoryAvatar` | "Your Story" avatar |
| `img_current_user.xml` | Blue | 26dp | `activity_home.xml` | `@+id/navProfile` | Bottom nav profile icon |
| `img_current_user.xml` | Blue | 24dp | `item_post.xml` | `@+id/ivCommentUserAvatar` | Comment input avatar |
| `img_user_1.xml` | Orange | 62dp | `item_story.xml` | `@+id/ivStoryAvatar` | punit_super story |
| `img_user_2.xml` | Green | 62dp | `item_story.xml` | `@+id/ivStoryAvatar` | siko.speed story |
| `img_user_3.xml` | Purple | 62dp | `item_story.xml` | `@+id/ivStoryAvatar` | galish story |
| `img_user_4.xml` | Amber | 62dp | `item_story.xml` | `@+id/ivStoryAvatar` | talvin story |
| `img_user_post_1.xml` | Pink | 32dp | `item_post.xml` | `@+id/ivPostUserAvatar` | __tushill post avatar |
| `img_user_placeholder.xml` | Gray | Various | All layouts | Various IDs | Default/fallback avatar |

### **Post Images**

| Image File | Type | Size | Used In | Element ID | Purpose |
|------------|------|------|---------|------------|---------|
| `img_post_1.xml` | Landscape | 450dp | `item_post.xml` | `@+id/ivPostImage` | Main post image |
| `img_post_placeholder.xml` | Generic | 450dp | `item_post.xml` | `@+id/ivPostImage` | Fallback post image |

---

## 🎯 **SHAPES & GRADIENTS**

### **Story Gradient Border**

| Shape File | Colors | Size | Used In | Element ID | Purpose |
|------------|--------|------|---------|------------|---------|
| `story_gradient_border.xml` | #FD5949 → #D6249F → #285AEB | 70dp | `item_story.xml` | `@+id/storyBorder` | Instagram-style gradient ring |

### **Page Indicators**

| Shape File | Color | Size | Used In | Purpose |
|------------|-------|------|---------|---------|
| `page_indicator_active.xml` | #2196F3 (Blue) | 6dp | `item_post.xml` | Active carousel dot |
| `page_indicator_inactive.xml` | #555555 (Gray) | 6dp | `item_post.xml` | Inactive carousel dots |

---

## 📱 **LAYOUT FILES BREAKDOWN**

### **1. activity_home.xml** (Main Screen)

**Purpose**: Instagram-like home feed with stories, posts, and navigation

**Resources Used**:
- **Top Bar**:
  - Text: "Snaplink" (cursive, 24sp)
  - Icon: `ic_messenger.xml` (26dp)

- **Stories Section**:
  - RecyclerView: Horizontal scrolling
  - Item Layout: `item_story.xml`

- **Posts Feed**:
  - RecyclerView: Vertical scrolling (✅ **Scrollable for multiple posts**)
  - Item Layout: `item_post.xml`

- **Bottom Navigation**:
  - `ic_home_filled.xml` (26dp)
  - `ic_search.xml` (26dp)
  - `ic_add_post.xml` (26dp)
  - `ic_reels.xml` (26dp)
  - `img_current_user.xml` (26dp, circular)

**Total Icons**: 6  
**Total Images**: 1  
**Scrollable**: ✅ Yes (Posts RecyclerView)

---

### **2. item_story.xml** (Story Item)

**Purpose**: Individual story avatar with gradient border

**Resources Used**:
- **Story Border**: `story_gradient_border.xml` (70dp)
- **Avatar**: User images (62dp, circular)
  - `img_current_user.xml`
  - `img_user_1.xml`
  - `img_user_2.xml`
  - `img_user_3.xml`
  - `img_user_4.xml`
  - `img_user_placeholder.xml`
- **Add Icon**: `ic_add_post.xml` (20dp, only on "Your Story")

**Total Icons**: 1  
**Total Images**: 6  
**Size**: 70dp × 70dp

---

### **3. item_post.xml** (Post Card)

**Purpose**: Complete Instagram-style post with image, actions, and comments

**Resources Used**:

**Header Section**:
- Avatar: User images (32dp)
- Icon: `ic_more_vertical.xml` (20dp)

**Post Image**:
- `img_post_1.xml` (450dp height)
- `img_post_placeholder.xml` (450dp height)

**Action Buttons**:
- `ic_heart_outline.xml` (26dp)
- `ic_comment.xml` (26dp)
- `ic_share.xml` (26dp)
- `ic_bookmark_outline.xml` (24dp)

**Likes Counter**:
- Blue background (#2196F3)
- Text: "574 x 574" (11sp, bold)

**Comment Section**:
- Avatar: `img_current_user.xml` (24dp)
- Emojis: `ic_emoji_1.xml`, `ic_emoji_2.xml` (18dp each)

**Total Icons**: 8  
**Total Images**: 3  
**Height**: ~600-650dp per post

---

## 📊 **RESOURCE SUMMARY**

### **By Type**

| Resource Type | Count | Files |
|---------------|-------|-------|
| **Icons** (Vector) | 14 | All `ic_*.xml` files |
| **User Avatars** | 7 | All `img_user_*.xml` files |
| **Post Images** | 2 | `img_post_*.xml` files |
| **Shapes/Gradients** | 3 | `story_gradient_border.xml`, `page_indicator_*.xml` |
| **Layout Files** | 3 | `activity_home.xml`, `item_story.xml`, `item_post.xml` |

### **By Color Scheme**

| Color | Hex Code | Used In |
|-------|----------|---------|
| Blue (Active) | #2196F3 | Indicators, likes counter, user avatar |
| Orange | #FF5722 | User 1 avatar |
| Green | #4CAF50 | User 2 avatar |
| Purple | #9C27B0 | User 3 avatar |
| Amber | #FF9800 | User 4 avatar |
| Pink | #E91E63 | Post user avatar |
| Gray | #555555 | Placeholders, inactive states |
| Gradient | #FD5949 → #D6249F → #285AEB | Story borders |

---

## 🔍 **QUICK REFERENCE**

### **Find Icon by Purpose**

- **Like**: `ic_heart_outline.xml`
- **Comment**: `ic_comment.xml`
- **Share**: `ic_share.xml`
- **Save**: `ic_bookmark_outline.xml`
- **More Options**: `ic_more_vertical.xml`
- **Add/Create**: `ic_add_post.xml`
- **Search**: `ic_search.xml`
- **Home**: `ic_home_filled.xml`
- **Reels**: `ic_reels.xml`
- **Messages**: `ic_messenger.xml`
- **Emoji**: `ic_emoji_1.xml`, `ic_emoji_2.xml`

### **Find Image by User**

- **Your Story**: `img_current_user.xml` (Blue)
- **punit_super**: `img_user_1.xml` (Orange)
- **siko.speed**: `img_user_2.xml` (Green)
- **galish**: `img_user_3.xml` (Purple)
- **talvin**: `img_user_4.xml` (Amber)
- **__tushill**: `img_user_post_1.xml` (Pink)
- **Default**: `img_user_placeholder.xml` (Gray)

---

## ✅ **SCROLLING CONFIGURATION**

### **Posts Feed (activity_home.xml)**

```xml
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/rvPosts"
    android:layout_height="0dp"  <!-- Fills available space -->
    android:scrollbars="vertical"  <!-- Shows scrollbar -->
    <!-- ✅ SCROLLABLE: Supports unlimited posts -->
/>
```

**Features**:
- ✅ Vertical scrolling enabled
- ✅ Fills space between stories and bottom nav
- ✅ Supports multiple posts (unlimited)
- ✅ Smooth scrolling performance
- ✅ Scrollbar visible when scrolling

### **Stories Section (activity_home.xml)**

```xml
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/rvStories"
    android:layout_height="wrap_content"  <!-- Fixed height -->
    android:orientation="horizontal"  <!-- Horizontal scroll -->
    android:nestedScrollingEnabled="false"  <!-- Optimized -->
    <!-- ✅ SCROLLABLE: Horizontal only -->
/>
```

**Features**:
- ✅ Horizontal scrolling enabled
- ✅ Fixed height (doesn't expand)
- ✅ Nested scrolling disabled for performance
- ✅ Supports multiple stories

---

## 📝 **NOTES**

1. **All icons are vector drawables** - Scale perfectly at any size
2. **All user images are vector placeholders** - Replace with real PNGs/WebPs for production
3. **Posts RecyclerView is fully scrollable** - Can display unlimited posts
4. **Stories scroll horizontally** - Independent of posts scrolling
5. **CircleImageView library required** - For circular avatars (already in build.gradle)

---

**Last Updated**: 2026-01-23  
**Version**: 1.0  
**Status**: ✅ Complete & Scrollable
