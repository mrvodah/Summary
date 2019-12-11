package com.example.summary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.model.SpeedTestError
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.SpeedTestSocket
import android.net.wifi.WifiManager
import java.lang.Exception
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val SOCKET_TIMEOUT = 5000

    private val SPEED_TEST_SERVER_URI_UL = "http://ipv4.ikoula.testdebit.info/"

    private val FILE_SIZE = 1000000

    var text: String = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Connection Info
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nc = cm.getNetworkCapabilities(cm.activeNetwork)
        val downSpeed = nc!!.linkDownstreamBandwidthKbps
        val upSpeed = nc.linkUpstreamBandwidthKbps
        Log.d("TAG", "upSpeed: $upSpeed - downSpeed: $downSpeed")


        // Wifi Info
        val wifiManager: WifiManager = this.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.getConnectionInfo()
//        Log.d("TAG", "linkSpeed: ${wifiInfo.linkSpeed} - transmit: ${wifiInfo.txLinkSpeedMbps} - receive: ${wifiInfo.rxLinkSpeedMbps}")
        Log.d("TAG", "linkSpeed: ${wifiInfo.linkSpeed}")


        // SpeedTest Info
        val speedTestSocket = SpeedTestSocket()

        //set timeout for download
        speedTestSocket.socketTimeout = SOCKET_TIMEOUT

        // 1000 bit/s = 1 kb/s
        // add a listener to wait for speedtest completion and progress
        speedTestSocket.addSpeedTestListener(object : ISpeedTestListener {

            override fun onCompletion(report: SpeedTestReport) {
                // called when download/upload is complete
                text += "\n[COMPLETED] rate in bit/s   : " + report.transferRateBit
                runOnUiThread {
                    socket.text = text
                }

                println("[COMPLETED] rate in octet/s : " + report.transferRateOctet)
                println("[COMPLETED] rate in bit/s   : " + report.transferRateBit)
            }

            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                // called when a download/upload error occur
                println("[ERROR] : $errorMessage")
            }

            override fun onProgress(percent: Float, report: SpeedTestReport) {
                // called to notify download/upload progress
                println("[PROGRESS] progress : $percent%")
                text += "\n[PROGRESS] progress : $percent%"
                text += "\n[PROGRESS] rate in bit/s   : " + report.transferRateBit
                println("[PROGRESS] rate in octet/s : " + report.transferRateOctet)
                println("[PROGRESS] rate in bit/s   : " + report.transferRateBit)
            }
        })

        val thread = Thread(Runnable {
            try {
//                speedTestSocket.startDownload(SPEED_TEST_SERVER_URI_DL)
                speedTestSocket.startUpload(SPEED_TEST_SERVER_URI_UL, FILE_SIZE)
            } catch (e: Exception){

            }
        })
        thread.start()

    }
}
