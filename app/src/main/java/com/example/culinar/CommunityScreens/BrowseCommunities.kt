package com.example.culinar.CommunityScreens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.ButtonColors
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culinar.models.Community
import com.example.culinar.models.viewModels.CommunityViewModel
import com.example.culinar.ui.theme.darkGrey
import com.example.culinar.ui.theme.grey
import com.example.culinar.ui.theme.lightGrey
import kotlinx.coroutines.launch


@Composable
fun BrowseCommunities(
	toCommunity: (Community) -> Unit = {},
	backToHome: () -> Unit = {},
	communityViewModel: CommunityViewModel = viewModel()
) {

	Log.d("CommunityScreen", "Composing BrowseCommunities")

	val communities = communityViewModel.allCommunities.collectAsState().value
	Column {
		// Screen title and options
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth()
				.height(80.dp)
				.background(color = grey)
		) {
			// Return button
			TextButton(
				onClick = { backToHome() },
				shape = CutCornerShape(3.dp),
				colors = ButtonColors(
					containerColor = Color(0x0059EA85),
					contentColor = Color.White,
					disabledContainerColor = Color(0xFF59EA85),
					disabledContentColor = Color.White
				)
			) {
				Icon(
					Icons.AutoMirrored.Default.KeyboardArrowLeft,
					contentDescription = "Cancel",
					tint = darkGrey,
					modifier = Modifier.height(100.dp).width(45.dp)
				)

			}

			Spacer(Modifier.width(35.dp))

			// Title of the subscreen
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
		Column {
			LazyColumn {

				items (communities) { community ->

					CommunityCard(
						modifier = Modifier.padding(
							horizontal = 5.dp,
							vertical = 0.dp
						),
						community = community,
						toCommunity = toCommunity,
						viewModel = communityViewModel
					)
				}
			}
		}
	}
}



@Composable
fun CommunityCard(modifier: Modifier = Modifier, community: Community, toCommunity: (Community) -> Unit = {}, viewModel: CommunityViewModel = viewModel()) {

	val scope = rememberCoroutineScope()
	val memberships = viewModel.isMember.collectAsState().value

	//Log.d("CommunityCard", "isMemberState: ${memberships[community.id]}")

	TextButton(
		onClick = {
			if (memberships[community.id] == true)
				toCommunity(community)
		},
		colors = ButtonDefaults.buttonColors(
			containerColor = Color.White,
			contentColor = Color.Black
		),

	) {
		Row (
			modifier = modifier
				.fillMaxWidth()
				.fillMaxWidth()
				.height(95.dp)
				.border(width=2.dp, color = lightGrey, shape = CutCornerShape(3.dp))
		) {
			Column(modifier = Modifier.padding(5.dp)) {
				// Name of the community
				Text(text = community.name, style = MaterialTheme.typography.titleMedium)
				Spacer(Modifier.weight(1f))
				// Description of the community
				Text(text = community.description, style = MaterialTheme.typography.bodySmall)

			}
			Spacer(Modifier.weight(1f))
			Column(modifier = Modifier.padding(5.dp), horizontalAlignment = Alignment.End) {
				// Number of members
				val nbMembers = viewModel.allCommunities.collectAsState().value.find { it.id == community.id }?.members?.size ?: 0
				Text(text = " $nbMembers membre" + if(nbMembers != 1) "s" else "", style = MaterialTheme.typography.bodySmall)

				Spacer(Modifier.weight(1f))
				// Join button
				TextButton(
					onClick = {
						if (memberships[community.id] == false) {
							Log.d("CommunityCard", "Request to join community ${community.name}")
							scope.launch {
								viewModel.addMember(community.id)
							}

							if (memberships[community.id] == true) {
								Log.d(
									"CommunityCard",
									"Joined community ${community.name}"
								)
							}
						} else {
							Log.d("CommunityCard", "Request to leave community ${community.name}")
							scope.launch {
								viewModel.removeMember(community.id)
							}
							if (memberships[community.id] == false) {
								Log.d(
									"CommunityCard",
									"Left community ${community.name}"
								)
							}
						}
							  },
					shape = MaterialTheme.shapes.small,
					colors = ButtonDefaults.buttonColors(
						containerColor = if (memberships[community.id] == true) Color.Red else MaterialTheme.colorScheme.primary,
						contentColor = MaterialTheme.colorScheme.onPrimary
					)
				)
				{
					Text(
						text = if(memberships[community.id] == true) "Quitter" else "Rejoindre",
						style = MaterialTheme.typography.labelSmall,
						fontSize = 15.sp
					)
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