package com.example.culinar.navigationGraph

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    sessionViewModel: SessionViewModel = viewModel()
) {
    val navController = rememberNavController()
    val username by sessionViewModel.username.collectAsState()

    val navRoutes: (String, String?) -> Unit = { screen, user ->
        val destination = if (!user.isNullOrEmpty()) "$screen?username=$user" else screen
        navController.navigate(destination) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                toAccount = { navRoutes(Screen.Account.name, username) },
                toSettings = { /* navRoutes(...) si tu as un écran de paramètres */ },
                logout = {
                    sessionViewModel.logout()
                    navRoutes(Screen.Home.name, null)
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                navRoutes = { screen -> navRoutes(screen, username) },
                navController = navController,
                username = username
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Account.name) {
                AccountScreen(authAndNavigation = { newUser ->
                    sessionViewModel.login(newUser)
                    navRoutes(Screen.Home.name, newUser)
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
                    navRoutes = { screen -> navRoutes(screen, user) },
                    username = user
                )
            }
            composable(route = Screen.Calendar.name) {CalendarScreen() }
            composable(route = Screen.Groceries.name) { Grocery() }
            composable(route = Screen.Recipes.name) { RecipeListScreen(navController = navController, vm = viewModelRecipes) }
            composable(route = Screen.Community.name) { CommunityScreen(communityViewModel = communityViewModel, navController = navController) }


            // Secondary screens routes (accessible from within main screens)

            // ...Home screen sub-routes
            composable(route = Screen.PostFeed.name) { PostFeed(navController) }
            composable(route = Screen.CheckFeed.name) { CheckFeed() }
            composable(route = Screen.SendMessage.name) { SendMessage(navController, friendViewModel) }
            composable(route = Screen.AddFriends.name) { AddFriends(friendViewModel) }
            composable(
                route = "${Screen.Conversation.name}/{username}",
                arguments = listOf(navArgument("username") {
                    type = NavType.StringType
                    nullable = false
                })
            ) { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username")
                username?.let { ConversationScreen(username = it) }
            }
            composable(
                "${Screen.PhotoPreview.name}?uri={uri}",
                arguments = listOf(
                    navArgument("uri") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val uri = backStackEntry.arguments?.getString("uri")
                PhotoPreviewScreen(imageUriString = uri)
            }


            // ...Recipes screen sub-routes
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


            // ...Community screen sub-routes
            composable(Screen.CreateCommunity.name) { CreateCommunity(communityViewModel = communityViewModel, navController = navController) }
            composable(Screen.ListCommunities.name) { ListCommunities(communityViewModel = communityViewModel, navController = navController) }
            composable(Screen.MyCommunity.name) { MyCommunity(communityViewModel = communityViewModel, navController = navController) }

        }
    }

}
