# Numrise Production ProGuard Rules
# Optimized for Jetpack Compose, Kotlin Coroutines, Retrofit, Moshi, Room, and CameraX.

# -----------------------------------------------------------------------------------
# General Rules
# -----------------------------------------------------------------------------------

# Preserve line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# Preserve Annotations and Signatures for Reflection-based libraries
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# -----------------------------------------------------------------------------------
# Jetpack Compose
# -----------------------------------------------------------------------------------
-keepclassmembers class androidx.compose.ui.platform.ComposeView {
   public *;
}
-keep class androidx.compose.material.icons.** { *; }

# -----------------------------------------------------------------------------------
# Kotlin Coroutines
# -----------------------------------------------------------------------------------
# Keep internal dispatcher names for runtime resolution
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-dontwarn kotlinx.coroutines.**

# -----------------------------------------------------------------------------------
# Room Persistence Library
# -----------------------------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
# Keep DAOs and Entities
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# -----------------------------------------------------------------------------------
# Retrofit / OkHttp
# -----------------------------------------------------------------------------------
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# -----------------------------------------------------------------------------------
# Moshi (JSON Serialization)
# -----------------------------------------------------------------------------------
# Retain generic type information and JsonClass constructors
-keep class com.squareup.moshi.* { *; }
-keep class kotlin.reflect.jvm.internal.** { *; }
-keep @com.squareup.moshi.JsonQualifier interface *
-keep @com.squareup.moshi.JsonClass class * {
    <init>(...);
}
# Keep generated adapters
-keep class *JsonAdapter { *; }

# -----------------------------------------------------------------------------------
# CameraX
# -----------------------------------------------------------------------------------
-keep class androidx.camera.core.** { *; }
-keep class androidx.camera.camera2.** { *; }
-keep class androidx.camera.lifecycle.** { *; }
-keep class androidx.camera.view.** { *; }

# -----------------------------------------------------------------------------------
# Google Play Services & Billing
# -----------------------------------------------------------------------------------
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.android.billingclient.** { *; }
-dontwarn com.android.billingclient.**

# -----------------------------------------------------------------------------------
# Coil (Image Loading)
# -----------------------------------------------------------------------------------
-keep class coil.** { *; }
-keepclassmembers class * extends coil.decode.Decoder {
    public <init>(...);
}
-keepclassmembers class * extends coil.fetch.Fetcher {
    public <init>(...);
}

# -----------------------------------------------------------------------------------
# Numrise Data Models (Crucial for Obfuscation)
# -----------------------------------------------------------------------------------
# Prevent Moshi/Room from failing due to renamed fields
-keepclassmembers class com.jn.numrise.data.model.** { *; }
-keep @com.squareup.moshi.JsonClass class com.jn.numrise.data.model.** { *; }