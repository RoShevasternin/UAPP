# libGDX: нативні методи й бекенд, які шукаються рефлексією
-keep class com.badlogic.gdx.backends.android.** { *; }
-keepclassmembers class com.badlogic.gdx.** { native <methods>; }
