package com.redwave.downloader

import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.content.getSystemService

// ═════════════════════════════════════════════════════════════════════════════
//  LauncherTrampoline — ЛИШЕ для іконки застосунку (не для кнопки «Додому»).
//
//  Чому: на Android 10+ HOME-інтент запускає MainActivity в окремому home-таску.
//  Якби іконка вела в MainActivity напряму, у процесі жили б ДВІ MainActivity —
//  два GDX-застосунки з одними статиками Gdx.* (заміряно на Redmi 07.10.2026:
//  білі прямокутники замість шейдерів, події в «мертвий» екземпляр).
//
//  Тому: роль HOME наша → відкриваємо той самий home-таск з EXTRA_OPEN_APP
//  (GDX переходить на AppScreen без рестарту); не наша → звичайний запуск.
//  Сама Activity без UI (Theme.NoDisplay) і закривається одразу.
// ═════════════════════════════════════════════════════════════════════════════
class LauncherTrampoline : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val target = Intent(this, MainActivity::class.java).putExtra(MainActivity.EXTRA_OPEN_APP, true)
        if (isDefaultHome()) {
            target.action = Intent.ACTION_MAIN
            target.addCategory(Intent.CATEGORY_HOME)
        }
        target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(target)
        finish()
        overridePendingTransition(0, 0)
    }

    private fun isDefaultHome(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val rm = getSystemService<RoleManager>()
            if (rm != null && rm.isRoleAvailable(RoleManager.ROLE_HOME)) return rm.isRoleHeld(RoleManager.ROLE_HOME)
        }
        val home = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        return packageManager.resolveActivity(home, PackageManager.MATCH_DEFAULT_ONLY)?.activityInfo?.packageName == packageName
    }
}
