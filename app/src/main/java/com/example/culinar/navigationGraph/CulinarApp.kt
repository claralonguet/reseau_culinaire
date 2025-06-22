package com.example.culinar.navigationGraph

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.culinar.AccountScreens.AccountScreen
import com.example.culinar.CalendarScreens.CalendarScreen
import com.example.culinar.CommunityScreens.*
import com.example.culinar.CommunityScreens.AddFriends
import com.example.culinar.CommunityScreens.CheckFeed
import com.example.culinar.CommunityScreens.CommunityScreen
import com.example.culinar.CommunityScreens.ConversationScreen
import com.example.culinar.CommunityScreens.CreateCommunity
import com.example.culinar.CommunityScreens.ListCommunities
import com.example.culinar.CommunityScreens.MyCommunity
import com.example.culinar.CommunityScreens.PhotoPreviewScreen
import com.example.culinar.CommunityScreens.PostFeed
import com.example.culinar.CommunityScreens.SendMessage
import com.example.culinar.GroceriesScreens.Grocery
import com.example.culinar.Home.BottomNavBar
import com.example.culinar.Home.Home
import com.example.culinar.Home.TopBar

import com.example.culinar.models.Screen
import com.example.culinar.models.viewModels.CommunityViewModel
import com.example.culinar.models.viewModels.FriendViewModel
import com.example.culinar.models.viewModels.GeneralPostViewModel
import com.example.culinar.settings.ExpertRequestsScreen
import com.example.culinar.settings.SettingScreen
import com.example.culinar.ui.screens.RecipeDetailScreen
import com.example.culinar.ui.screens.RecipeListScreen
import com.example.culinar.viewmodels.RecipeViewModel
import com.example.culinar.viewmodels.SessionViewModel

/**
 * Main application composable for the Culinar app.
 *
 * Sets up the navigation graph, manages top and bottom bar visibility based on current route,
 * and handles user session state propagation to various view models.
 *
 * @param modifier Modifier applied to the root layout composable.
 * @param friendViewModel ViewModel handling friend data and logic.
 * @param viewModelRecipes ViewModel managing recipes data and operations.
 * @param communityViewModel ViewModel managing community data and membership state.
 * @param sessionViewModel ViewModel managing user session, authentication, and user info.
 * @param generalPostViewModel ViewModel managing general public feed posts.
 */
@Composable
fun CulinarApp(
    modifier: Modifier = Modifier,
    friendViewModel: FriendViewModel = viewModel(),
    viewModelRecipes: RecipeViewModel = viewModel(),
    communityViewModel: CommunityViewModel = viewModel(),
    sessionViewModel: SessionViewModel = viewModel(),
    generalPostViewModel: GeneralPostViewModel = viewModel()
) {
    val navController = rememberNavController()

    // Collect session data as State from the SessionViewModel
    val username by sessionViewModel.username.collectAsState()
    val isExpert by sessionViewModel.isExpert.collectAsState()
    val idConnect by sessionViewModel.id.collectAsState()

    // Propagate current user ID to ViewModels that require it for filtering/fetching
    communityViewModel.setUserId(idConnect ?: "")
    generalPostViewModel.setUserId(idConnect ?: "")

    // Get current navigation route from NavController's back stack entry
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStackEntry?.destination?.route

    // List of routes where top and bottom navigation bars should be hidden
    val hideBarsRoutes = listOf(
        Screen.Account.name,
        Screen.MyCommunity.name,
        Screen.PostFeed.name,
        Screen.CheckFeed.name,
        Screen.SendMessage.name,
        Screen.Conversation.name
    )

    // Determine visibility of top and bottom bars based on current route
    val showTopBar = currentRoute?.let { it !in hideBarsRoutes } ?: true
    val showBottomBar = currentRoute?.let { it !in hideBarsRoutes } ?: true

    // Navigation lambda function that handles parameterized route building and navigation options
    val onNavigate: (String, String?) -> Unit = { screenRoute, userIdOrUsername ->
        val destination = when (screenRoute) {
            "AddFriends" -> "AddFriends"
            "SendMessage" -> "SendMessage"
            Screen.Conversation.name -> screenRoute
            else -> {
                if (!userIdOrUsername.isNullOrEmpty()) {
                    "$screenRoute?username=$userIdOrUsername"
                } else screenRoute
            }
        }

        navController.navigate(destination) {
            // Pop up to the start destination to avoid building a large back stack
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true  // Avoid multiple copies of the same destination
            restoreState = true     // Restore saved state if possible
        }
    }

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopBar(
                    isLoggedIn = !username.isNullOrEmpty(),
                    toAccount = { onNavigate(Screen.Account.name, username) },
                    toSettings = { onNavigate(Screen.Settings.name, username) },
                    logout = {
                        sessionViewModel.logout()
                        onNavigate(Screen.Home.name, null)
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    navRoutes = { screen -> onNavigate(screen, username) },
                    navController = navController,
                    username = username
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Account screen with optional nextRoute to redirect after successful login
            composable(route = "${Screen.Account.name}?nextRoute={nextRoute}") { backStackEntry ->
                val nextRoute = backStackEntry.arguments?.getString("nextRoute")
                AccountScreen(authAndNavigation = { id, username ->
                    sessionViewModel.login(id, username)
                    onNavigate(if (nextRoute == null) Screen.Home.name else nextRoute, username)
                })
            }

            // Home screen showing personalized content, username parameter is optional
            composable(
                route = "${Screen.Home.name}?username={username}",
                arguments = listOf(navArgument("username") {
                    type = NavType.StringType
                    nullable = true
                })
            ) { backStackEntry ->
                val user = backStackEntry.arguments?.getString("username") ?: username
                Home(
                    navRoutes = { screen -> onNavigate(screen, user) },
                    username = user
                )
            }

            // Settings screen requiring authentication; redirects if not logged in
            composable(
                route = "${Screen.Settings.name}?username={username}",
                arguments = listOf(navArgument("username") {
                    type = NavType.StringType
                    nullable = true
                })
            ) { backStackEntry ->
                val user = backStackEntry.arguments?.getString("username") ?: username
                if (user.isNullOrBlank()) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Account.name) {
                            popUpTo(Screen.Home.name) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                } else {
                    if (isExpert == null) {
                        // Show loading indicator while user expertise status is unknown
                        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                    } else {
                        SettingScreen(
                            sessionViewModel = sessionViewModel,
                            navController = navController,
                            onRequestSent = { navController.popBackStack() }
                        )
                    }
                }
            }

            // Other screens for expert requests, calendar, groceries, recipes, etc.
            composable(route = Screen.PendingExpertRequests.name) {
                ExpertRequestsScreen()
            }
            composable(route = Screen.Calendar.name) {
                CalendarScreen()
            }
            composable(route = Screen.Groceries.name) {
                Grocery(sessionViewModel = sessionViewModel, onNavigate = onNavigate)
            }
            composable(route = Screen.Recipes.name) {
                RecipeListScreen(navController = navController, vm = viewModelRecipes)
            }

            // Community screen varies behavior based on login state
            composable(route = Screen.Community.name) {
                if (idConnect == null) {
                    Log.d("CommunityScreen", "No user ID found in the session.")
                    CommunityScreen(
                        communityViewModel = communityViewModel,
                        onNavigate = onNavigate
                    )
                } else {
                    Log.d("CommunityScreen", "User $idConnect is logged in.")
                    CommunityScreen(
                        communityViewModel = communityViewModel,
                        sessionViewModel = sessionViewModel,
                        onNavigate = onNavigate
                    )
                }
            }

            // Post feed screen for general public posts
            composable(route = Screen.PostFeed.name) {
                CreateGeneralFeedPost(
                    generalPostViewModel = generalPostViewModel,
                    createPost = { post ->
                        generalPostViewModel.setUserId(idConnect ?: "")
                        Log.d("CulinarApp", "Creating post in public feed.")
                        generalPostViewModel.createPost(post)
                        Log.d("CulinarApp", "Post created successfully")
                    },
                    goBack = { navController.popBackStack() }
                )
            }

            // Check feed screen showing user feeds
            composable(route = Screen.CheckFeed.name) {
                CheckFeed(navController = navController)
            }

            // Comments screen with postId argument to show comments for a specific post
            composable(
                route = "comments/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                CommentsScreen(postId = postId, navController = navController)
            }

            // Send message screen requiring user to be logged in
            composable("SendMessage") {
                val userId = idConnect
                if (userId == null) return@composable
                SendMessage(navController, userId)
            }

            // Add friends screen with loading indicator if user ID is not available yet
            composable("AddFriends") {
                when (val userId = idConnect) {
                    null -> CircularProgressIndicator()
                    else -> AddFriends(currentUserId = userId, viewModel = friendViewModel)
                }
            }

            // Conversation screen between users identified by userId parameter
            composable(
                route = "${Screen.Conversation.name}/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val targetUserId = backStackEntry.arguments?.getString("userId")
                val currentUserId = idConnect

                if (targetUserId == null || currentUserId == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    ConversationScreen(userId = targetUserId, currentUserId = currentUserId)
                }
            }

            // Photo preview screen with optional URI parameter
            composable(
                "${Screen.PhotoPreview.name}?uri={uri}",
                arguments = listOf(navArgument("uri") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val uri = backStackEntry.arguments?.getString("uri")
                PhotoPreviewScreen(imageUriString = uri)
            }

            // Recipe list and detail screens
            composable(Screen.RecipeList.name) {
                RecipeListScreen(navController = navController, vm = viewModelRecipes)
            }
            composable("${Screen.RecipeDetail.name}/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                val recipe = viewModelRecipes.findById(id ?: "")
                if (recipe != null) {
                    RecipeDetailScreen(recipe = recipe)
                }
            }

            // Community management screens for creating, listing, and viewing communities
            composable(Screen.CreateCommunity.name) {
                CreateCommunity(
                    communityViewModel = communityViewModel,
                    onNavigate = onNavigate
                )
            }
            composable(Screen.ListCommunities.name) {
                ListCommunities(
                    communityViewModel = communityViewModel,
                    onNavigate = onNavigate
                )
            }
            composable(Screen.MyCommunity.name) {
                MyCommunity(
                    communityViewModel = communityViewModel,
                    goBack = { navController.popBackStack() },
                    createPost = { navController.navigate(Screen.CreatePost.name) }
                )
            }
            composable(Screen.Feed.name) {
                Feed(
                    communityViewModel = communityViewModel,
                    goBack = { navController.popBackStack() }
                )
            }

            // Post creation screen allowing standard or recipe post creation within selected community
            composable(Screen.CreatePost.name) {

                //
                val selectedCommunity = communityViewModel.selectedCommunity.collectAsState().value
                CreatePost(
                    communityViewModel = communityViewModel,
                    createPost = { post ->
                        if (selectedCommunity == null) return@CreatePost
                        Log.d("CulinarApp", "Creating post in community ${selectedCommunity.id}")
                        communityViewModel.createPost(post, selectedCommunity.id)
                        Log.d("CulinarApp", "Post created successfully")
                    },
                    createRecipe = { recipe ->
                        // TODO: Optionally handle recipe creation here
                    },
                    goBack = { navController.popBackStack() }
                )
            }
        }
    }
}


