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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.culinar.ui.theme.CulinarTheme

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

    // Retrieve list of grocery items
    val grorecyItems = List(10) { index -> "item ${index + 1}" }

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
            onValueChange = {value -> searchText = value},
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
                items (grorecyItems) { item ->
                    GroceryItem(
                        item = item,
                        changeOnboardingScreen = changeOnboardingScreen
                    )
                }
            }
        }

        // Footer space
        Spacer(modifier = modifier.height(90.dp))
    }
}


@Composable
fun GroceryItem(modifier: Modifier = Modifier, item : String, changeOnboardingScreen: (Int) -> Unit) {

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
                onClick = {},
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
                onClick = { changeOnboardingScreen(3) },
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