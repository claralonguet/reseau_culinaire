package com.example.culinar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.culinar.ui.theme.CulinarTheme
import com.example.culinar.ui.theme.Typography
import com.example.culinar.ui.theme.darkGreen
import com.example.culinar.ui.theme.grey
import com.example.culinar.ui.theme.mediumGreen

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CulinarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Home(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun Header(modifier: Modifier = Modifier) {

    var menuClicked by remember { mutableStateOf(false) }

    Column {
        Column(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth()
                .background(color = mediumGreen)
        ){}


        Row(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
                .background(color = mediumGreen),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // App title
            Text(
                text = "Culinary",
                style = Typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 5.dp)
            )

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
            TextButton(
                onClick = {menuClicked = !menuClicked},
                modifier = Modifier.width(85.dp).height(85.dp).padding(0.dp),
                shape = CutCornerShape(3.dp),
                colors = ButtonColors(
                    containerColor = if (menuClicked) darkGreen else mediumGreen,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.Face,
                    contentDescription = "Clear",
                    tint = Color.White,
                    modifier = Modifier.width(40.dp).height(40.dp)
                )
            }
        }

        // Action bar with profile and settings options
        if (menuClicked) {
            Column(
                modifier = Modifier
                    .background(color = grey)
                    .fillMaxWidth()
                    .height(180.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally

                ) {

                // Profile settings button
                TextButton(
                    onClick = { menuClicked = !menuClicked },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = CutCornerShape(3.dp),
                    colors = ButtonColors(
                        containerColor = Color(0x00000000),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0x00000000),
                        disabledContentColor = Color.White
                    )
                ) {

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Outlined.AccountCircle,
                            contentDescription = "Account",
                            tint = Color(0xFF0A0A0A),
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                        )
                        Text("Compte", style = Typography.bodyMedium, color = Color(0xFF0A0A0A), modifier = Modifier.padding(horizontal = 10.dp))
                    }
                }

                // Settings button
                TextButton(
                    onClick = { menuClicked = !menuClicked },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = CutCornerShape(3.dp),
                    colors = ButtonColors(
                        containerColor = Color(0x00000000),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0x00000000),
                        disabledContentColor = Color.White
                    )
                ) {

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                        ) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFF0A0A0A),
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                        )
                        Text("Paramètres", style = Typography.bodyMedium, color = Color(0xFF0A0A0A), modifier = Modifier.padding(horizontal = 10.dp))
                    }
                }

                // Log out button
                TextButton (
                    onClick = { menuClicked = !menuClicked },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = CutCornerShape(3.dp),
                    colors = ButtonColors(
                        containerColor = Color(0x00000000),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0x00000000),
                        disabledContentColor = Color.White
                    )
                ) {

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.AutoMirrored.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color(0xFF0A0A0A),
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                .graphicsLayer {
                                    rotationZ = 180f
                                }
                        )
                        Text("Déconnexion", style = Typography.bodyMedium, color = Color(0xFF0A0A0A), modifier = Modifier.padding(horizontal = 10.dp))
                    }
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
            modifier = Modifier
                .height(90.dp)
                .fillMaxWidth()
                .background(color = mediumGreen),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Calendar button
            Button(
                onClick = {},
                modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
                shape = CutCornerShape(1.dp),
                colors = ButtonColors(
                    containerColor = mediumGreen,
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
                modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
                shape = CutCornerShape(1.dp),
                colors = ButtonColors(
                    containerColor = darkGreen,
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
                modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
                shape = CutCornerShape(1.dp),
                colors = ButtonColors(
                    containerColor = mediumGreen,
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
                modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
                shape = CutCornerShape(1.dp),
                colors = ButtonColors(
                    containerColor = mediumGreen,
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
                modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
                shape = CutCornerShape(1.dp),
                colors = ButtonColors(
                    containerColor = mediumGreen,
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
fun Home(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
    Column {  }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    CulinarTheme {
        Home("Android")
    }
}