package com.vidtools.framefall.android.studio

import android.graphics.Bitmap
import java.io.OutputStream

// ═════════════════════════════════════════════════════════════════════════════
//  GifEncoder — анімований GIF89a з нескінченним повтором.
//
//  Android енкодера GIF не має, тож свій, мінімальний:
//    • одна глобальна палітра 6×7×6 = 252 кольори (зелений — 7 рівнів: око до нього
//      найчутливіше) + впорядкований дизеринг Байєра 4×4 — без «смуг» на градієнтах
//      і без квантизації на кожен кадр (60 кадрів 320 px — секунди, а не хвилини);
//    • LZW з динамічною довжиною коду (2..12 біт), як у специфікації GIF89a.
//  Розміри всіх кадрів — як у першого.
// ═════════════════════════════════════════════════════════════════════════════
class GifEncoder(private val out: OutputStream, private val delayCs: Int) {

    private var width = 0
    private var height = 0
    private var started = false

    fun addFrame(bmp: Bitmap) {
        if (!started) {
            width = bmp.width; height = bmp.height
            writeHeader()
            started = true
        }
        val px = IntArray(width * height)
        val src = if (bmp.width == width && bmp.height == height) bmp else Bitmap.createScaledBitmap(bmp, width, height, true)
        src.getPixels(px, 0, width, 0, 0, width, height)
        if (src !== bmp) src.recycle()
        val idx = ByteArray(px.size)
        for (y in 0 until height) for (x in 0 until width) {
            val c = px[y * width + x]
            val d = BAYER[(y and 3) * 4 + (x and 3)]   // -0.5..0.5 кроку
            val r = quant((c shr 16) and 0xFF, 5, d)
            val g = quant((c shr 8) and 0xFF, 6, d)
            val b = quant(c and 0xFF, 5, d)
            idx[y * width + x] = (r * 42 + g * 6 + b).toByte()
        }
        writeGraphicControl()
        writeImage(idx)
    }

    fun finish() {
        if (!started) return
        out.write(0x3B)
        out.flush()
    }

    // ------------------------------------------------------------------------
    // Блоки
    // ------------------------------------------------------------------------
    private fun writeHeader() {
        out.write("GIF89a".toByteArray(Charsets.US_ASCII))
        short(width); short(height)
        out.write(0xF7)                 // глобальна палітра, 8 біт, 256 записів
        out.write(0); out.write(0)
        for (i in 0 until 256) {
            if (i < 252) {
                val r = i / 42; val g = (i / 6) % 7; val b = i % 6
                out.write(r * 255 / 5); out.write(g * 255 / 6); out.write(b * 255 / 5)
            } else { out.write(0); out.write(0); out.write(0) }
        }
        // NETSCAPE2.0: повтор нескінченно
        out.write(byteArrayOf(0x21, 0xFF.toByte(), 0x0B))
        out.write("NETSCAPE2.0".toByteArray(Charsets.US_ASCII))
        out.write(byteArrayOf(3, 1, 0, 0, 0))
    }

    private fun writeGraphicControl() {
        out.write(byteArrayOf(0x21, 0xF9.toByte(), 4, 0x04))   // disposal = do not dispose
        short(delayCs)
        out.write(0); out.write(0)
    }

    private fun writeImage(idx: ByteArray) {
        out.write(0x2C)
        short(0); short(0); short(width); short(height)
        out.write(0)                    // без локальної палітри
        out.write(8)                    // мінімальна довжина коду LZW
        lzw(idx)
        out.write(0)
    }

    private fun short(v: Int) { out.write(v and 0xFF); out.write((v shr 8) and 0xFF) }

    // ------------------------------------------------------------------------
    // LZW
    // ------------------------------------------------------------------------
    private fun lzw(data: ByteArray) {
        val clear = 256; val eoi = 257
        val table = HashMap<Int, Int>(8192)
        var next = 258; var size = 9
        val bw = BlockWriter(out)
        bw.write(clear, size)
        var prefix = data[0].toInt() and 0xFF
        for (i in 1 until data.size) {
            val k = data[i].toInt() and 0xFF
            val key = (prefix shl 8) or k
            val hit = table[key]
            if (hit != null) { prefix = hit; continue }
            bw.write(prefix, size)
            if (next < 4096) {
                table[key] = next++
                if (next > (1 shl size) && size < 12) size++
            } else {
                bw.write(clear, size)
                table.clear(); next = 258; size = 9
            }
            prefix = k
        }
        bw.write(prefix, size)
        bw.write(eoi, size)
        bw.flush()
    }

    /** Біти LSB-first → під-блоки по 255 байт. */
    private class BlockWriter(private val out: OutputStream) {
        private val buf = ByteArray(255)
        private var n = 0
        private var acc = 0
        private var bits = 0
        fun write(code: Int, size: Int) {
            acc = acc or (code shl bits); bits += size
            while (bits >= 8) { put(acc and 0xFF); acc = acc ushr 8; bits -= 8 }
        }
        private fun put(b: Int) { buf[n++] = b.toByte(); if (n == 255) dump() }
        private fun dump() { if (n > 0) { out.write(n); out.write(buf, 0, n); n = 0 } }
        fun flush() { if (bits > 0) { put(acc and 0xFF); acc = 0; bits = 0 }; dump() }
    }

    private companion object {
        val BAYER = floatArrayOf(0f, 8f, 2f, 10f, 12f, 4f, 14f, 6f, 3f, 11f, 1f, 9f, 15f, 7f, 13f, 5f).map { it / 16f - 0.5f }.toFloatArray()
        fun quant(v: Int, levels: Int, dither: Float): Int =
            (v / 255f * levels + dither).let { Math.round(it) }.coerceIn(0, levels)
    }
}
