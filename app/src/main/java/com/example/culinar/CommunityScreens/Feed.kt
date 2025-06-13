package com.example.culinar.CommunityScreens

import android.widget.Toolbar
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culinar.models.Community
import com.example.culinar.models.viewModels.CommunityViewModel


@Composable
fun Feed(communityViewModel: CommunityViewModel = viewModel()) {

	val selectedCommunity = communityViewModel.selectedCommunity

	Box() {
		ToolBar()
		if (selectedCommunity != null) {
			Text(text = "Nothing to show here!")
		} else {
			PostFeed()
		}
	}
}


@Composable
fun ToolBar(communityViewModel: CommunityViewModel = viewModel()) {

}


@Composable
fun PostFeed(communityViewModel: CommunityViewModel = viewModel()) {

}


@Composable
fun Post() {

}


@Preview(showBackground = true)
@Composable
fun FeedPreview() {
	Feed()
}