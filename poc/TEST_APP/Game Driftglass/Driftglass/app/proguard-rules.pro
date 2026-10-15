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

# kotlinx.serialization: стан апки (AppState) — у JSON
-keepattributes *Annotation*, InnerClasses
-keepclassmembers @kotlinx.serialization.Serializable class com.driftglass.home.** { *; }

#Ads Module -----------------------------------------------------------------
# Зберігаємо всі data класи для Gson (RemoteConfigModel — конфіг з нашого сервера)
-keep class com.driftglass.home.adsmodule.** { *; }
-keepclassmembers class com.driftglass.home.adsmodule.** { *; }

#Business Module -----------------------------------------------------------------
# WorkManager створює Worker РЕФЛЕКСІЄЮ за ім'ям класу, збереженим у його базі в момент
# планування. Після оновлення апки мапа обфускації інша — без -keep задача старого білда
# не знайде свій клас і пуш мовчки загубиться.
-keep class com.driftglass.home.businesModule.push.LocalPush$PushWorker { *; }
#TikTok -----------------------------------------------------------------
-keep class com.tiktok.** { *; }
# TikTok SDK має необов'язковий трекінг покупок через Play Billing — білінгу в Driftglass немає
-dontwarn com.android.billingclient.**
# Google Install Referrer
-keep class com.android.installreferrer.api.** { *; }
