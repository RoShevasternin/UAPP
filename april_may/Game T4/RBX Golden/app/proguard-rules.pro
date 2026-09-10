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

#Ads Module -----------------------------------------------------------------
# Зберігаємо всі data класи для Gson
-keep class com.rbxgolden.fungamems.adsmodule.** { *; }
-keepclassmembers class com.rbxgolden.fungamems.adsmodule.** { *; }

#Business Module -----------------------------------------------------------------
# WorkManager створює Worker РЕФЛЕКСІЄЮ за іменем класу, збереженим у його базі
# в момент планування. Після оновлення апки карта обфускації змінюється, і
# задача, запланована старим білдом, не знаходить свій клас — пуш тихо губиться.
-keep class com.rbxgolden.fungamems.businesModule.push.LocalPush$PushWorker { *; }

#TikTok -----------------------------------------------------------------
-keep class com.tiktok.** { *; }
# Google Play Billing Library
-keep class com.android.billingclient.api.** { *; }
-dontwarn com.android.billingclient.**
# Google Install Referrer
-keep class com.android.installreferrer.api.** { *; }
# Android Lifecycle
-keep class androidx.lifecycle.** { *; }