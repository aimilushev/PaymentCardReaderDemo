package com.milushev.card_read_demo

import android.nfc.tech.IsoDep
import android.util.Log
import com.github.devnied.emvnfccard.parser.IProvider

/**
 * IProvider implementation using IsoDep as a communication channel
 */
class IsoDepProvider(private val isoDep: IsoDep) : IProvider {

    private val TAG by lazy { IsoDepProvider::class.java.simpleName }

    override fun transceive(pCommand: ByteArray?): ByteArray {
        Log.d(TAG, "transceive")
        return isoDep.transceive(pCommand)
    }

    /**
     * This method is not called if your EMV template config has readAt set to false
     */
    override fun getAt(): ByteArray {
        Log.d(TAG, "get AT")
        return byteArrayOf()
    }
}