package com.example.culinar.CommunityScreens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culinar.R
import com.example.culinar.models.Community
import com.example.culinar.models.viewModels.CommunityViewModel
import com.example.culinar.ui.theme.darkGrey
import com.example.culinar.ui.theme.grey
import com.example.culinar.ui.theme.lightGrey
import kotlinx.coroutines.launch



/**
 * Displays a list of communities for browsing, joining/leaving, and navigation.
 *
 * Shows a header with a back button and a title, followed by a scrollable list of community cards.
 * Each community card displays info and membership actions.
 *
 * @param toCommunity Callback invoked when the user wants to navigate to a selected community.
 * @param backToHome Callback invoked to navigate back to the home screen.
 * @param communityViewModel ViewModel providing the list of all communities and membership states.
 */
@Composable
fun BrowseCommunities(
	toCommunity: (Community) -> Unit = {},
	backToHome: () -> Unit = {},
	communityViewModel: CommunityViewModel = viewModel()
) {
	Log.d("CommunityScreen", "Composing BrowseCommunities")

	// Collect all communities from the ViewModel as a state
	val communities = communityViewModel.allCommunities.collectAsState().value

	Column {
		// Header row with a back button and the screen title
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
				.height(80.dp)
				.background(color = grey)
		) {
			// Back button to return to home screen
			TextButton(
				onClick = { backToHome() },
				shape = CutCornerShape(3.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = Color(0x0059EA85), // Transparent greenish background
					contentColor = Color.White,
					disabledContainerColor = Color(0xFF59EA85), // Opaque green when disabled
					disabledContentColor = Color.White
				)
			) {
				Icon(
					Icons.AutoMirrored.Default.KeyboardArrowLeft,
					contentDescription = "Cancel",
					tint = darkGrey,
					modifier = Modifier
						.height(100.dp)
						.width(45.dp)
				)
			}

			Spacer(Modifier.width(35.dp))

			// Title text "Nos communautés" styled with serif font and green color
			Text(
				text = "Nos communautés",
				fontSize = 20.sp,
				fontFamily = FontFamily.Serif,
				textAlign = TextAlign.Center,
				fontWeight = FontWeight.Bold,
				lineHeight = 50.sp,
				color = Color(0xFF3CB460),
				modifier = Modifier
					.height(50.dp)
					.background(color = grey)
			)
		}

		Spacer(modifier = Modifier.height(10.dp))

		// LazyColumn for efficient vertical scrolling of community cards
		Column {
			LazyColumn {
				items(communities) { community ->
					CommunityCard(
						modifier = Modifier.padding(horizontal = 5.dp, vertical = 0.dp),
						community = community,
						toCommunity = toCommunity,
						viewModel = communityViewModel
					)
				}
			}
		}
	}
}




/**
 * Card composable displaying community information and membership actions.
 *
 * Shows the community name, a truncated description (max 20 characters),
 * number of members (pluralized), and buttons to join/leave or access the community.
 *
 * The entire card is a non-navigable TextButton (disabled onClick) to allow
 * individual action buttons ("Rejoindre"/"Quitter" and "Accéder") to handle navigation and membership.
 *
 * @param modifier Modifier to customize layout and appearance of the card.
 * @param community The Community data object containing id, name, description, and members.
 * @param toCommunity Callback invoked when the "Accéder" button is clicked; navigates to community details.
 * @param viewModel ViewModel responsible for managing community membership state and actions.
 */
@Composable
fun CommunityCard(
	modifier: Modifier = Modifier,
	community: Community,
	toCommunity: (Community) -> Unit = {},
	viewModel: CommunityViewModel = viewModel()
) {
	val scope = rememberCoroutineScope()
	// Collect membership states for communities as a map of communityId -> Boolean
	val memberships = viewModel.isMember.collectAsState().value

	TextButton(
		onClick = {
			// Disable whole card click navigation; navigation only via buttons below
		},
		colors = ButtonDefaults.buttonColors(
			containerColor = Color(0x00FFFFFF), // Transparent background for card
			contentColor = MaterialTheme.colorScheme.onBackground
		),
	) {
		Row(
			modifier = modifier
				.fillMaxWidth()
				.height(95.dp)
				.border(width = 2.dp, color = lightGrey, shape = CutCornerShape(3.dp))
				.padding(5.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			// Left column with community name and truncated description
			Column(modifier = Modifier.weight(1f)) {
				Text(text = community.name, style = MaterialTheme.typography.titleMedium)
				Spacer(Modifier.height(4.dp))
				Text(
					text = if (community.description.length < 20) community.description
					else community.description.substring(0, 20) + "...",
					style = MaterialTheme.typography.bodySmall
				)
			}

			// Right column with member count and action buttons
			Column(
				horizontalAlignment = Alignment.End,
				verticalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.padding(start = 8.dp)
			) {
				// Display member count with correct pluralization
				val nbMembers = viewModel.allCommunities.collectAsState().value
					.find { it.id == community.id }?.members?.size ?: 0
				Text(
					text = "$nbMembers ${stringResource(R.string.community_members_label)}${if (nbMembers != 1) "s" else ""}",
					style = MaterialTheme.typography.bodySmall
				)

				Spacer(Modifier.height(6.dp))

				Row {
					// Button to join or leave community, color-coded by membership status
					TextButton(
						onClick = {
							scope.launch {
								if (memberships[community.id] == true) {
									Log.d("CommunityCard", "Request to leave community ${community.name}")
									viewModel.removeMember(community.id)
								} else {
									Log.d("CommunityCard", "Request to join community ${community.name}")
									viewModel.addMember(community.id)
								}
							}
						},
						shape = MaterialTheme.shapes.small,
						colors = ButtonDefaults.buttonColors(
							containerColor = if (memberships[community.id] == true) Color.Red else MaterialTheme.colorScheme.primary,
							contentColor = MaterialTheme.colorScheme.onPrimary
						)
					) {
						Text(
							text = if (memberships[community.id] == true)
								stringResource(R.string.join_community_button_joined)
							else
								stringResource(R.string.join_community_button),
							style = MaterialTheme.typography.labelSmall,
							fontSize = 15.sp
						)
					}

					// "Accéder" button to enter community, shown only if user is a member
					if (memberships[community.id] == true) {
						Spacer(Modifier.width(8.dp))
						TextButton(
							onClick = { toCommunity(community) },
							shape = MaterialTheme.shapes.small,
							colors = ButtonDefaults.buttonColors(
								containerColor = MaterialTheme.colorScheme.secondary,
								contentColor = MaterialTheme.colorScheme.onSecondary
							)
						) {
							Text(
								text = "Accéder",
								style = MaterialTheme.typography.labelSmall,
								fontSize = 15.sp
							)
						}
					}
				}
			}
		}
	}
}



@Preview(showBackground = true)
@Composable
fun BrowseCommunitiesPreview() {
	BrowseCommunities()
}
