package com.example.culinar.GroceriesScreens


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.culinar.R
import com.example.culinar.models.Aliment
import com.example.culinar.models.Screen
import com.example.culinar.models.viewModels.GroceryViewModel
import com.example.culinar.ui.theme.CulinarTheme
import com.example.culinar.ui.theme.Typography
import com.example.culinar.ui.theme.grey
import com.example.culinar.viewmodels.SessionViewModel



var toModify = ""

@Composable
fun Grocery(
    sessionViewModel: SessionViewModel = viewModel(),
    onNavigate: (String, String?) -> Unit = { _, _ -> },
) {

    val groceryViewModel : GroceryViewModel = viewModel ()
    val userId by sessionViewModel.id.collectAsState()
    groceryViewModel.setUserId(userId ?: "")

    var screenOn by remember { mutableIntStateOf(if(userId != null) 1 else 4) }
    val changeOnboardingScreen = { value : Int -> screenOn = value }

    when(screenOn) {
        1 -> GroceryList(changeOnboardingScreen = changeOnboardingScreen, groceryViewModel = groceryViewModel)
        2 -> GroceryAddItem(changeOnboardingScreen = changeOnboardingScreen, groceryViewModel = groceryViewModel)
        3 -> GroceryModifyItem(changeOnboardingScreen = changeOnboardingScreen, groceryViewModel = groceryViewModel)
        4 -> ToLogin(onNavigate = onNavigate)
    }
}


@Composable
fun ToLogin(onNavigate: (String, String?) -> Unit) {

    Column (
        modifier = Modifier.fillMaxSize()
    ) {

        // Title of the subscreen
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = grey)
        ) {
            Text(
                text = "Ma liste de courses",
                style = Typography.titleLarge,
                fontSize = 30.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
        Text(
            text = stringResource(R.string.grocery_screen_description),
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 50.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        // To login button
        Button(
            onClick = {
                onNavigate(
                    "${Screen.Account.name}?nextRoute=${Screen.Groceries.name}",
                    null
                )
            },
            modifier = Modifier
                .width(250.dp)
                .height(80.dp),
            shape = CutCornerShape(3.dp),
            colors = ButtonColors(
                containerColor = Color(0xFF59EA85),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFF59EA85),
                disabledContentColor = Color.White
            )
        ) {
            Icon(Icons.Default.AccountCircle, contentDescription = "Log in", tint = Color.White)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Connectez vous", style = Typography.labelSmall)
        }
    }
    }

}


@Composable
fun GroceryList(
    modifier: Modifier = Modifier,
    changeOnboardingScreen: (Int) -> Unit,
    groceryViewModel: GroceryViewModel = viewModel(),
) {


    val groceryItems : List<Aliment> by groceryViewModel.groceryItems.collectAsState()
    var groceryItemsToDisplay by remember { mutableStateOf(groceryItems) }

    // Update groceryItemsToDisplay when groceryItems change
    LaunchedEffect(groceryItems) {
        Log.d("GroceryList", "groceryItems changed, updating groceryItemsToDisplay. New count: ${groceryItems.size}")
        groceryItemsToDisplay = groceryItems
    }

    // Screen content
    Column (verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.fillMaxSize()) {


        // Title of the subscreen
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = grey)
        ) {
            Text(
                text = "Ma liste de courses",
                style = Typography.titleLarge,
                fontSize = 30.sp,
                modifier = modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = modifier.height(15.dp))

        // Search bar
        var searchText by remember { mutableStateOf("") }
        TextField(
            value = searchText,
            placeholder = { Text("Rechercher un aliment", style = Typography.bodySmall) },
            onValueChange = {
                searchText = it
                groceryItemsToDisplay = groceryItems.filter { item -> item.name.lowercase().contains(searchText.lowercase()) }
                //Log.d("GroceryList", "Search text changed to $it : $groceryItemsToDisplay")
            },
            leadingIcon = {Icon(Icons.Default.Search, contentDescription = "Rechercher")},
            modifier = modifier
                .width(300.dp)
                .height(50.dp),
        )
        Spacer(modifier = modifier.height(5.dp))
        // GroceryItem add button
        Button(
            onClick = { changeOnboardingScreen(2) },
            modifier = modifier
                .width(170.dp)
                .height(55.dp),
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
            Text(text = "Ajouter", style = Typography.labelSmall)
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
                            groceryViewModel.removeItemToGroceryList(item)
                            groceryItemsToDisplay = groceryItems
                        }
                    )
                }
            }
        }

    }
}


@Composable
fun GroceryItem(modifier: Modifier = Modifier, item : Aliment, changeOnboardingScreen: (Int) -> Unit, deleteItem: () -> Unit = {}) {

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(vertical = 2.dp, horizontal = 6.dp)
            .fillMaxWidth()
            .height(108.dp)
            .border(color = Color(0xFFAAAAAA), width = 1.dp, shape = CutCornerShape(3.dp))
    ) {
        // Image of the item
        /*Image(Icons.Default.Info,
            contentDescription = "Aliment",
            modifier = modifier
                .width(85.dp)
                .height(85.dp)
        )
         */
        AsyncImage(
            model = item.photo,
            contentDescription = item.name,
            modifier = modifier
                .width(85.dp)
                .height(85.dp)
                .padding(start = 5.dp)
                .border(color = Color(0xFFAAAAAA), width = 1.dp, shape = CutCornerShape(3.dp))
        )

        Spacer(modifier = modifier.width(20.dp))
        // Details on the item
        Column (
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier.fillMaxHeight()
                //.border(color = Color(0xFFAAAAAA), width = 1.dp, shape = CutCornerShape(3.dp))
        ) {
            // Name of the item
            Text(
                text = item.name,
                style = Typography.titleMedium,
            )

            Row {
                // Unit of the item
                Text(
                    text = item.unit,
                    style = Typography.bodySmall,
                )

                Spacer(modifier = modifier.width(40.dp))
                // Quantity of the item
                Text(
                    text = "x ${item.quantity}",
                    style = Typography.bodyLarge,
                )
            }

        }
        Spacer(modifier = modifier.weight(2f))

        // Modify and delete buttons
        Column (
            modifier = modifier
                .padding(vertical = 0.dp, horizontal = 5.dp)
                .height(100.dp)
                //.border(color = Color(0xFFAAAAAA), width = 1.dp, shape = CutCornerShape(3.dp))

        ) {

            // Delete button
            TextButton(
                onClick = {
                    deleteItem()
                },
                modifier = modifier
                    .width(65.dp)
                    .height(48.dp)
                    .padding(vertical = 0.dp),
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
                    modifier = modifier
                        .height(45.dp)
                        .width(45.dp)
                )
            }

            Spacer(modifier = modifier.height(10.dp))

            // Modify button
            TextButton(
                onClick = {
                    changeOnboardingScreen(3)
                    toModify = item.name
                          },
                modifier = modifier
                    .width(65.dp)
                    .height(35.dp)
                    .border(color = Color(0xFF3F51B5), width = 2.dp, shape = CutCornerShape(3.dp)),
                // shape = CutCornerShape(3.dp),
                colors = ButtonColors(
                    containerColor = Color(0x002196F3),
                    contentColor = Color(0xFF2196F3),
                    disabledContainerColor = Color(0xFF2196F3),
                    disabledContentColor = Color.White
                )

            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Modifier",
                    tint = Color(0xFF2196F3),
                )
            }
        }
    }

}

@Composable
fun GroceryAddItem(modifier: Modifier = Modifier, changeOnboardingScreen: (Int) -> Unit, toModify : String = "", groceryViewModel: GroceryViewModel = viewModel ()) {

    val allFoodItems : List<Aliment> = groceryViewModel.allFoodItems
    val groceryItems : List<Aliment> by groceryViewModel.groceryItems.collectAsState()

    var nameText by remember { mutableStateOf(toModify) }
    var selectedItem: Aliment by remember { mutableStateOf(Aliment("")) }
    var itemToModify: Aliment by remember { mutableStateOf(groceryItems.find { it.name == toModify } ?: Aliment()) }

    // Screen content
    Column (
        //verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {

        // Screen title and options
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(color = grey)
        ) {
            // Return button
            TextButton(
                onClick = {changeOnboardingScreen(1)},
                shape = CutCornerShape(3.dp),
                colors = ButtonColors(
                    containerColor = Color(0x0059EA85),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF59EA85),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Default.KeyboardArrowLeft,
                    contentDescription = "Cancel",
                    tint = Color.Red,
                    modifier = modifier
                        .height(100.dp)
                        .width(60.dp)
                )

            }

            Spacer(modifier = modifier.width(15.dp))

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
                    .background(color = grey)
            )

            Spacer(modifier = modifier.weight(5f))
        }


        // Grocery item input form
        Column (
            modifier = modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column (
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = modifier.height(420.dp)
            ) {

                // Name of the food item
                Column {
                    var expanded by remember { mutableStateOf(false) }
                    Row {
                        TextField(
                            value = if (toModify != "") itemToModify.name else nameText,
                            onValueChange = {
                                nameText = it
                                selectedItem = selectedItem.copy(name = it)
                            },
                            label = { Text("Nom de l'aliment", fontSize = 15.sp) },
                            enabled = toModify == "",
                            singleLine = true,
                            modifier = modifier
                                .width(if (toModify != "") 280.dp else 240.dp)
                                .height(60.dp)
                                .border(
                                    color = Color(0xFFAAAAAA),
                                    width = 0.dp,
                                    shape = CutCornerShape(3.dp)
                                ),
                        )

                        // Expand dropdown menu button
                        if (toModify == "") {
                            TextButton(
                                onClick = { expanded = !expanded },
                                modifier = modifier
                                    .width(40.dp)
                                    .height(60.dp),
                                shape = CutCornerShape(3.dp),
                                border = BorderStroke(width = 1.dp, color = Color(0xFF939292)),
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
                    }
                    // Drop down menu
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        offset = DpOffset(x = 0.dp, y = 0.dp),
                        modifier = modifier
                            .width(240.dp)
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
                        for (item in allFoodItems) {
                            if (groceryItems.none { it.name == item.name }) {
                                DropdownMenuItem(
                                    text = { Text(text = item.name) },
                                    onClick = {
                                        nameText = item.name
                                        selectedItem = item
                                        selectedItem.quantity = 1
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                }

                // Quantity unit
                var unit by remember { mutableStateOf("unité") }
                TextField(
                    value = if (toModify != "") itemToModify.unit else if (allFoodItems.none { it.name == nameText }) unit else selectedItem.unit, //allFoodItems.find{ it.name.contains(nameText) }?.unit ?: "",
                    enabled = allFoodItems.none { it.name == nameText } && toModify == "",
                    singleLine = true,
                    label = { Text("Unité", fontSize = 15.sp) },
                    onValueChange = {
                        unit = it
                        if (toModify != "")
                            itemToModify = itemToModify.copy(unit = it)
                        else
                            selectedItem = selectedItem.copy(unit = it)
                    },
                    modifier = modifier
                        .width(280.dp)
                        .height(60.dp)
                        .border(
                            color = Color(0xFFAAAAAA),
                            width = 0.dp,
                            shape = CutCornerShape(3.dp)
                        ),
                )


                // Quantity input
                var value by remember { mutableStateOf((itemToModify.quantity ?: 1).toString()) }
                TextField(
                    value = value,
                    singleLine = true,
                    label = { Text("Quantité", fontSize = 15.sp) },
                    onValueChange = {
                        if (it.isDigitsOnly()) {
                            value = if (it == "") "1" else it
                            if (toModify != "")
                                itemToModify = itemToModify.copy(quantity = value.toInt())
                            else
                                selectedItem = selectedItem.copy(quantity = value.toInt())
                        }
                    },
                    placeholder = { Text("Quantité de l'aliment", fontSize = 15.sp) },
                    modifier = modifier
                        .width(280.dp)
                        .height(60.dp)
                        .border(
                            color = Color(0xFFAAAAAA),
                            width = 0.dp,
                            shape = CutCornerShape(3.dp)
                        ),

                    )
            }

            //Spacer(modifier = modifier.weight(1f))

            // Add button
            Button(
                onClick = {
                    // val newItem = allFoodItems.find{ it.name == nameText }
                    if (toModify == "") { // Ajout d'un nouvel aliment à la liste
                        if (allFoodItems.any { it.name == selectedItem.name }) { // Si l'aliment existe
                            if (selectedItem !in groceryItems) // Si l'aliment n'est pas déjà dans la liste
                                groceryViewModel.addItemToGroceryList(selectedItem)

                        } else { // Si l'aliment n'existe pas
                            if (selectedItem.name != "")
                                groceryViewModel.addItemToGroceryList(selectedItem)
                        }
                    } else
                        groceryViewModel.modifyItemToGroceryList(itemToModify)

                    changeOnboardingScreen(1)
                },
                modifier = modifier
                    .width(150.dp)
                    .height(50.dp),
                shape = CutCornerShape(3.dp),
                colors = ButtonColors(
                    containerColor = Color(0xFF59EA85),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF59EA85),
                    disabledContentColor = Color.White
                )
            ) {

                // Spacer(modifier = modifier.width(10.dp))
                Text(text = "Ajouter", fontSize = 18.sp, letterSpacing = 3.sp)
            }
        }

    }
}


@Composable
fun GroceryModifyItem(modifier: Modifier = Modifier, changeOnboardingScreen: (Int) -> Unit, groceryViewModel: GroceryViewModel = viewModel ()) {
    GroceryAddItem(modifier = modifier, changeOnboardingScreen = changeOnboardingScreen, toModify = toModify, groceryViewModel = groceryViewModel)
}


@Preview(showBackground = true)
@Composable
fun GroceryPreview() {
    CulinarTheme {
        Grocery()
    }
}

@Preview(showBackground = true)
@Composable
fun GroceryAddItemPreview() {
    CulinarTheme {
        GroceryAddItem(changeOnboardingScreen = {})
    }
}