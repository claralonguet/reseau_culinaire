package com.example.culinar.Home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.culinar.R
import com.example.culinar.models.Screen
import com.example.culinar.models.communityRelatedScreens
import com.example.culinar.ui.theme.grey
import kotlinx.coroutines.delay

@Composable
/**
 * Top navigation bar of the app, showing the app name and a profile menu.
 *
 * - Displays the app name on the left.
 * - Shows a profile icon button on the right that toggles a dropdown menu.
 * - The dropdown menu provides options based on login state:
 *   - If not logged in: "Compte" (Account) to navigate to login/account.
 *   - Always shows "Paramètres" (Settings).
 *   - If logged in: "Déconnexion" (Logout).
 *
 * @param isLoggedIn Indicates if the user is currently logged in, affects menu options shown.
 * @param toAccount Lambda called when user selects the account/login option.
 * @param toSettings Lambda called when user selects settings.
 * @param logout Lambda called when user selects logout.
 * @param showSnackbar Lambda to display a Snackbar message.
 */
fun TopBar(
    isLoggedIn: Boolean = false,
    toAccount: () -> Unit = {},
    toSettings: () -> Unit = {},
    logout: () -> Unit = {},
    showSnackbar: (String) -> Unit
) {

    var menuClicked by remember { mutableStateOf(false) } // Tracks whether dropdown menu is open

    Column {
        // Thin colored top line as visual accent
        Column(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
        ) {}

        // Main row containing app name and profile menu button
        Row(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 5.dp)
            )

            Spacer(modifier = Modifier.weight(1f)) // Push menu button to right

            // Profile icon button toggling the dropdown menu
            TextButton(
                onClick = { menuClicked = !menuClicked },
                modifier = Modifier
                    .width(85.dp)
                    .height(85.dp)
                    .padding(0.dp),
                shape = CutCornerShape(3.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (menuClicked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.Face,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.width(40.dp).height(40.dp)
                )
            }
        }

        // Dropdown menu content shown when menuClicked == true
        if (menuClicked) {
            Column(
                modifier = Modifier
                    .background(color = grey)
                    .fillMaxWidth()
                    .height(180.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Account/Login option shown only if user is NOT logged in
                if(!isLoggedIn) {
                    TextButton(
                        onClick = {
                            toAccount()
                            menuClicked = false
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = CutCornerShape(3.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Black
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Outlined.AccountCircle,
                                contentDescription = "Se connecter",
                                tint = Color.Black,
                                modifier = Modifier.size(30.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Compte", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                // Settings option always shown
                TextButton(
                    onClick = {
                        toSettings()
                        menuClicked = false
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = CutCornerShape(3.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Black
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
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Paramètres", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // Logout option shown only if user IS logged in
                if(isLoggedIn) {
                    TextButton(
                        onClick = {
                            logout()
                            menuClicked = false
                            showSnackbar("Déconnexion réussie")
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = CutCornerShape(3.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.AutoMirrored.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color.Black,
                                modifier = Modifier.size(30.dp).graphicsLayer { rotationZ = 180f }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Déconnexion", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}




@Composable
        /**
         * Bottom navigation bar for the app.
         *
         * Provides quick access to key app screens via icons:
         * - Calendar
         * - Groceries
         * - Home (with optional username parameter in route)
         * - Recipes
         * - Community
         *
         * The icon and selection state update dynamically based on the current route.
         *
         * @param modifier Modifier to be applied to the NavigationBar container.
         * @param navRoutes Lambda to navigate to a given route string when a nav item is clicked.
         * @param navController NavHostController to observe current navigation back stack entry.
         * @param username Optional username string to append as query param on home route navigation.
         */
fun BottomNavBar(
    modifier: Modifier = Modifier,
    navRoutes: (String) -> Unit = {},
    navController: NavHostController,
    username: String?
) {
    // Observe current navigation back stack entry state
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    // Derive current screen route name from navigation destination, default to Home
    val screenName by remember {
        derivedStateOf { currentBackStackEntry?.destination?.route ?: Screen.Home.name }
    }

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        //contentColor = Color(0x33FFFFFF) // (commented out, could be customized)
    ) {
        // Calendar navigation item
        NavigationBarItem(
            icon = {
                Icon(
                    if (screenName == Screen.CheckFeed.name) Icons.Default.Public else Icons.Outlined.Public,
                    contentDescription = "Feed",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            },
            selected = screenName == Screen.CheckFeed.name,
            onClick = { navRoutes(Screen.CheckFeed.name) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )


        // Groceries navigation item
        NavigationBarItem(
            icon = {
                Icon(
                    if (screenName == Screen.Groceries.name) Icons.Default.ShoppingCart else Icons.Outlined.ShoppingCart,
                    contentDescription = "Groceries",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            },
            selected = screenName == Screen.Groceries.name,
            onClick = { navRoutes(Screen.Groceries.name) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        // Home navigation item; includes username as query param if available
        NavigationBarItem(
            icon = {
                Icon(
                    if (screenName.startsWith(Screen.Home.name)) Icons.Default.Home else Icons.Outlined.Home,
                    contentDescription = "Home",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            },
            selected = screenName.startsWith(Screen.Home.name),
            onClick = {
                val route = if (!username.isNullOrEmpty()) {
                    "${Screen.Home.name}?username=$username"
                } else {
                    Screen.Home.name
                }
                navRoutes(route)
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        // Recipes navigation item
        NavigationBarItem(
            icon = {
                Icon(
                    if (screenName == Screen.Recipes.name) painterResource(R.drawable.food_filled) else painterResource(R.drawable.food_outlined),
                    contentDescription = "Recipes",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            },
            selected = screenName == Screen.Recipes.name,
            onClick = { navRoutes(Screen.Recipes.name) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        // Community navigation item; selected if current screen is part of community-related screens
        NavigationBarItem(
            icon = {
                Icon(
                    if (screenName in communityRelatedScreens)
                        Icons.Default.Email else Icons.Outlined.Email,
                    contentDescription = "Community",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            },
            selected = screenName == Screen.Community.name,
            onClick = { navRoutes(Screen.Community.name) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}




/**
 * SlideUpSnackbar displays a Snackbar with a slide-up animation when visible.
 *
 * @param message The text message to display inside the Snackbar.
 * @param isVisible Boolean flag to control visibility of the Snackbar.
 * @param onDismiss Callback triggered to hide the Snackbar (e.g., set isVisible to false).
 */
@Composable
fun SlideUpSnackbar(
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit // Callback to set isVisible to false after animation
) {
    // Automatically dismiss after a delay when it becomes visible
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(3000L) // Display the Snackbar for 2 seconds before triggering dismissal
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(), // Slide up from half height and fade in
        exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(), // Slide down to half height and fade out
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp) // Some padding around the snackbar
    ) {
        // We use a Box to align the Snackbar at the bottom of its AnimatedVisibility container
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter // Align Snackbar at the bottom center
        ) {
            Snackbar(
                modifier = Modifier.padding(bottom = 100.dp), // Extra padding from the bottom edge
                action = {
                    // "Dismiss" button allows user to close the Snackbar manually
                    TextButton(onClick = onDismiss) {
                        Text("Dismiss", color = MaterialTheme.colorScheme.inversePrimary)
                    }
                }
            ) {
                Text(text = message) // Main message text
            }
        }
    }
}




@Composable
        /**
         * Home screen showing a personalized welcome and main user actions.
         *
         * Displays the username or "Invité" if no username is provided.
         * Provides buttons to navigate to:
         * - Posting on the user's feed
         * - Viewing the user's feed
         * - Chatting with friends
         * - Adding new friends
         *
         * @param navRoutes Lambda to navigate to a specified route string.
         * @param username Optional username to personalize the welcome message.
         * @param showSnackbar Lambda to display a Snackbar message.
         */
fun Home(
    navRoutes: (String) -> Unit,
    username: String?,
    showSnackbar: (String) -> Unit
) {
    // Determine display name: username if provided, otherwise "Invité"
    val displayName = username.takeUnless { it.isNullOrEmpty() } ?: "Invité"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display the username or guest label
            Text(
                text = "Utilisateur : $displayName",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Navigation buttons for main user actions
            Button(
                onClick = {
                    // If the user is not logged in, show error message, requiring him to log in
                    if (username == null) {
                        Log.d("CulinarApp", "User not logged in. Showing error message.")
                        navRoutes(Screen.Account.name)
                        showSnackbar("Please log in to post on the feed.")
                    }
                    else
                        navRoutes(Screen.PostFeed.name)

                          },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Poster sur mon feed")
            }

            Button(
                onClick = { navRoutes(Screen.CheckFeed.name) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Accéder à mon feed")
            }

            Button(
                onClick = {
                    // If the user is not logged in, show error message, requiring him to log in
                    if (username == null) {
                        Log.d("CulinarApp", "User not logged in. Showing error message.")
                        navRoutes(Screen.Account.name)
                        showSnackbar("Please log in to post on the feed.")
                    }
                    else
                        navRoutes(Screen.SendMessage.name)
                          },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Chatter avec des amis")
            }

            Button(
                onClick = {
                    // If the user is not logged in, show error message, requiring him to log in
                    if (username == null) {
                        Log.d("CulinarApp", "User not logged in. Showing error message.")
                        navRoutes(Screen.Account.name)
                        showSnackbar("Please log in to post on the feed.")
                    }
                    else
                        navRoutes(Screen.AddFriends.name)
                          },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Ajouter des amis")
            }
        }
    }
}



