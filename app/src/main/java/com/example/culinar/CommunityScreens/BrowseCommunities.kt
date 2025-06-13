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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.culinar.models.Community
import com.example.culinar.models.Screen
import com.example.culinar.models.viewModels.CommunityViewModel
import com.example.culinar.ui.theme.darkGrey
import com.example.culinar.ui.theme.grey
import com.example.culinar.ui.theme.lightGrey
import kotlinx.coroutines.runBlocking


@Composable
fun BrowseCommunities(
	toCommunity: (Community) -> Unit = {},
	backToHome: () -> Unit = {},
	viewModel: CommunityViewModel = viewModel()
) {

	val communities = viewModel.allCommunities.collectAsState().value
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

		for (community in communities) {
			//val isMember = remember { mutableStateOf(false) }
			CommunityCard(
				modifier = Modifier.padding(
					top = 20.dp,
					bottom = 10.dp,
					start = 10.dp,
					end = 10.dp
				),
				community = community,
				toCommunity = toCommunity,
				viewModel = viewModel
			)
		}
	}
}



@Composable
fun CommunityCard(modifier: Modifier = Modifier, community: Community, toCommunity: (Community) -> Unit = {}, viewModel: CommunityViewModel = viewModel()) {

	var isMemberState by remember { mutableStateOf(false) }
	runBlocking {
		isMemberState = viewModel.checkMembership(community.id)
	}
	Log.d("CommunityCard", "isMemberState: $isMemberState")

	TextButton(
		onClick = {
			if (isMemberState)
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
				.height(80.dp)
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
			Column(modifier = Modifier.padding(5.dp)) {
				// Number of members
				val nbMembers = community.members?.size ?: 0
				Text(text = " $nbMembers membres", style = MaterialTheme.typography.bodySmall)

				Spacer(Modifier.weight(1f))
				// Join button
				TextButton(
					onClick = {
						if (!isMemberState) {
							Log.d("CommunityCard", "Request to join community ${community.name}")
							runBlocking {
								isMemberState = viewModel.addMember(community.id)
							}

							if (isMemberState) {
								Log.d(
									"CommunityCard",
									"Joined community ${community.name}"
								)
							}
						}
							  },
					shape = MaterialTheme.shapes.small,
					colors = ButtonDefaults.buttonColors(
						containerColor = if (isMemberState) Color.Gray else MaterialTheme.colorScheme.primary,
						contentColor = MaterialTheme.colorScheme.onPrimary
					)
				)
				{
					Text(text = if(isMemberState) "Déjà membre" else "Rejoindre", style = MaterialTheme.typography.labelSmall)
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