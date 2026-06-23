package com.example.totp

import android.util.Base64
import java.lang.Math.pow
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object TOTP {

    private const val TIME_STEP = 30
    private const val DIGITS = 6
    private const val ALGO = "HmacSHA1"

    fun generate(secret: String, time: Long = System.currentTimeMillis()): String {

        val key = Base64.decode(secret, Base64.DEFAULT)
        val counter = time / 1000 / TIME_STEP

        val data = ByteArray(8)
        var value = counter

        for (i in 7 downTo 0) {
            data[i] = (value and 0xff).toByte()
            value = value shr 8
        }

        val mac = Mac.getInstance(ALGO)
        mac.init(SecretKeySpec(key, ALGO))

        val hash = mac.doFinal(data)

        val offset = hash.last().toInt() and 0x0f

        val binary =
            ((hash[offset].toInt() and 0x7f) shl 24) or
            ((hash[offset + 1].toInt() and 0xff) shl 16) or
            ((hash[offset + 2].toInt() and 0xff) shl 8) or
            (hash[offset + 3].toInt() and 0xff)

        val otp = binary % pow(10.0, DIGITS.toDouble()).toInt()

        return otp.toString().padStart(DIGITS, '0')
    }
}
