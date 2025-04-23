package com.example.culinar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.culinar.ui.theme.CulinarTheme

class GroceryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CulinarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Grocery(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Grocery(modifier: Modifier = Modifier) {
    var screenOn by remember { mutableIntStateOf(1) }

    if (screenOn == 1) {
        HomeTemplate(subscreen = { value -> GroceryList(changeOnboardingScreen = value) }, changeOnboardingScreen = { value -> screenOn = value })
    } else if (screenOn == 2) {
        HomeTemplate(subscreen = { value -> GroceryAddItem(changeOnboardingScreen = value) }, changeOnboardingScreen = { value -> screenOn = value })
    } else if (screenOn == 3) {
        HomeTemplate(subscreen = { value -> GroceryModifyItem(changeOnboardingScreen = value) }, changeOnboardingScreen = { value -> screenOn = value })
    }
}


@Composable
fun Header(modifier: Modifier = Modifier) {

    var menuClicked by remember { mutableStateOf(false) }

    Column () {
        Spacer(modifier = Modifier.height(20.dp).background(color = Color(0xFF3CB460)))


        Row(
            modifier = modifier
                .height(60.dp)
                .fillMaxWidth()
                .background(color = Color(0xFF3CB460)),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Menu button
            Button(
                onClick = {menuClicked = !menuClicked},
                modifier = Modifier.width(90.dp).height(90.dp).padding(0.dp),
                shape = CutCornerShape(3.dp),
                colors = ButtonColors(
                    containerColor = if (!menuClicked) Color(0xFF3CB460) else Color(0xFF1C6231),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.width(40.dp).height(40.dp)
                )

            }

            Spacer(modifier = Modifier.weight(1f))

            /*
            Text(
                text = "Esteban GOMEZ",
                fontSize = 20.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                lineHeight = 21.sp,
                modifier = Modifier.width(120.dp),
            )
            */
            //Spacer(modifier = Modifier.weight(1f))
            // Profile picture
            Button(
                onClick = {},
                modifier = Modifier.width(90.dp).height(90.dp).padding(0.dp),
                shape = CutCornerShape(3.dp),
                colors = ButtonColors(
                    containerColor = Color(0xFF3CB460),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.Face,
                    contentDescription = "Clear",
                    tint = Color.White,
                    modifier = Modifier.width(45.dp).height(45.dp)
                )
            }
        }

        // Side bar
        if (menuClicked) {
            Column(
                modifier = Modifier
                    .background(color = Color(0xFFFFFCF0))
                    .width(150.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,

            ) {
                // Settings button
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(
                        onClick = { menuClicked = !menuClicked },
                        modifier = Modifier
                            .width(150.dp)
                            .height(150.dp),
                        shape = CutCornerShape(3.dp),
                        colors = ButtonColors(
                            containerColor = Color(0x003CB460),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0x003CB460),
                            disabledContentColor = Color.White
                        )
                    ) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFF0A0A0A),
                            modifier = Modifier
                                .width(70.dp)
                                .height(70.dp)
                        )
                    }
                    Text("Settings", fontSize = 20.sp)
                }


                // Log out button
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(
                        onClick = { menuClicked = !menuClicked },
                        modifier = Modifier
                            .width(150.dp)
                            .height(150.dp),
                        shape = CutCornerShape(3.dp),
                        colors = ButtonColors(
                            containerColor = Color(0x003CB460),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0x003CB460),
                            disabledContentColor = Color.White
                        )
                    ) {
                        Icon(
                            Icons.Outlined.ExitToApp,
                            contentDescription = "Log out",
                            tint = Color(0xFF0A0A0A),
                            modifier = Modifier
                                .width(70.dp)
                                .height(70.dp)
                        )
                    }
                    Text("Log out", fontSize = 20.sp)
                }

            }
        }
    }
}

@Composable
fun Footer(modifier: Modifier = Modifier) {

    Column {
        Spacer(modifier = Modifier.weight(1f))

        // Footer bar
        Row(
            modifier = modifier
                .height(50.dp)
                .fillMaxWidth()
                .background(color = Color(0xFF3CB460)),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Calendar button
            Button(
                onClick = {},
                modifier = Modifier.width(72.dp).height(72.dp).padding(0.dp),
                shape = CutCornerShape(3.dp),
                colors = ButtonColors(
                    containerColor = Color(0xFF1C6231),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.width(40.dp).height(40.dp)
                )

            }

            // Groceries button
            Button(
                onClick = {},
                modifier = Modifier.width(72.dp).height(72.dp).padding(0.dp),
                shape = CutCornerShape(3.dp),
                colors = ButtonColors(
                    containerColor = Color(0xFF3CB460),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Clear",
                    tint = Color.White,
                    modifier = Modifier.width(45.dp).height(45.dp)
                )
            }

            // Recipes button
            Button(
                onClick = {},
                modifier = Modifier.width(72.dp).height(72.dp).padding(0.dp),
                shape = CutCornerShape(3.dp),
                colors = ButtonColors(
                    containerColor = Color(0xFF3CB460),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Clear",
                    tint = Color.White,
                    modifier = Modifier.width(45.dp).height(45.dp)
                )
            }

            // Home button
            Button(
                onClick = {},
                modifier = Modifier.width(72.dp).height(72.dp).padding(0.dp),
                shape = CutCornerShape(3.dp),
                colors = ButtonColors(
                    containerColor = Color(0xFF3CB460),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Clear",
                    tint = Color.White,
                    modifier = Modifier.width(45.dp).height(45.dp)
                )
            }

            // Community button
            Button(
                onClick = {},
                modifier = Modifier.width(72.dp).height(72.dp).padding(0.dp),
                shape = CutCornerShape(3.dp),
                colors = ButtonColors(
                    containerColor = Color(0xFF3CB460),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.Email,
                    contentDescription = "Clear",
                    tint = Color.White,
                    modifier = Modifier.width(45.dp).height(45.dp)
                )
            }

            //

        }
    }
}


@Composable
fun HomeTemplate(modifier: Modifier = Modifier, subscreen: @Composable ((Int) -> Unit) -> Unit = {}, changeOnboardingScreen: (Int) -> Unit) {
    Box()
    {
        subscreen(changeOnboardingScreen)
        Header()
        Footer()
    }
}

@Composable
fun GroceryList(modifier: Modifier = Modifier, changeOnboardingScreen: (Int) -> Unit) {

    // Retrieve list of grocery items
    val grorecyItems = List(10) { index -> "item ${index.toString()}" }

    // Screen content
    Column (verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        // Header place
        Spacer(modifier = Modifier.height(80.dp))

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

            modifier = Modifier.fillMaxWidth()
                .height(50.dp)
                .background(color = Color(0xFFE5E3E3))
            )

        Spacer(modifier = Modifier.height(15.dp))

        // Search bar
        TextField(
            value = "",
            placeholder = { Text("Rechercher un aliment") },
            onValueChange = {},
            leadingIcon = {Icon(Icons.Default.Search, contentDescription = "Rechercher")},
            modifier = Modifier.width(300.dp).height(50.dp),
        )
        Spacer(modifier = Modifier.height(5.dp))
        // GroceryItem add button
        Button(
            onClick = { changeOnboardingScreen(2) },
            modifier = Modifier.width(160.dp).height(55.dp),
            shape = CutCornerShape(3.dp),
            colors = ButtonColors(
                containerColor = Color(0xFF59EA85),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFF59EA85),
                disabledContentColor = Color.White
            )
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = "Ajouter", tint = Color.White)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Ajouter", fontSize = 18.sp, letterSpacing = 3.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))
        // Grocery items list
        Column () {
            LazyColumn (modifier = Modifier.fillMaxSize()) {
                items (grorecyItems) { item ->
                    GroceryItem(
                        item = item,
                        changeOnboardingScreen = changeOnboardingScreen
                    )
                }
            }
        }
    }

}


@Composable
fun GroceryItem(modifier: Modifier = Modifier, item : String, changeOnboardingScreen: (Int) -> Unit) {

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp, horizontal = 6.dp)
            .fillMaxWidth()
            .height(108.dp)
            .border(color = Color(0xFFAAAAAA), width = 1.dp, shape = CutCornerShape(3.dp))
    ) {
        // Image of the item
        Image(Icons.Default.Face, contentDescription = "Aliment", modifier = Modifier.width(85.dp).height(85.dp))

        Spacer(modifier = Modifier.weight(1f))
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

                Spacer(modifier = Modifier.width(40.dp))
                // Quantity of the item
                Text(
                    text = "x 1",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                )
            }

        }
        Spacer(modifier = Modifier.weight(1f))

        // Modify and delete buttons
        Column (
            modifier = Modifier
                .padding(vertical = 0.dp, horizontal = 5.dp)
                .height(100.dp)
                //.border(color = Color(0xFFAAAAAA), width = 1.dp, shape = CutCornerShape(3.dp))

        ) {

            // Delete button
            Button(
                onClick = {},
                modifier = Modifier.width(65.dp).height(48.dp).padding(vertical = 0.dp),
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
                    modifier = Modifier.height(45.dp).width(45.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Modify button
            Button(
                onClick = { changeOnboardingScreen(3) },
                modifier = Modifier.width(65.dp).height(35.dp)
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
fun GroceryAddItem(modifier: Modifier = Modifier, changeOnboardingScreen: (Int) -> Unit) {

}


@Composable
fun GroceryModifyItem(modifier: Modifier = Modifier, changeOnboardingScreen: (Int) -> Unit) {

}


@Preview(showBackground = true)
@Composable
fun GroceryPreview() {
    CulinarTheme {
        Grocery()
    }
}