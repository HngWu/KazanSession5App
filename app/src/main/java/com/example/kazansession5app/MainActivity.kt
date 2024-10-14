package com.example.kazansession5app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kazansession5app.HttpService.httpgetwells
import com.example.kazansession5app.Models.RockType
import com.example.kazansession5app.Models.Well
import com.example.kazansession5app.Models.WellLayer
import com.example.kazansession5app.ui.theme.KazanSession5AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "addWell") {
                composable("well") {
                    WellsScreen(
                        navController = navController,
                    )
                }
                composable("addWell") {
                    RegisterNewWellScreen(
                        navController = navController,
                    )
                }
            }



        }
    }

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegisterNewWellScreen(navController: NavController) {
    var wellName by remember { mutableStateOf("") }
    var wellType by remember { mutableStateOf("") }
    var gasOilDepth by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var layers by remember { mutableStateOf(mutableListOf<WellLayer>()) }
    var layerName by remember { mutableStateOf("") }
    var fromDepth by remember { mutableStateOf("") }
    var toDepth by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val rockTypes = mapOf(
        1 to RockType("Argillite", "#E52B50"),
        2 to RockType("Breccia", "#FFBF00"),
        3 to RockType("Chalk", "#9966CC"),
        4 to RockType("Chert", "#FBCEB1"),
        5 to RockType("Coal", "#7FFFD4"),
        6 to RockType("Conglomerate", "#007FFF"),
        7 to RockType("Dolomite", "#0095B6"),
        8 to RockType("Limestone", "#800020"),
        9 to RockType("Marl", "#DE3163"),
        10 to RockType("Mudstone", "#F7E7CE"),
        11 to RockType("Sandstone", "#7FFF00"),
        12 to RockType("Shale", "#C8A2C8"),
        13 to RockType("Tufa", "#BFFF00"),
        14 to RockType("Wackestone", "#FFFF00")
    )
    val wellTypes = mapOf(
        1 to "Well",
        2 to "Section"
    )


    fun validateAndAddLayer() {
        val from = fromDepth.toIntOrNull()
        val to = toDepth.toIntOrNull()
        val depth = gasOilDepth.toIntOrNull()

        if (from == null || to == null || from >= to || to - from < 100) {
            errorMessage = "Invalid layer depths. Ensure 'From' is less than 'To' and the depth is at least 100."
            return
        }

        if (depth != null && to > depth) {
            errorMessage = "Layer depth cannot exceed the depth of gas or oil extraction."
            return
        }

        if (layers.any { it.rockName == layerName }) {
            errorMessage = "Layer names must be unique within the same well."
            return
        }

        if (layers.any { (from in it.startPoint until it.endPoint) || (to in it.startPoint until it.endPoint) }) {
            errorMessage = "Layers cannot overlap."
            return
        }

        if (layers.isEmpty() && from != 0) {
            errorMessage = "The first layer must start at depth 0."
            return
        }

        layers.add(WellLayer(0, 0, 0, from, to, layerName, "#FFFFFF"))
        fromDepth = ""
        toDepth = ""
        layerName = ""
        errorMessage = null
    }

    fun validateAndSubmit() {
        if (wellName.isBlank() || wellType.isBlank() || gasOilDepth.isBlank() || capacity.isBlank()) {
            errorMessage = "All required fields must be filled."
            return
        }

        if (layers.isEmpty() || layers.none { it.startPoint == 0 }) {
            errorMessage = "There must be at least one layer starting at depth 0."
            return
        }

        // Save the well to the database (pseudo code)
        // saveWellToDatabase(Well(...))

        navController.navigate("home")
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TextField(
                value = wellName,
                onValueChange = { wellName = it },
                label = { Text("Well Name") },
                modifier = Modifier.width(250.dp)
            )

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ){
                TextField(
                    value = gasOilDepth,
                    onValueChange = { gasOilDepth = it },
                    label = { Text("Depth of Gas/Oil Extraction") },
                    modifier = Modifier.width(200.dp).padding(end = 8.dp)
                )
                TextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("Well Capacity") },
                    modifier = Modifier.width(200.dp)
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            Text("Rock Layers", style = MaterialTheme.typography.bodyLarge)
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            )
            {
                DropDownMenu(
                    items = rockTypes.values.select { it.rockName },
                    name = "Layer Name",
                    selectedItem = layerName,
                    onItemSelected = { layerName = it },
                    width = 200
                )
                DropDownMenu(
                    items = wellTypes.values.toList(),
                    name = "Layer Name",
                    selectedItem = wellType,
                    onItemSelected = { wellType = it },
                    width = 200
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    value = fromDepth,
                    onValueChange = { fromDepth = it },
                    label = { Text("From Depth") },
                    modifier = Modifier.width(120.dp)
                )
                TextField(
                    value = toDepth,
                    onValueChange = { toDepth = it },
                    label = { Text("To Depth") },
                    modifier = Modifier.width(120.dp)
                )
                Button(
                    onClick = { validateAndAddLayer() },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text("Add Layer")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            layers.forEachIndexed { index, layer ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${layer.rockName}: ${layer.startPoint} - ${layer.endPoint}")
                    Button(onClick = { layers.removeAt(index) }) {
                        IconButton(
                            onClick = { layers.removeAt(index) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete Layer")
                        }

                    }
                }
            }




            Spacer(modifier = Modifier.height(16.dp))

            errorMessage?.let {
                Text(it, color = Color.Red)
            }

            Button(
                onClick = {
                    validateAndSubmit()
                    if (errorMessage == null) {
                        val newWell = Well(
                            0,
                            wellTypes.filterValues { it == wellType }.keys.first(),
                            wellName,
                            gasOilDepth.toInt(),
                            capacity.toInt(),
                            layers,
                            wellType
                        )



                    }

                          },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Submit")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun WellsScreen(navController: NavController) {
    var wellList by remember { mutableStateOf<List<Well>>(emptyList()) }
    var selectedWell by remember { mutableStateOf<Well?>(null) }
    var httpgetwells = remember { httpgetwells() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val fetchedWells = httpgetwells.getFunction()
            if (fetchedWells != null) {
                wellList = fetchedWells
                if (fetchedWells.isNotEmpty()) {
                    selectedWell = fetchedWells[0]
                }
            }
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ){
                DropDownMenu(
                    items = wellList.map { it.wellName },
                    name = "Select Well",
                    selectedItem = selectedWell?.wellName ?: "",
                    width = 200,
                    onItemSelected = { wellName ->
                        selectedWell = wellList.find { it.wellName == wellName }
                    }
                )
                Button(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {

                    Text("Edit")
                    }
            }




            Spacer(modifier = Modifier.height(16.dp))

            selectedWell?.let { well ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    item {
                        Text(text = "Well Name: ${well.wellName}", style = MaterialTheme.typography.labelLarge)
                    }
                    itemsIndexed(well.wellLayers) { index, layer ->
                        val isLastLayer = index == well.wellLayers.size - 1
                        val backgroundColor =Color(android.graphics.Color.parseColor(layer.backgroundColor))
                        val textColor = Color.Black

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* Navigate to layer details screen */ }
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(((layer.endPoint - layer.startPoint) / 8).dp.coerceAtLeast(50.dp))
                                    .background(backgroundColor)
                                    .width(320.dp)
                            ) {
                                Text(
                                    text = layer.rockName,
                                    color = textColor,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .fillMaxHeight(), // Ensure the column fills the height of the row
                            ) {
//

                                Text(
                                    text = layer.startPoint.toString(),
                                    color = textColor,
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                if (index == well.wellLayers.size - 1) {
                                    Text(
                                        text = layer.endPoint.toString(),
                                        color = textColor,
                                        modifier = Modifier.align(Alignment.Start).padding(top = (((layer.endPoint - layer.startPoint) / 8)-36).dp.coerceAtLeast(18.dp)
                                        )
                                    )
                                }

                            }
                        }
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .height(55.dp)
                                .background(Color.Black)
                                .width(320.dp)
                        ) {
                            Text(
                                text = "Oil/Gas",
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                    item {
                        Row (
                            modifier = Modifier.fillMaxWidth().padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,

                        ){
                            Text(
                                text = "Capacity: ${well.capacity} m3",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            FloatingActionButton(
                                onClick = {
                                    navController.navigate("addWell")
                                },
                                modifier = Modifier.align(Alignment.Bottom),
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Add Layer")
                            }
                        }
                    }

                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(items: List<String>, name: String, selectedItem: String,width: Int, onItemSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .width(width.dp)
            .padding(5.dp) // Adjust the width as needed
    ) {
        TextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            label = { Text(name) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        expanded = false
                        onItemSelected(item)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KazanSession5AppTheme {
        //Greeting("Android")
    }
}