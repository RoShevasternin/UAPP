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
-keep class com.rbxhubpro.rohumex.adsmodule.** { *; }
-keepclassmembers class com.rbxhubpro.rohumex.adsmodule.** { *; }

#Business Module -----------------------------------------------------------------
# WorkManager создаёт Worker'а РЕФЛЕКСИЕЙ по имени класса, которое сохранено в
# его базе в момент планирования. Правило -keep из work-runtime защищает только
# конструктор; после обновления приложения карта обфускации меняется, и задача,
# запланированная старым билдом, не находит свой класс — уведомление молча
# теряется. Имя фиксируем: релиз с minify включён.
-keep class com.rbxhubpro.rohumex.businesModule.push.LocalPush$PushWorker { *; }

#TikTok -----------------------------------------------------------------
-keep class com.tiktok.** { *; }
# Google Play Billing Library
-keep class com.android.billingclient.api.** { *; }
-dontwarn com.android.billingclient.**
# Google Install Referrer
-keep class com.android.installreferrer.api.** { *; }
# Android Lifecycle
-keep class androidx.lifecycle.** { *; }