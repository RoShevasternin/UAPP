package com.rbxrush.rushrbx.util

import java.net.NetworkInterface

object NetworkUtils {

    fun isVpnConnected(): Boolean {
        return try {
            NetworkInterface.getNetworkInterfaces()
                .toList()
                .any { iface -> iface.isUp && (
                        iface.name.startsWith("tun") ||
                        iface.name.startsWith("tap") ||
                        iface.name.startsWith("ppp")
                ) }
        } catch (e: Exception) { false }
    }
}