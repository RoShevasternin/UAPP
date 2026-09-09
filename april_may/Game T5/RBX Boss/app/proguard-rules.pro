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
-keep class com.bossrbx.rbxcalculator.adsmodule.** { *; }
-keepclassmembers class com.bossrbx.rbxcalculator.adsmodule.** { *; }

#Business Module -----------------------------------------------------------------
# WorkManager створює Worker РЕФЛЕКСІЄЮ за іменем класу, збереженим у його базі
# в момент планування. Правило -keep з work-runtime захищає лише конструктор;
# після оновлення апки карта обфускації змінюється, і задача, запланована старим
# білдом, не знаходить свій клас — нотифікація мовчки губиться.
-keep class com.bossrbx.rbxcalculator.businesModule.push.LocalPush$PushWorker { *; }

#TikTok -----------------------------------------------------------------
-keep class com.tiktok.** { *; }
# Google Play Billing Library
-keep class com.android.billingclient.api.** { *; }
-dontwarn com.android.billingclient.**
# Google Install Referrer
-keep class com.android.installreferrer.api.** { *; }
# Android Lifecycle
-keep class androidx.lifecycle.** { *; }