package com.example.kazansession5app.HttpService

import com.example.kazansession5app.Models.Well
import com.example.kazansession5app.Models.WellLayer
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class httpgetwells {
    fun getFunction(): MutableList<Well>? {
        val url = URL("http://10.0.2.2:5181/Well")

        try {
            val con = url.openConnection() as HttpURLConnection ?: return null
            con.requestMethod = "GET"
            con.setRequestProperty("Content-Type", "application/json; utf-8")
            con.setRequestProperty("Accept", "application/json")
            con.connectTimeout = 1000


            val status = con.responseCode
            if (status == 200) {
                val reader = BufferedReader(InputStreamReader(con.inputStream))
                val jsonData = reader.use { it.readText() }
                reader.close()

                val jsonArray = JSONArray(jsonData)
                val objectList = mutableListOf<Well>()

                for (i in 0 until jsonArray.length()) {
                    val taskObject = jsonArray.getJSONObject(i)
                    val wellLayerList = mutableListOf<WellLayer>()
                    val wellLayerListObject = taskObject.getJSONArray("wellLayers")

                    for (j in 0 until wellLayerListObject.length()) {
                        val wellLayerObject = wellLayerListObject.getJSONObject(j)
                        val wellLayer = WellLayer(
                            wellLayerObject.getInt("id"),
                            wellLayerObject.getInt("wellId"),
                            wellLayerObject.getInt("rockTypeId"),
                            wellLayerObject.getInt("startPoint"),
                            wellLayerObject.getInt("endPoint"),
                            wellLayerObject.getString("rockName") ,
                            wellLayerObject.getString("rockColor")

                        )
                        wellLayerList.add(wellLayer)
                    }


                    val well = Well(
                        taskObject.getInt("id"),
                        taskObject.getInt("wellTypeId"),
                        taskObject.getString("wellName") ?: "",
                        taskObject.getInt("gasOilDepth"),
                        taskObject.getInt("capacity"),
                        wellLayerList,
                        taskObject.getString("wellTypeName"),
                        )
                    objectList.add(well)
                }

                return objectList
            }
            con.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return null
    }


}