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

    val username by sessionViewModel.username.collectAsState()
    val isExpert by sessionViewModel.isExpert.collectAsState()
    val idConnect by sessionViewModel.id.collectAsState()

    // Setting session id into viewModels
    communityViewModel.setUserId(idConnect ?: "")
    generalPostViewModel.setUserId(idConnect ?: "")

    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStackEntry?.destination?.route

    val hideBarsRoutes = listOf(
        Screen.Account.name,
        Screen.MyCommunity.name,
        Screen.PostFeed.name,
        Screen.CheckFeed.name,
        Screen.SendMessage.name,
        Screen.Conversation.name
    )

    val showTopBar = currentRoute?.let { it !in hideBarsRoutes } ?: true
    val showBottomBar = currentRoute?.let { it !in hideBarsRoutes } ?: true

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
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
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
            composable(route = "${Screen.Account.name}?nextRoute={nextRoute}") { backStackEntry ->
                val nextRoute = backStackEntry.arguments?.getString("nextRoute")

                AccountScreen(authAndNavigation = {id, username ->
                    sessionViewModel.login(id, username)
                    onNavigate(if (nextRoute == null) Screen.Home.name else nextRoute, username)
                })
            }

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
                        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                    } else {
                        SettingScreen(
                            sessionViewModel = sessionViewModel,
                            navController = navController,
                            onRequestSent = {
                                // Par exemple revenir à la page précédente
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
            // *** Nouvelle route ExpertRequestsScreen ***
            composable(route = Screen.PendingExpertRequests.name) {
                ExpertRequestsScreen()
            }

            // Autres routes inchangées ...
            composable(route = Screen.Calendar.name) { CalendarScreen() }
            composable(route = Screen.Groceries.name) {
                Grocery(
                    sessionViewModel = sessionViewModel,
                    onNavigate = onNavigate
                )
            }
            composable(route = Screen.Recipes.name) { RecipeListScreen(navController = navController, vm = viewModelRecipes) }
            composable(route = Screen.Community.name) {
                // Check if there's a user ID in the session
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
            composable(route = Screen.PostFeed.name) {
                // PostFeed(navController)
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
            composable(route = Screen.CheckFeed.name) {
                CheckFeed(navController = navController)
            }
            composable(
                route = "comments/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                CommentsScreen(postId = postId, navController = navController)
            }
            composable("SendMessage") {
                val userId = idConnect
                if (userId == null) return@composable
                SendMessage(navController, userId)
            }
            composable("AddFriends") {
                when (val userId = idConnect) {
                    null -> CircularProgressIndicator()
                    else -> AddFriends(currentUserId = userId, viewModel = friendViewModel)
                }
            }
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
            composable(Screen.CreateCommunity.name) { CreateCommunity(communityViewModel = communityViewModel, onNavigate = onNavigate) }
            composable(Screen.ListCommunities.name) { ListCommunities(communityViewModel = communityViewModel, onNavigate = onNavigate) }
            composable(Screen.MyCommunity.name) { MyCommunity(communityViewModel = communityViewModel, goBack = { navController.popBackStack() }, createPost = { navController.navigate(Screen.CreatePost.name) }) }
            composable(Screen.Feed.name) { Feed(communityViewModel = communityViewModel, goBack = { navController.popBackStack() }) }
            composable(Screen.CreatePost.name) {
                CreatePost(
                    communityViewModel = communityViewModel,
                    createPost = { post ->
                        if (communityViewModel.selectedCommunity == null) return@CreatePost
                        Log.d("CulinarApp", "Creating post in community ${communityViewModel.selectedCommunity?.id}")
                        communityViewModel.createPost(post, communityViewModel.selectedCommunity?.id ?: "")
                        Log.d("CulinarApp", "Post created successfully")
                                 },
                    createRecipe = { recipe ->
                        // communityViewModel.createRecipe(recipe)
                    },
                    goBack = { navController.popBackStack() }
                )
            }

        }
    }
}
