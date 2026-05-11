-flattenpackagehierarchy
-ignorewarnings

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int w(...);
    public static int i(...);
    public static int d(...);
    public static int e(...);

}

-keepnames class com.robuxe.robuxtracker.freerobux.Memee.*
-keep class com.robuxe.robuxtracker.freerobux.Memee.** { *; }

-keepnames class com.robuxe.robuxtracker.freerobux.adsmodule.*
-keep class com.robuxe.robuxtracker.freerobux.adsmodule.** { *; }


-keepattributes Signature
-keepattributes Annotation
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

##---------------End: proguard configuration for Gson  ----------

# Keep all Firebase Remote Config classes
-keep class com.google.firebase.remoteconfig.** { *; }
-dontwarn com.google.firebase.remoteconfig.**

# Keep internal Firebase utility classes (safety net)
-keep class com.google.firebase.components.** { *; }
-dontwarn com.google.firebase.components.**

# Keep Gson (used internally by Firebase for JSON parsing)
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Keep annotations used by Firebase
-keep @interface com.google.firebase.remoteconfig.annotations.**

# Keep classes that may be accessed via reflection
-keepattributes *Annotation*, InnerClasses, Signature

# Keep the Gson class itself
-keep class com.google.gson.Gson { *; }
-keep class com.google.gson.GsonBuilder { *; }

# Keep all related internal Gson classes
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Keep model classes used in Gson serialization/deserialization
# Replace 'your.package.model.**' with your actual model package
-keep class your.package.model.** {
    <fields>;
    <methods>;
}

# If you use Gson annotations (e.g., @SerializedName)
-keepattributes *Annotation*