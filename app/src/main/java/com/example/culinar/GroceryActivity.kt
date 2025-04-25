package com.example.culinar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.culinar.ui.theme.CulinarTheme

// Retrieve list of grocery items
val AllGroceryItems = List(10) { index -> "item ${index + 1}" }
var groceryItems = AllGroceryItems.filter { it.contains("1") }
var toModify = ""

class GroceryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            CulinarTheme {
                Scaffold(modifier = Modifier.fillMaxSize().background(color = Color(0xFFFFFFFF))) { innerPadding ->
                    Grocery(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //groceryItems = AllGroceryItems.filter { it.contains("item") }
    }
}

@Composable
fun Grocery(modifier: Modifier = Modifier) {
    var screenOn by remember { mutableIntStateOf(1) }

    if (screenOn == 1) {
        HomeTemplate(subscreen = { value -> GroceryList(changeOnboardingScreen = value) }, changeOnboardingScreen = { value -> screenOn = value }, modifier = modifier)
    } else if (screenOn == 2) {
        HomeTemplate(subscreen = { value -> GroceryAddItem(changeOnboardingScreen = value) }, changeOnboardingScreen = { value -> screenOn = value }, modifier = modifier)
    } else if (screenOn == 3) {
        HomeTemplate(subscreen = { value -> GroceryModifyItem(changeOnboardingScreen = value) }, changeOnboardingScreen = { value -> screenOn = value }, modifier = modifier)
    }
}



@Composable
fun HomeTemplate(modifier: Modifier = Modifier, subscreen: @Composable ((Int) -> Unit) -> Unit = {}, changeOnboardingScreen: (Int) -> Unit) {
    Box()
    {
        subscreen(changeOnboardingScreen)
        Header(modifier = modifier)
        Footer(modifier = modifier)
    }
}

@Composable
fun GroceryList(modifier: Modifier = Modifier, changeOnboardingScreen: (Int) -> Unit) {

    var groceryItemsToDisplay by remember { mutableStateOf(groceryItems) }

    // Screen content
    Column (verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.fillMaxSize()) {
        // Header place
        Spacer(modifier = modifier.height(90.dp))

        // Body place
        // Title of the subscreen
        Text(
            text = "Ma liste de courses",
            fontSize = 28.sp,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            lineHeight = 50.sp,
            color = Color(0xFF3CB460),

            modifier = modifier.fillMaxWidth()
                .height(50.dp)
                .background(color = Color(0xFFE5E3E3))
            )

        Spacer(modifier = modifier.height(15.dp))

        // Search bar
        var searchText by remember { mutableStateOf("") }
        TextField(
            value = searchText,
            placeholder = { Text("Rechercher un aliment") },
            onValueChange = {
                searchText = it
                groceryItemsToDisplay = groceryItems.filter { it.contains(searchText) }
                            },
            leadingIcon = {Icon(Icons.Default.Search, contentDescription = "Rechercher")},
            modifier = modifier.width(300.dp).height(50.dp),
        )
        Spacer(modifier = modifier.height(5.dp))
        // GroceryItem add button
        Button(
            onClick = { changeOnboardingScreen(2) },
            modifier = modifier.width(170.dp).height(55.dp),
            shape = CutCornerShape(3.dp),
            colors = ButtonColors(
                containerColor = Color(0xFF59EA85),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFF59EA85),
                disabledContentColor = Color.White
            )
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = "Ajouter", tint = Color.White)
            Spacer(modifier = modifier.width(10.dp))
            Text(text = "Ajouter", fontSize = 18.sp, letterSpacing = 3.sp)
        }

        Spacer(modifier = modifier.height(10.dp))
        // Grocery items list
        Column (modifier = modifier.weight(1f)) {
            LazyColumn (modifier = modifier.fillMaxWidth()) {
                items (groceryItemsToDisplay) { item ->
                    GroceryItem(
                        item = item,
                        changeOnboardingScreen = changeOnboardingScreen,
                        deleteItem = {
                            groceryItems = groceryItems - item
                            groceryItemsToDisplay = groceryItems
                        }
                    )
                }
            }
        }

        // Footer space
        Spacer(modifier = modifier.height(90.dp))
    }
}


@Composable
fun GroceryItem(modifier: Modifier = Modifier, item : String, changeOnboardingScreen: (Int) -> Unit, deleteItem: () -> Unit = {}) {

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 2.dp, horizontal = 6.dp)
            .fillMaxWidth()
            .height(108.dp)
            .border(color = Color(0xFFAAAAAA), width = 1.dp, shape = CutCornerShape(3.dp))
    ) {
        // Image of the item
        Image(Icons.Default.Info,
            contentDescription = "Aliment",
            modifier = modifier
                .width(85.dp)
                .height(85.dp)
        )

        Spacer(modifier = modifier.weight(1f))
        // Details on the item
        Column {
            // Name of the item
            Text(
                text = item,
                fontSize = 24.sp,
                fontFamily = FontFamily.Serif,
            )

            Row {
                // Unit of the item
                Text(
                    text = "1 kg",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                )

                Spacer(modifier = modifier.width(40.dp))
                // Quantity of the item
                Text(
                    text = "x 1",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                )
            }

        }
        Spacer(modifier = modifier.weight(1f))

        // Modify and delete buttons
        Column (
            modifier = modifier
                .padding(vertical = 0.dp, horizontal = 5.dp)
                .height(100.dp)
                //.border(color = Color(0xFFAAAAAA), width = 1.dp, shape = CutCornerShape(3.dp))

        ) {

            // Delete button
            Button(
                onClick = {
                    deleteItem()
                },
                modifier = modifier.width(65.dp).height(48.dp).padding(vertical = 0.dp),
                shape = CutCornerShape(3.dp),
                colors = ButtonColors(
                    containerColor = Color(0xFFE91E63),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFE91E63),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = Color.White,
                    modifier = modifier.height(45.dp).width(45.dp)
                )
            }

            Spacer(modifier = modifier.height(10.dp))

            // Modify button
            Button(
                onClick = {
                    changeOnboardingScreen(3)
                    toModify = item
                          },
                modifier = modifier.width(65.dp).height(35.dp)
                    .border(color = Color(0xFF3F51B5), width = 2.dp, shape = CutCornerShape(3.dp)),
                // shape = CutCornerShape(3.dp),
                colors = ButtonColors(
                    containerColor = Color(0x002196F3),
                    contentColor = Color(0xFF2196F3),
                    disabledContainerColor = Color(0xFF2196F3),
                    disabledContentColor = Color.White
                )

            ) {
                Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = Color(0xFF2196F3))
            }
        }
    }

}

@Composable
fun GroceryAddItem(modifier: Modifier = Modifier, changeOnboardingScreen: (Int) -> Unit, toModify : String = "") {


    // Screen content
    Column (
        //verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        // Header place
        Spacer(modifier = modifier.height(90.dp))

        // Body place
        var nameText by remember { mutableStateOf(toModify) }

        // Screen title and options
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .height(80.dp)
                .background(color = Color(0xFFE5E3E3))
        ) {
            // Return button
            Button(
                onClick = {changeOnboardingScreen(1)},
                modifier = modifier.width(80.dp).height(100.dp),
                shape = CutCornerShape(3.dp),
                colors = ButtonColors(
                    containerColor = Color(0x0059EA85),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF59EA85),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cancel",
                    tint = Color.Red,
                    modifier = modifier.height(70.dp).width(70.dp)
                )

            }

            Spacer(modifier = modifier.weight(1f))

            // Title of the subscreen
            Text(
                text = if (toModify != "") "Modifier $toModify" else "Ajouter à ma liste",
                fontSize = 25.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                lineHeight = 50.sp,
                color = Color(0xFF3CB460),

                modifier = modifier
                    .height(50.dp)
                    .background(color = Color(0xFFE5E3E3))
            )

            Spacer(modifier = modifier.weight(1f))

            // Add button
            Button(
                onClick = {
                    val newItem = AllGroceryItems.find{ it.contains(nameText) }
                    if (newItem != null && newItem !in groceryItems)
                        groceryItems = groceryItems + newItem
                    else
                        groceryItems = groceryItems + nameText

                    changeOnboardingScreen(1)
                },
                modifier = modifier.width(80.dp).height(100.dp),
                shape = CutCornerShape(3.dp),
                colors = ButtonColors(
                    containerColor = Color(0x0059EA85),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF59EA85),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Ajouter",
                    tint = Color(0xFF59EA85),
                    modifier = modifier.height(70.dp).width(70.dp)
                )
                Spacer(modifier = modifier.width(10.dp))
                Text(text = "Add", fontSize = 18.sp, letterSpacing = 3.sp)
            }
        }

        Spacer(modifier = modifier.height(90.dp))

        // Grocery item input form
        Column (
            modifier = modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            // Name of the food item
            Row {
                Text(
                    text = "Nom: ",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 50.sp,
                    modifier = modifier.padding(start = 10.dp)
                )
                Spacer(modifier = modifier.weight(1f))
                var expanded by remember { mutableStateOf(false) }
                TextField(
                    value = if (toModify != "") toModify else nameText,
                    onValueChange = { nameText = it },
                    placeholder = { Text("Nom de l'aliment") },
                    enabled = if (toModify != "") false else true,
                    modifier = modifier
                        .width(if (toModify != "") 200.dp else 160.dp)
                        .height(50.dp)
                        .border(
                            color = Color(0xFFAAAAAA),
                            width = 0.dp,
                            shape = CutCornerShape(3.dp)
                        ),
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = DpOffset(x = 131.dp, y = 0.dp),
                    modifier = modifier
                        .width(200.dp)
                        .height(300.dp)
                        .border(
                            color = Color(0xFFAAAAAA),
                            width = 0.dp,
                            shape = CutCornerShape(3.dp)
                        )
                ) {

                    DropdownMenuItem(
                        text = { Text("Choisissez un aliment") },
                        onClick = { nameText = "Choisissez un aliment" }
                    )
                    for (item in AllGroceryItems - groceryItems) {
                        DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                nameText = item
                                expanded = false
                            }
                        )
                    }
                }

                // Expand dropdown menu button
                if (toModify == "") {
                    TextButton(
                        onClick = { expanded = !expanded },
                        modifier = modifier.width(40.dp).height(50.dp),
                        shape = CutCornerShape(3.dp),
                        border = BorderStroke(width = 1.dp, color = Color(0xFF17211A)),
                        colors = ButtonColors(
                            containerColor = Color(0x0059EA85),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFF59EA85),
                            disabledContentColor = Color.White
                        )
                    ) {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Create new food",
                            tint = Color(0xFF142119),
                            modifier = modifier
                                .height(45.dp)
                                .width(45.dp)

                        )

                    }
                }
                // Spacer(modifier = modifier.width(5.dp))

                // Create new food button
                Button(
                    onClick = {},
                    modifier = modifier.width(80.dp).height(50.dp),
                    shape = CutCornerShape(3.dp),
                    //border = BorderStroke(width = 1.dp, color = Color(0xFF59EA85)),
                    colors = ButtonColors(
                        containerColor = Color(0x0059EA85),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF59EA85),
                        disabledContentColor = Color.White
                    )
                ) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = "Create new food",
                        tint = Color(0xFF59EA85),
                        modifier = modifier
                            .height(45.dp)
                            .width(45.dp)

                    )

                }

            }

            // Quantity unit
            Row {
                Text(
                    text = "Unité: ",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 50.sp,
                    modifier = modifier.padding(start = 10.dp)
                )
                Spacer(modifier = modifier.width(55.dp))

                TextField(
                    value = AllGroceryItems.find{ it.contains(nameText) } ?: "",
                    enabled = false,
                    onValueChange = { },
                    modifier = modifier
                        .width(220.dp)
                        .height(50.dp)
                        .padding(end = 20.dp)
                        .border(
                            color = Color(0xFFAAAAAA),
                            width = 0.dp,
                            shape = CutCornerShape(3.dp)
                        ),
                )
            }

            // Quantity input
            Row {
                Text(
                    text = "Quantité: ",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 50.sp,
                    modifier = modifier.padding(start = 10.dp)
                )
                Spacer(modifier = modifier.width(20.dp))
                var value by remember { mutableStateOf(toModify) }
                TextField(
                    value = value,
                    onValueChange = { if(it.isDigitsOnly()) value = it },
                    placeholder = { Text("Quantité de l'aliment") },
                    modifier = modifier
                        .width(220.dp)
                        .height(50.dp)
                        .padding(end = 20.dp)
                        .border(
                            color = Color(0xFFAAAAAA),
                            width = 0.dp,
                            shape = CutCornerShape(3.dp)
                        ),
                )

            }


            // Footer space
            Spacer(modifier = modifier.height(90.dp))
        }

    }
}


@Composable
fun GroceryModifyItem(modifier: Modifier = Modifier, changeOnboardingScreen: (Int) -> Unit) {
    GroceryAddItem(modifier = modifier, changeOnboardingScreen = changeOnboardingScreen, toModify = toModify)
}


@Preview(showBackground = true)
@Composable
fun GroceryPreview() {
    CulinarTheme {
        Grocery()
    }
}