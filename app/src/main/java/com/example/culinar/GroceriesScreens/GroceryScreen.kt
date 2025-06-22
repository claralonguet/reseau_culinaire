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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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

// Holds the name of the grocery item to be modified. Used globally across screens.
var toModify = ""

private object GroceryScreen {
    const val LIST = 1
    const val ADD = 2
    const val MODIFY = 3
    const val TO_LOGIN = 4
}

@Composable
        /**
         * Main Grocery screen controller composable.
         * Displays different screens based on user's login status and current interaction.
         *
         * @param sessionViewModel Provides user session info such as user ID.
         * @param onNavigate Callback to trigger navigation actions (e.g., to login screen).
         *
         * It maintains an internal screen state:
         * - LIST (1): Shows the grocery list.
         * - ADD (2): Shows the add grocery item form.
         * - MODIFY (3): Shows the modify grocery item form.
         * - TO_LOGIN (4): Shows a prompt to log in if user is unauthenticated.
         *
         * The screen switches dynamically based on user actions and authentication state.
         */
fun Grocery(
    sessionViewModel: SessionViewModel = viewModel(),
    onNavigate: (String, String?) -> Unit = { _, _ -> },
) {
    val groceryViewModel: GroceryViewModel = viewModel()

    // Collect user ID from session; null means not logged in
    val userId by sessionViewModel.id.collectAsState()

    // Update groceryViewModel's userId whenever userId changes
    LaunchedEffect(userId) {
        groceryViewModel.setUserId(userId ?: "")
    }

    // Screen state remembers last screen, defaults to LIST if logged in, else TO_LOGIN
    var screenOn by rememberSaveable {
        mutableIntStateOf(
            if (userId != null) GroceryScreen.LIST else GroceryScreen.TO_LOGIN
        )
    }

    // Lambda to switch screens, passed down to child composables
    val changeOnboardingScreen = { value: Int -> screenOn = value }

    // Render the composable corresponding to current screen state
    when (screenOn) {
        GroceryScreen.LIST -> GroceryList(
            changeOnboardingScreen = changeOnboardingScreen,
            groceryViewModel = groceryViewModel
        )
        GroceryScreen.ADD -> GroceryAddItem(
            changeOnboardingScreen = changeOnboardingScreen,
            groceryViewModel = groceryViewModel
        )
        GroceryScreen.MODIFY -> GroceryModifyItem(
            changeOnboardingScreen = changeOnboardingScreen,
            groceryViewModel = groceryViewModel
        )
        GroceryScreen.TO_LOGIN -> ToLogin(onNavigate = onNavigate)
    }
}


@Composable
        /**
         * Screen shown when the user is not logged in.
         * Displays a title bar, a description text explaining the grocery feature,
         * and a button that navigates to the login screen with a redirect back to groceries after login.
         *
         * @param onNavigate Callback for navigation actions. Takes a route string and an optional parameter.
         * @param modifier Optional Compose modifier for styling.
         */
fun ToLogin(
    onNavigate: (String, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Top bar title for the grocery screen
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = grey)
        ) {
            Text(
                text = stringResource(R.string.grocery_list_title),
                style = Typography.titleLarge,
                fontSize = 30.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Main content block: description and login button
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

            // Button that triggers navigation to the Account screen with nextRoute parameter for post-login redirect
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF59EA85),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.AccountCircle, contentDescription = "Log in", tint = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = stringResource(R.string.login_first_button), style = Typography.labelSmall)
            }
        }
    }
}


@Composable
        /**
         * Displays the grocery list screen.
         * Includes a search bar to filter grocery items by name,
         * a button to navigate to the item addition screen,
         * and a scrollable list of grocery items.
         *
         * @param modifier Optional Compose modifier for styling.
         * @param changeOnboardingScreen Callback to change the current screen view, accepting an Int screen code.
         * @param groceryViewModel ViewModel providing grocery items and actions.
         */
fun GroceryList(
    modifier: Modifier = Modifier,
    changeOnboardingScreen: (Int) -> Unit,
    groceryViewModel: GroceryViewModel = viewModel(),
) {
    // Collect current grocery items from ViewModel as state
    val groceryItems by groceryViewModel.groceryItems.collectAsState()

    // Search text state for filtering items
    var searchText by remember { mutableStateOf("") }

    // Filtered list recomputed when groceryItems or searchText change
    val groceryItemsToDisplay = remember(groceryItems, searchText) {
        if (searchText.isBlank()) groceryItems
        else groceryItems.filter { it.name.contains(searchText, ignoreCase = true) }
    }

    Column (
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        // Title bar with list screen title
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = grey)
        ) {
            Text(
                text = stringResource(R.string.grocery_list_title),
                style = Typography.titleLarge,
                fontSize = 30.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Search input field with leading search icon
        TextField(
            value = searchText,
            placeholder = { Text(stringResource(R.string.grocery_search_placeholder), style = Typography.bodySmall) },
            onValueChange = { searchText = it },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = stringResource(R.string.grocery_search_button))
            },
            modifier = Modifier
                .width(300.dp)
                .height(50.dp),
        )

        Spacer(modifier = Modifier.height(5.dp))

        // Button to navigate to the add grocery item screen
        Button(
            onClick = { changeOnboardingScreen(2) },
            modifier = Modifier
                .width(170.dp)
                .height(55.dp),
            shape = CutCornerShape(3.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF59EA85),
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = "Ajouter", tint = Color.White)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(R.string.grocery_add_button), style = Typography.labelSmall)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Scrollable list showing filtered grocery items
        Column(modifier = Modifier.weight(1f)) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(groceryItemsToDisplay) { item ->
                    GroceryItem(
                        item = item,
                        changeOnboardingScreen = changeOnboardingScreen,
                        deleteItem = {
                            groceryViewModel.removeItemToGroceryList(item)
                        }
                    )
                }
            }
        }
    }
}



@Composable
        /**
         * Displays a single grocery item as a horizontal row.
         * Shows the item's image, name, unit, and quantity.
         * Includes buttons to delete or modify the item.
         *
         * @param modifier Modifier for styling the component.
         * @param item The Aliment data object representing the grocery item.
         * @param changeOnboardingScreen Lambda to switch the current screen (e.g., to modify screen).
         * @param onModify Callback invoked with the item when the modify button is clicked.
         * @param deleteItem Callback invoked when the delete button is clicked.
         */
fun GroceryItem(
    modifier: Modifier = Modifier,
    item: Aliment,
    changeOnboardingScreen: (Int) -> Unit,
    onModify: (Aliment) -> Unit = {},
    deleteItem: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(vertical = 2.dp, horizontal = 6.dp)
            .fillMaxWidth()
            .height(108.dp)
            .border(color = Color(0xFFAAAAAA), width = 1.dp, shape = CutCornerShape(3.dp))
    ) {
        // Image preview of the grocery item
        AsyncImage(
            model = item.photo,
            contentDescription = item.name,
            modifier = Modifier
                .width(85.dp)
                .height(85.dp)
                .padding(start = 5.dp)
                .border(color = Color(0xFFAAAAAA), width = 1.dp, shape = CutCornerShape(3.dp))
        )

        Spacer(modifier = Modifier.width(20.dp))

        // Item name, unit, and quantity details
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(
                text = item.name,
                style = Typography.titleMedium,
            )
            Row {
                Text(text = item.unit, style = Typography.bodySmall)
                Spacer(modifier = Modifier.width(40.dp))
                Text(text = "x ${item.quantity}", style = Typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.weight(2f))

        // Action buttons: delete and modify
        Column(
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .height(100.dp)
        ) {
            // Delete button with red background
            TextButton(
                onClick = deleteItem,
                modifier = Modifier
                    .width(65.dp)
                    .height(48.dp),
                shape = CutCornerShape(3.dp),
                colors = ButtonDefaults.textButtonColors(
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
                    modifier = Modifier
                        .height(45.dp)
                        .width(45.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Modify button outlined in blue
            TextButton(
                onClick = {
                    changeOnboardingScreen(3)  // Navigate to modify screen
                    onModify(item)              // Pass the current item for modification
                },
                modifier = Modifier
                    .width(65.dp)
                    .height(35.dp)
                    .border(color = Color(0xFF3F51B5), width = 2.dp, shape = CutCornerShape(3.dp)),
                colors = ButtonDefaults.textButtonColors(
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
        /**
         * Screen to add a new grocery item or modify an existing one.
         * - If [toModify] is non-empty, pre-fills fields for the item to modify.
         * - Shows a dropdown of predefined food items (excluding those already in the grocery list) when adding a new item.
         * - Allows editing of name (when adding), unit (editable only for custom items), and quantity.
         * - Handles updating or adding items through the [groceryViewModel].
         *
         * @param modifier Modifier for styling the composable.
         * @param changeOnboardingScreen Lambda to switch between screens, e.g., back to list after adding/modifying.
         * @param toModify Name of the grocery item to modify; empty string if adding a new item.
         * @param groceryViewModel ViewModel managing grocery data and operations.
         */
fun GroceryAddItem(
    modifier: Modifier = Modifier,
    changeOnboardingScreen: (Int) -> Unit,
    toModify: String = "",
    groceryViewModel: GroceryViewModel = viewModel()
) {
    val allFoodItems: List<Aliment> = groceryViewModel.allFoodItems
    val groceryItems: List<Aliment> by groceryViewModel.groceryItems.collectAsState()

    // The item currently being modified, if any
    val itemToModify by remember(groceryItems, toModify) {
        derivedStateOf { groceryItems.find { it.name == toModify } ?: Aliment() }
    }

    var nameText by remember { mutableStateOf(toModify) }  // Name input text
    var selectedItem by remember { mutableStateOf(Aliment("")) }  // Selected item from dropdown or new
    var expanded by remember { mutableStateOf(false) }  // Dropdown menu expanded state
    var unit by remember { mutableStateOf("unité") }  // Unit input text, default to "unité"
    var quantityText by remember { mutableStateOf((itemToModify.quantity ?: 1).toString()) }  // Quantity input text

    // When the name changes, update selectedItem and unit accordingly
    LaunchedEffect(nameText) {
        selectedItem = selectedItem.copy(name = nameText)
        if (allFoodItems.none { it.name == nameText }) {
            unit = "unité"  // Reset unit for new custom items
        } else {
            val predefinedItem = allFoodItems.find { it.name == nameText }
            if (predefinedItem != null) {
                unit = predefinedItem.unit
                selectedItem = selectedItem.copy(unit = predefinedItem.unit)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        // Header omitted for brevity...

        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight()
        ) {
            // Name input with dropdown for predefined items when adding new
            Row {
                TextField(
                    value = if (toModify != "") itemToModify.name else nameText,
                    onValueChange = {
                        if (toModify == "") nameText = it
                    },
                    label = { Text(stringResource(R.string.grocery_item_name_label), fontSize = 15.sp) },
                    enabled = toModify == "",
                    singleLine = true,
                    modifier = Modifier.width(if (toModify != "") 280.dp else 240.dp).height(60.dp),
                )
                if (toModify == "") {
                    TextButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.width(40.dp).height(60.dp),
                        shape = CutCornerShape(3.dp),
                        border = BorderStroke(1.dp, Color(0xFF939292)),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color(0x0059EA85),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Select food",
                            tint = Color(0xFF142119),
                            modifier = Modifier.size(45.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.width(240.dp).heightIn(max = 300.dp)
                    ) {
                        val defaultLabel = stringResource(R.string.grocery_dropdown_menu_default)
                        DropdownMenuItem(
                            text = { Text(defaultLabel) },
                            onClick = {
                                nameText = defaultLabel
                                expanded = false
                            }
                        )
                        for (item in allFoodItems) {
                            if (groceryItems.none { it.name == item.name }) {
                                DropdownMenuItem(
                                    text = { Text(item.name) },
                                    onClick = {
                                        nameText = item.name
                                        selectedItem = item.copy(quantity = 1)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Unit input, editable only for custom items when adding, or for modify mode it updates the ViewModel directly
            TextField(
                value = if (toModify != "") itemToModify.unit else unit,
                onValueChange = {
                    if (toModify != "") {
                        // Update unit immediately in modify mode
                        groceryViewModel.modifyItemToGroceryList(itemToModify.copy(unit = it))
                    } else {
                        unit = it
                        selectedItem = selectedItem.copy(unit = it)
                    }
                },
                label = { Text(stringResource(R.string.grocery_item_unit_label), fontSize = 15.sp) },
                singleLine = true,
                enabled = allFoodItems.none { it.name == nameText } && toModify == "",
                modifier = Modifier.width(280.dp).height(60.dp),
            )

            // Quantity input, allowing only digits; updates ViewModel in modify mode or local state when adding
            TextField(
                value = quantityText,
                onValueChange = {
                    if (it.isDigitsOnly()) {
                        quantityText = if (it.isEmpty()) "1" else it
                        val quantityInt = quantityText.toIntOrNull() ?: 1
                        if (toModify != "") {
                            groceryViewModel.modifyItemToGroceryList(itemToModify.copy(quantity = quantityInt))
                        } else {
                            selectedItem = selectedItem.copy(quantity = quantityInt)
                        }
                    }
                },
                label = { Text(stringResource(R.string.grocery_item_quantity_label), fontSize = 15.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(280.dp).height(60.dp),
            )

            Spacer(Modifier.height(20.dp))

            // Add or Modify button, performs appropriate ViewModel action and returns to list
            Button(
                onClick = {
                    if (toModify == "") {
                        if (selectedItem.name.isNotBlank() && selectedItem !in groceryItems) {
                            groceryViewModel.addItemToGroceryList(selectedItem)
                        }
                    } else {
                        groceryViewModel.modifyItemToGroceryList(itemToModify)
                    }
                    changeOnboardingScreen(1) // Back to grocery list screen
                },
                modifier = Modifier.width(150.dp).height(50.dp),
                shape = CutCornerShape(3.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF59EA85),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (toModify == "") stringResource(R.string.grocery_item_add_button) else stringResource(R.string.grocery_item_modify_button),
                    fontSize = 18.sp,
                    letterSpacing = 3.sp
                )
            }
        }
    }
}



/**
 * Screen composable for modifying an existing grocery item.
 *
 * This screen delegates the UI and logic to [GroceryAddItem], passing the
 * global variable [toModify] as the item to be edited.
 *
 * @param modifier Optional Modifier to apply to the root layout.
 * @param changeOnboardingScreen Callback to change the onboarding screen step.
 * @param groceryViewModel ViewModel managing grocery items and related state.
 */
@Composable
fun GroceryModifyItem(
    modifier: Modifier = Modifier,
    changeOnboardingScreen: (Int) -> Unit,
    groceryViewModel: GroceryViewModel = viewModel()
) {
    GroceryAddItem(
        modifier = modifier,
        changeOnboardingScreen = changeOnboardingScreen,
        toModify = toModify,
        groceryViewModel = groceryViewModel
    )
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