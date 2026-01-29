# Quick Reference: JWT Authentication Implementation

## 🔑 Key Components

### 1. TokenManager (Token Storage)
**Location**: `network/TokenManager.kt`

```kotlin
// Initialize (done in Application.onCreate)
TokenManager.init(context)

// Save token after login/signup
TokenManager.saveToken(token)

// Get token
val token = TokenManager.getToken()

// Check if logged in
if (TokenManager.isLoggedIn()) { ... }

// Clear token (logout)
TokenManager.clearToken()
```

### 2. AuthInterceptor (Automatic Header Injection)
**Location**: `network/AuthInterceptor.kt`

- Automatically adds `Authorization: Bearer <token>` to all requests
- Excludes public endpoints: `/users/signin`, `/users/signup`
- No manual intervention needed - works automatically!

### 3. ApiClient (Retrofit Setup)
**Location**: `network/ApiClient.kt`

```kotlin
// Logout
ApiClient.clearAuth()

// API calls work automatically with token
ApiClient.api.getUserDetails() // Token added automatically
```

## 📝 API Request/Response Format

### Login
```kotlin
// Request
LoginRequest(
    identifier = "email@example.com", // or username
    password = "password123"
)

// Response
{
    "success": true,
    "message": "Login successful",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "user": {
            "id": "...",
            "name": "...",
            "email": "...",
            ...
        }
    }
}
```

### Signup
```kotlin
// Request
RegisterRequest(
    name = "John Doe",
    username = "johndoe",
    email = "john@example.com",
    password = "SecurePass123!",
    phone = "1234567890",
    gender = "male"
)

// Response
{
    "success": true,
    "message": "Signup successful",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    }
}
```

### Profile (Protected)
```kotlin
// Request - No body needed
// Headers automatically include: Authorization: Bearer <token>

// Response
{
    "success": true,
    "message": "Profile fetched successfully",
    "data": {
        "user": {
            "id": "...",
            "name": "...",
            "username": "...",
            "email": "...",
            "profileImg": "...",
            "bio": "...",
            ...
        }
    }
}
```

## 🔄 Authentication Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                     App Startup                              │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
                  ┌──────────────────┐
                  │ TokenManager.    │
                  │ isLoggedIn()?    │
                  └──────────────────┘
                     │              │
                 Yes │              │ No
                     ▼              ▼
              ┌──────────┐    ┌──────────┐
              │  Verify  │    │  Login   │
              │  Token   │    │  Screen  │
              └──────────┘    └──────────┘
                     │              │
              Valid  │              │ Login Success
                     ▼              ▼
              ┌─────────────────────────┐
              │   Store Token           │
              │   TokenManager.save()   │
              └─────────────────────────┘
                            │
                            ▼
              ┌─────────────────────────┐
              │   Home Screen           │
              │   (Profile loads)       │
              └─────────────────────────┘
                            │
                            │ Logout
                            ▼
              ┌─────────────────────────┐
              │   Clear Token           │
              │   ApiClient.clearAuth() │
              └─────────────────────────┘
                            │
                            ▼
              ┌─────────────────────────┐
              │   Login Screen          │
              └─────────────────────────┘
```

## ⚠️ Error Handling

### 401 Unauthorized (Token Invalid/Expired)
```kotlin
if (response.code() == 401) {
    // Automatically handled:
    // 1. Clear token
    ApiClient.clearAuth()
    
    // 2. Redirect to login
    val intent = Intent(context, LoginActivity::class.java)
    intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}
```

### Other Errors (400, 404, 409, 500, etc.)
```kotlin
// Extract error message from backend
val errorBody = response.errorBody()?.string()
val match = "\"message\":\"(.*?)\"".toRegex().find(errorBody)
val message = match?.groupValues?.get(1) ?: "Request failed"
Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
```

## 🧪 Testing Commands

### Test Login
```bash
# Using email
curl -X POST http://10.0.2.2:5000/api/users/signin \
  -H "Content-Type: application/json" \
  -d '{"identifier":"test@example.com","password":"Test123!"}'

# Using username
curl -X POST http://10.0.2.2:5000/api/users/signin \
  -H "Content-Type: application/json" \
  -d '{"identifier":"testuser","password":"Test123!"}'
```

### Test Signup
```bash
curl -X POST http://10.0.2.2:5000/api/users/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Test User",
    "username":"testuser",
    "email":"test@example.com",
    "password":"Test123!",
    "phone":"1234567890",
    "gender":"male"
  }'
```

### Test Profile (Protected)
```bash
curl -X GET http://10.0.2.2:5000/api/users/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## 🐛 Common Issues & Solutions

### Issue: "No token received"
**Cause**: Backend didn't return token in response
**Solution**: Check backend response format, ensure it returns `{ token: "..." }`

### Issue: "401 Unauthorized" on profile
**Cause**: Token expired or invalid
**Solution**: App automatically clears token and redirects to login

### Issue: Token not persisting
**Cause**: TokenManager not initialized
**Solution**: Ensure `TokenManager.init()` is called in `Application.onCreate()`

### Issue: Login with username not working
**Cause**: Using old `email` field instead of `identifier`
**Solution**: Already fixed - now uses `identifier` field

## 📦 Files You Can Delete

These files are no longer needed:
- ❌ `network/PersistentCookieJar.kt` (cookie handling removed)

## ✅ Verification Checklist

- [x] TokenManager created and initialized
- [x] AuthInterceptor created and added to OkHttp
- [x] ApiClient updated (removed cookies, added interceptor)
- [x] LoginRequest uses `identifier` field
- [x] LoginActivity stores token on success
- [x] RegisterActivity stores token on success
- [x] HomeActivity handles 401 errors
- [x] Logout clears token properly
- [x] No UI/XML changes made
- [x] All dependencies present in build.gradle

---

**Status**: ✅ Ready to Test
**Next Step**: Build and run the app, test login/signup/profile/logout flows
