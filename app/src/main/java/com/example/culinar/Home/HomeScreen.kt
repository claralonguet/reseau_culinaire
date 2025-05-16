package com.example.culinar.Home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.culinar.ui.theme.Typography
import com.example.culinar.ui.theme.darkGreen
import com.example.culinar.ui.theme.grey
import com.example.culinar.ui.theme.mediumGreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.culinar.models.Screen



// Top bar
@Composable
fun TopBar(modifier: Modifier = Modifier, toAuth : () -> Unit = {}) {

    var menuClicked by remember { mutableStateOf(false) }

    Column {
        Column(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth()
                .background(color = mediumGreen)
        ) {}


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
                onClick = { menuClicked = !menuClicked },
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
                    onClick = { toAuth() },
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

                    Row(
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
                        Text(
                            "Compte",
                            style = Typography.bodyMedium,
                            color = Color(0xFF0A0A0A),
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
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

                    Row(
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
                        Text(
                            "Paramètres",
                            style = Typography.bodyMedium,
                            color = Color(0xFF0A0A0A),
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }
                }

                // Log out button
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

                    Row(
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
                        Text(
                            "Déconnexion",
                            style = Typography.bodyMedium,
                            color = Color(0xFF0A0A0A),
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }


                }
            }
        }
    }
}

// Bottom bar
@Composable
fun BottomNavBar(modifier: Modifier = Modifier, navRoutes: (String) -> Unit = {}, navController : NavHostController = rememberNavController()) {

    val currentBackStackEntr by navController.currentBackStackEntryAsState()
    var screenName by remember { mutableStateOf( currentBackStackEntr?.destination?.route ?: Screen.Home.name ) }


    NavigationBar(
        modifier = Modifier.fillMaxWidth().height(85.dp),
        contentColor = Color.White,
        containerColor = mediumGreen // Couleur de fond de la barre
    ) {
        // Calendar button
        NavigationBarItem(
            icon = { Icon(
                if(screenName == Screen.Calendar.name) Icons.Default.DateRange else Icons.Outlined.DateRange,
                contentDescription = "Calendar",
                tint = Color.White,
                modifier = Modifier.width(40.dp).height(40.dp)
            ) },
            modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.White,
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.White,
            ),
            // label = { Text("Calendar") },
            selected = false,
            onClick = {
                screenName = Screen.Calendar.name
                navRoutes(Screen.Calendar.name) }
        )

        // Groceries button
        NavigationBarItem(
            icon = { Icon(
                if(screenName == Screen.Groceries.name) Icons.Default.ShoppingCart else Icons.Outlined.ShoppingCart,
                contentDescription = "Groceries",
                tint = Color.White,
                modifier = Modifier.width(40.dp).height(40.dp)
            ) },
            modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.White,
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.White,
            ),
            // label = { Text("Groceries") },
            selected = false,
            onClick = {
                screenName = Screen.Groceries.name
                navRoutes(Screen.Groceries.name)
            }
        )

        // Home button
        NavigationBarItem(
            icon = { Icon(
                if(screenName == Screen.Home.name) Icons.Default.Home else Icons.Outlined.Home,
                contentDescription = "Home",
                tint = Color.White,
                modifier = Modifier.width(40.dp).height(40.dp)
            ) },
            modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.White,
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.White,
            ),
            // label = { Text("Home") },
            selected = false,
            onClick = {
                screenName = Screen.Home.name
                navRoutes(Screen.Home.name)
            }
        )

        // Recipes button
        NavigationBarItem(
            icon = { Icon(
                if(screenName == Screen.Recipes.name) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                contentDescription = "Recipes",
                tint = Color.White,
                modifier = Modifier.width(40.dp).height(40.dp)
            ) },
            modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.White,
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.White,
            ),
            // label = { Text("Recipes") },
            selected = false,
            onClick = {
                screenName = Screen.Recipes.name
                navRoutes(Screen.Recipes.name)
            }
        )

        // Community button
        NavigationBarItem(
            icon = { Icon(
                if(screenName == Screen.Community.name) Icons.Default.Email else Icons.Outlined.Email,
                contentDescription = "Community",
                tint = Color.White,
                modifier = Modifier.width(40.dp).height(40.dp)
            ) },
            modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.White,
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.White,
            ),
            // label = { Text("Community") },
            selected = false,
            onClick = {
                screenName = Screen.Community.name
                navRoutes(Screen.Community.name)
            }
        )
    }
}


@Composable
fun Home(navRoutes: (String) -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Couleur de fond personnalisée
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.7f) // 70% de la largeur
        ) {
            Button(
                onClick = { navRoutes(Screen.PostFeed.name) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = grey,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Poster sur mon feed")
            }

            Button(
                onClick = { navRoutes(Screen.CheckFeed.name) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = grey,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Accéder à mon feed")
            }

            Button(
                onClick = { navRoutes(Screen.SendMessage.name
                ) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = grey,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Chatter avec des amis")
            }

            Button(
                onClick = { navRoutes(Screen.AddFriends.name) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = grey,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Ajouter des amis")
            }
        }
    }
}



/*/ Footer bar
@Composable
fun Footer(modifier: Modifier = Modifier, screenName: String = Screen.Home.name, navRoutes: (Screen) -> Unit = {}) {

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
                onClick = { navRoutes(Screen.Calendar) },
                modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
                shape = CutCornerShape(1.dp),
                colors = ButtonColors(
                    containerColor = if(screenName == Screen.Calendar.name) darkGreen else mediumGreen,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Calendar",
                    tint = Color.White,
                    modifier = Modifier.width(40.dp).height(40.dp)
                )

            }

            // Groceries button
            Button(
                onClick = { navRoutes(Screen.Groceries) },
                modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
                shape = CutCornerShape(1.dp),
                colors = ButtonColors(
                    containerColor = if(screenName == Screen.Groceries.name) darkGreen else mediumGreen,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Groceries",
                    tint = Color.White,
                    modifier = Modifier.width(45.dp).height(45.dp)
                )
            }


            // Home button
            Button(
                onClick = { navRoutes(Screen.Home) },
                modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
                shape = CutCornerShape(1.dp),
                colors = ButtonColors(
                    containerColor = if(screenName == Screen.Home.name) darkGreen else mediumGreen,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    tint = Color.White,
                    modifier = Modifier.width(45.dp).height(45.dp)
                )
            }


            // Recipes button
            Button(
                onClick = { navRoutes(Screen.Recipes) },
                modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
                shape = CutCornerShape(1.dp),
                colors = ButtonColors(
                    containerColor = if(screenName == Screen.Recipes.name) darkGreen else mediumGreen,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Recipes",
                    tint = Color.White,
                    modifier = Modifier.width(45.dp).height(45.dp)
                )
            }


            // Community button
            Button(
                onClick = { navRoutes(Screen.Community) },
                modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
                shape = CutCornerShape(1.dp),
                colors = ButtonColors(
                    containerColor = if(screenName == Screen.Community.name) darkGreen else mediumGreen,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.Email,
                    contentDescription = "Community",
                    tint = Color.White,
                    modifier = Modifier.width(45.dp).height(45.dp)
                )
            }

            //

        }
    }
}
*/