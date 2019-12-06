package com.milushev.card_read_demo

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.devnied.emvnfccard.parser.EmvTemplate

/**
 * Ultra simplified NFC credit card reader implemented using https://github.com/devnied/EMV-NFC-Paycard-Enrollment
 * library.
 */
class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private val TAG by lazy { MainActivity::class.java.simpleName }

    private lateinit var creditCardTextView: TextView
    private lateinit var cardExpiryTextView: TextView

    private var nfcAdapter: NfcAdapter? = null

    var config: EmvTemplate.Config = EmvTemplate.Config()
        .setContactLess(true) // Enable contact less reading (default: true)
        .setReadAllAids(true) // Read all aids in card (default: true)
        .setReadTransactions(false) // Read all transactions
        .setRemoveDefaultParsers(false) // Remove default parsers for GeldKarte and EmvCard (default: false)
        .setReadAt(false) // Read and extract ATR/ATS and description

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        creditCardTextView = findViewById(R.id.cardNumber)
        cardExpiryTextView = findViewById(R.id.expiryDate)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    public override fun onResume() {
        super.onResume()
        nfcAdapter?.enableReaderMode(
            this, this,
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null
        )
    }

    public override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    override fun onTagDiscovered(tag: Tag?) {
        Log.d(TAG, "onTagDiscovered")
        val isoDep = IsoDep.get(tag)
        isoDep.connect()

        // Create Parser
        val parser = EmvTemplate.Builder()
            .setProvider(IsoDepProvider(isoDep)) // Define provider
            .setConfig(config) // Define config
            .build()

        // Read card
        val emvCard = parser.readEmvCard()

        isoDep.close()

        Log.d(TAG, "card detected $emvCard")

        runOnUiThread {
            creditCardTextView.text = getString(R.string.card_number, emvCard.cardNumber)
            cardExpiryTextView.text = getString(R.string.expiry_date, emvCard.expireDate.toString())
        }

    }
}
