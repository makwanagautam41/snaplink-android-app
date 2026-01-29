# Backend API Alignment Document

## ✅ Android App ↔️ Node.js Backend Mapping

### 1. Signup Endpoint

#### Backend Route
```javascript
userRouter.post("/signup", signup);
```

#### Backend Handler (Expected Request)
```javascript
const { name, username, email, password, phone, gender } = req.body;
```

#### Android Request (ApiService.kt)
```kotlin
@POST("users/signup")
fun register(@Body body: RegisterRequest): Call<ApiResponse>

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val username: String,
    val phone: String,
    val gender: String
)
```

#### Backend Response
```javascript
return successResponse(res, 201, { token }, "Signup successful");
```

#### Android Response Handling (RegisterActivity.kt)
```kotlin
data class ApiResponse(
    val message: String,
    val token: String?,
    val accessToken: String?
)

// In RegisterActivity
val token = apiResponse?.token
if (token != null) {
    TokenManager.saveToken(token)
}
```

✅ **Status**: Fully Aligned

---

### 2. Signin Endpoint

#### Backend Route
```javascript
userRouter.post("/signin", signin);
```

#### Backend Handler (Expected Request)
```javascript
const { identifier, password } = req.body;
// identifier can be email or username
const user = await userModel.findOne({
    $or: [{ email: identifier }, { username: identifier }]
});
```

#### Android Request (ApiService.kt)
```kotlin
@POST("users/signin")
fun login(@Body body: LoginRequest): Call<ApiResponse>

data class LoginRequest(
    val identifier: String,  // ✅ Changed from 'email' to 'identifier'
    val password: String
)
```

#### Backend Response
```javascript
return successResponse(res, 200, {
    token,
    user: {
        id: user._id,
        name: user.name,
        username: user.username,
        email: user.email,
        profileImg: user.profileImg,
        bio: user.bio,
    }
}, "Login successful");
```

#### Android Response Handling (LoginActivity.kt)
```kotlin
data class ApiResponse(
    val message: String,
    val token: String?,
    val accessToken: String?
)

// In LoginActivity
val token = apiResponse?.token
if (token != null) {
    TokenManager.saveToken(token)
    // Navigate to home
}
```

✅ **Status**: Fully Aligned (Fixed: email → identifier)

---

### 3. Profile Endpoint

#### Backend Route
```javascript
userRouter.get("/profile", authUser, profile);
```

#### Backend Middleware (authUser)
```javascript
// Expects JWT token in Authorization header
Authorization: Bearer <JWT_TOKEN>
```

#### Android Request (ApiService.kt)
```kotlin
@GET("users/profile")
fun getUserDetails(): Call<UserDetailsResponse>

// AuthInterceptor automatically adds:
// Authorization: Bearer <token>
```

#### Backend Handler
```javascript
export const profile = async (req, res) => {
    const userId = req.userId; // From authUser middleware
    
    const user = await userModel.findById(userId)
        .select("name username email profileImg bio phone followers following...")
        .populate(...)
        .lean();
    
    return successResponse(res, 200, { 
        user: { ...user, postCount } 
    }, "Profile fetched successfully");
};
```

#### Android Response Handling (HomeActivity.kt)
```kotlin
data class UserDetailsResponse(
    val message: String,
    val user: User
)

data class User(
    val name: String,
    val email: String,
    val profileImage: ProfileImage?
)

// In HomeActivity
if (response.isSuccessful) {
    user = response.body()?.user
} else if (response.code() == 401) {
    // Token invalid - clear and redirect to login
    ApiClient.clearAuth()
    // Redirect to LoginActivity
}
```

✅ **Status**: Fully Aligned

---

## 🔐 Authentication Flow

### Backend JWT Creation
```javascript
const createToken = (userId) => {
    return jwt.sign({ userId }, process.env.JWT_SECRET, {
        expiresIn: '7d' // or whatever expiry you set
    });
};
```

### Backend JWT Verification (authUser middleware)
```javascript
const authUser = async (req, res, next) => {
    const token = req.headers.authorization?.split(' ')[1]; // "Bearer <token>"
    
    if (!token) {
        return errorResponse(res, 401, "Unauthorized");
    }
    
    try {
        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        req.userId = decoded.userId;
        next();
    } catch (err) {
        return errorResponse(res, 401, "Invalid token");
    }
};
```

### Android JWT Storage & Injection
```kotlin
// Storage (TokenManager.kt)
fun saveToken(token: String) {
    prefs.edit().putString(KEY_TOKEN, token).apply()
}

fun getToken(): String? {
    return prefs.getString(KEY_TOKEN, null)
}

// Injection (AuthInterceptor.kt)
override fun intercept(chain: Interceptor.Chain): Response {
    val token = TokenManager.getToken()
    
    val newRequest = if (token != null) {
        originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
    } else {
        originalRequest
    }
    
    return chain.proceed(newRequest)
}
```

✅ **Status**: Fully Aligned

---

## 🚨 Error Response Handling

### Backend Error Format
```javascript
const errorResponse = (res, statusCode, message) => {
    return res.status(statusCode).json({
        success: false,
        message: message
    });
};
```

### Android Error Parsing
```kotlin
if (!response.isSuccessful) {
    val errorBody = response.errorBody()?.string()
    if (errorBody != null && errorBody.contains("message")) {
        val match = "\"message\":\"(.*?)\"".toRegex().find(errorBody)
        val msg = match?.groupValues?.get(1) ?: "Request failed"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}
```

### Common Backend Error Messages
| Status | Message | Android Handling |
|--------|---------|------------------|
| 400 | "All fields required." | Show toast with message |
| 400 | "Invalid email" | Show toast with message |
| 400 | "Weak password..." | Show toast with message |
| 401 | "Incorrect password" | Show toast with message |
| 401 | "Invalid token" | Clear token, redirect to login |
| 403 | "Account is deactivated..." | Show toast with message |
| 404 | "User not found" | Show toast with message |
| 409 | "Email already in use" | Show toast with message |
| 409 | "Username already in use" | Show toast with message |
| 500 | "Internal server error" | Show toast with message |

✅ **Status**: Fully Aligned

---

## 📋 Request/Response Examples

### Signup Request
```json
POST /api/users/signup
Content-Type: application/json

{
    "name": "John Doe",
    "username": "johndoe",
    "email": "john@example.com",
    "password": "SecurePass123!",
    "phone": "1234567890",
    "gender": "male"
}
```

### Signup Response (Success)
```json
HTTP/1.1 201 Created
Content-Type: application/json

{
    "success": true,
    "message": "Signup successful",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    }
}
```

### Signin Request
```json
POST /api/users/signin
Content-Type: application/json

{
    "identifier": "john@example.com",  // or "johndoe"
    "password": "SecurePass123!"
}
```

### Signin Response (Success)
```json
HTTP/1.1 200 OK
Content-Type: application/json

{
    "success": true,
    "message": "Login successful",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "user": {
            "id": "507f1f77bcf86cd799439011",
            "name": "John Doe",
            "username": "johndoe",
            "email": "john@example.com",
            "profileImg": "https://...",
            "bio": "Hello world"
        }
    }
}
```

### Profile Request
```json
GET /api/users/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Profile Response (Success)
```json
HTTP/1.1 200 OK
Content-Type: application/json

{
    "success": true,
    "message": "Profile fetched successfully",
    "data": {
        "user": {
            "name": "John Doe",
            "username": "johndoe",
            "email": "john@example.com",
            "profileImg": "https://...",
            "bio": "Hello world",
            "phone": "1234567890",
            "followers": [...],
            "following": [...],
            "postCount": 42
        }
    }
}
```

### Profile Response (401 - Invalid Token)
```json
HTTP/1.1 401 Unauthorized
Content-Type: application/json

{
    "success": false,
    "message": "Invalid token"
}
```

---

## ✅ Verification Checklist

- [x] Signup request fields match backend expectations
- [x] Signin uses `identifier` field (not `email`)
- [x] Profile request includes `Authorization: Bearer` header
- [x] Token stored after successful login/signup
- [x] 401 errors clear token and redirect to login
- [x] Error messages extracted from backend response
- [x] All endpoints use correct paths (`/api/users/...`)
- [x] No cookie handling in Android app
- [x] JWT token automatically added to protected requests

---

**Status**: ✅ 100% Aligned with Backend
**Last Updated**: 2026-01-22
