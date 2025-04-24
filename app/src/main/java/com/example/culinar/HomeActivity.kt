package com.example.culinar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.culinar.ui.theme.CulinarTheme

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
                .background(color = Color(0xFF3CB460))
        ){}


        Row(
            modifier = Modifier
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
                                .width(80.dp)
                                .height(80.dp)
                        )
                    }
                    Text("Settings", fontSize = 20.sp, color = Color(0xFF0A0A0A))
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
                            Icons.AutoMirrored.Default.ExitToApp,
                            contentDescription = "Log out",
                            tint = Color(0xFF0A0A0A),
                            modifier = modifier
                                .width(80.dp)
                                .height(80.dp)
                                .graphicsLayer {
                                    rotationZ = 180f
                                },
                        )
                    }
                    Text("Log out", fontSize = 20.sp, color = Color(0xFF0A0A0A))
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
                .background(color = Color(0xFF3CB460)),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Calendar button
            Button(
                onClick = {},
                modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
                shape = CutCornerShape(1.dp),
                colors = ButtonColors(
                    containerColor = Color(0xFF3CB460),
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
                    containerColor = Color(0xFF1C6231),
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
                modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
                shape = CutCornerShape(1.dp),
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
                modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
                shape = CutCornerShape(1.dp),
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