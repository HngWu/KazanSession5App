package com.example.kazansession5app.HttpService

import android.os.Handler
import android.os.Looper
import com.example.kazansession5app.Models.Well
import com.example.kazansession5app.Models.WellLayer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class httpeditwell {
    fun postFunction(well: Well, onSuccess: (Boolean) -> Unit, onFailure: (Throwable) -> Unit) {
        val url = URL("http://10.0.2.2:5181/Well/edit")

        try {
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.setRequestProperty("Content-Type", "application/json; utf-8")
            con.setRequestProperty("Accept", "application/json")
            con.doOutput = true

            val json = Json.encodeToString(well)
            val os = OutputStreamWriter(con.outputStream)

            os.write(json)
            os.flush()
            os.close()

            val status = con.responseCode
            if (status == 200) {

                Handler(Looper.getMainLooper()).post() {
                    onSuccess(true)
                }
            } else {
                Handler(Looper.getMainLooper()).post{
                    onFailure(Throwable("Post asset failed"))
                }
            }
        }catch (e: Exception) {
            onFailure(e)
        }
    }

}