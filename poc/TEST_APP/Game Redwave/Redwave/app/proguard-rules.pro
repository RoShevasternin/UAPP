#LibGDX -----------------------------------------------------------------
-dontwarn javax.annotation.Nullable

-verbose

-dontwarn android.support.**
-dontwarn com.badlogic.gdx.backends.android.AndroidFragmentApplication

-keep public class com.badlogic.gdx.scenes.scene2d.** { *; }
-keep public class com.badlogic.gdx.graphics.g2d.BitmapFont { *; }
-keep public class com.badlogic.gdx.graphics.Color { *; }

-keepattributes LineNumberTable,SourceFile
-renamesourcefileattribute SourceFile

# ParticleEmitter
-keepclassmembers class com.badlogic.gdx.graphics.g2d.ParticleEmitter {
    *** particles;
    boolean[] active;
}

#Media3 / OkHttp ----------------------------------------------------------
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
