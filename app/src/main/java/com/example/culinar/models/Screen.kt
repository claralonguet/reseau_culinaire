package com.example.culinar.models

enum class Screen {
    // Main screens
    Account,
    Home,
    Calendar,
    Groceries,
    Recipes,
    Community,
    Settings,

    // Secondary screens
    // ... Home
    PostFeed,
    CheckFeed,
    SendMessage,
    AddFriends,
    Conversation,
    PhotoPreview,

    // ... Recipes
    RecipeList,
    RecipeDetail,

    // ... Community
    CreateCommunity,
    ListCommunities,
    MyCommunity,
    Feed,
}


val communityRelatedScreens = setOf(
    Screen.Community.name,
    Screen.CreateCommunity.name,
    Screen.ListCommunities.name,
    Screen.MyCommunity.name,
    Screen.Feed.name
)