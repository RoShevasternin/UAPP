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

#Ads Module -----------------------------------------------------------------
# Зберігаємо всі data класи для Gson (RemoteConfigModel — конфіг з нашого сервера)
-keep class com.vidtools.framefall.adsmodule.** { *; }
-keepclassmembers class com.vidtools.framefall.adsmodule.** { *; }

#Business Module -----------------------------------------------------------------
# WorkManager створює Worker РЕФЛЕКСІЄЮ за ім'ям класу, збереженим у його базі в момент
# планування. Після оновлення апки мапа обфускації інша — без -keep задача старого білда
# не знайде свій клас і пуш мовчки загубиться.
-keep class com.vidtools.framefall.businesModule.push.LocalPush$PushWorker { *; }
#TikTok -----------------------------------------------------------------
-keep class com.tiktok.** { *; }
# TikTok SDK має необов'язковий трекінг покупок через Play Billing — білінгу в Framefall немає
-dontwarn com.android.billingclient.**
# Google Install Referrer
-keep class com.android.installreferrer.api.** { *; }
