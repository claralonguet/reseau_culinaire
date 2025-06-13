package com.example.culinar.navigationGraph

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.culinar.AccountScreens.AccountScreen
import com.example.culinar.CommunityScreens.AddFriends
import com.example.culinar.Home.BottomNavBar
import com.example.culinar.CalendarScreens.CalendarScreen
import com.example.culinar.CommunityScreens.CheckFeed
import com.example.culinar.CommunityScreens.CommunityScreen
import com.example.culinar.CommunityScreens.ConversationScreen
import com.example.culinar.CommunityScreens.CreateCommunity
import com.example.culinar.CommunityScreens.ListCommunities
import com.example.culinar.CommunityScreens.MyCommunity
import com.example.culinar.GroceriesScreens.Grocery
import com.example.culinar.Home.TopBar
import com.example.culinar.Home.Home
import com.example.culinar.CommunityScreens.PhotoPreviewScreen
import com.example.culinar.CommunityScreens.PostFeed
import com.example.culinar.CommunityScreens.SendMessage
import com.example.culinar.models.CommunityScreens
import com.example.culinar.models.viewModels.CommunityViewModel
import com.example.culinar.models.viewModels.FriendViewModel
import com.example.culinar.models.Screen
import com.example.culinar.ui.screens.RecipeDetailScreen
import com.example.culinar.ui.screens.RecipeListScreen
import com.example.culinar.viewmodels.RecipeViewModel

@Composable
fun CulinarApp(
    modifier: Modifier = Modifier,
    //viewModel: CulinarViewModel = viewModel(),
    friendViewModel: FriendViewModel = viewModel(),
    viewModelRecipes: RecipeViewModel = viewModel(),
    communityViewModel: CommunityViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Determine if the top bar should be shown based on the current route
    val showTopBar = when (currentRoute) {
        Screen.Account.name -> false
        CommunityScreens.MyCommunity.name -> false
        CommunityScreens.Feed.name -> false
        else -> true // Default to showing it
    }

    // Determine if the bottom navbar should be shown based on the current route
    val showBottomBar = when (currentRoute) {
        Screen.Account.name -> false
        CommunityScreens.MyCommunity.name -> false
        CommunityScreens.Feed.name -> false
        else -> true // Default to showing it
    }

    val onNavigate = { screenRoute: String ->
        navController.navigate(screenRoute) {
            // Optional: Configuring navigation behavior (e.g., pop up to a specific destination)
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            // Avoiding building up a large stack of the same destination
            launchSingleTop = true

            // Restoring state when reselecting a previously selected item
            restoreState = true
        }
    }

    Scaffold (
        topBar = { if (showTopBar) TopBar(toAuth = { onNavigate(Screen.Account.name) }) },
        bottomBar = { if (showBottomBar) BottomNavBar(currentRoute = currentRoute, onNavigate = onNavigate) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {

            // Main screens routes (displayed on the bottom navbar)

            composable(route = Screen.Account.name) {
                val previousRoute = navController.previousBackStackEntry?.destination?.route ?: Screen.Home.name
                AccountScreen(authAndNavigation = { onNavigate(previousRoute) })
            }
            composable(route = Screen.Home.name) { Home(onNavigate = onNavigate) }
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
