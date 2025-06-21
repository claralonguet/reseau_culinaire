package com.example.culinar.CommunityScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culinar.models.Post
import com.example.culinar.models.Recipe
import com.example.culinar.models.viewModels.CommunityViewModel


/*
	Displays options:
	- Post a standard post (name, content, image)
	- Post a recipe (name, ingredients, steps, image, ...)
 */
@Composable
fun CreatePost(
	communityViewModel: CommunityViewModel,
	createPost : (Post) -> Unit,
	createRecipe : (Recipe) -> Unit,
	goBack : () -> Unit
) {

	var screenOn by rememberSaveable { mutableIntStateOf(0) }

	ToolBar(goBack = { goBack() }, community = communityViewModel.selectedCommunity)
	Column (
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier.fillMaxSize()
		) {

		when(screenOn) {
			0 -> TypeOfPost(
				changeScreenOn = { screenOn = it }
			)
			1 -> StandardPost(
				communityViewModel = communityViewModel,
				createPost = createPost,
				goBack = goBack
			)
			2 -> RecipePost(
				communityViewModel = communityViewModel,
				createRecipe = createRecipe,
				goBack = goBack
			)
			else -> TypeOfPost(
				changeScreenOn = { screenOn = it }
			)
		}
	}
}


@Composable
fun TypeOfPost(
	changeScreenOn : (Int) -> Unit
) {

	var selectedOption by rememberSaveable { mutableIntStateOf(1) }

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.fillMaxWidth(0.9f)
			.fillMaxHeight(0.5f)
			.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f))
			.border(width = 2.dp, color = MaterialTheme.colorScheme.onPrimary, shape = MaterialTheme.shapes.medium)
			.padding(10.dp)
	)
	{
		Text(
			text = "What do you want to post?",
			style = MaterialTheme.typography.titleMedium,
			color = MaterialTheme.colorScheme.onPrimary,
			textAlign = TextAlign.Center,
			modifier = Modifier.padding(10.dp)
		)
		Spacer(modifier = Modifier.height(40.dp))

		Column {
			// Radio button for standard posts
			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				RadioButton(
					selected = selectedOption == 1,
					onClick = { selectedOption = 1 },
					colors = RadioButtonDefaults.colors(
						selectedColor = MaterialTheme.colorScheme.onPrimary,
						unselectedColor = MaterialTheme.colorScheme.onPrimary
					)
				)
				Column {
					Text(
						text = "Standard post",
						color = MaterialTheme.colorScheme.onPrimary,
					)
					Text(
						text = "Name, content, image",
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onPrimary,
					)
				}
			}

			Spacer(modifier = Modifier.height(20.dp))

			// Radio button for recipes
			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				RadioButton(
					selected = selectedOption == 2,
					onClick = { selectedOption = 2 },
					colors = RadioButtonDefaults.colors(
						selectedColor = MaterialTheme.colorScheme.onPrimary,
						unselectedColor = MaterialTheme.colorScheme.onPrimary
					)
				)
				Column {
					Text(
						text = "Recipe",
						color = MaterialTheme.colorScheme.onPrimary,
					)
					Text(
						text = "Name, ingredients, steps, image",
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onPrimary,
					)
				}
			}
		}

		Spacer(modifier = Modifier.height(40.dp))

		TextButton(
			onClick = {
				if(selectedOption == 1)
					changeScreenOn(1)
				else
					changeScreenOn(2)
			},
			shape = MaterialTheme.shapes.medium,
			modifier = Modifier.height(50.dp),
			colors = ButtonColors(
				containerColor = MaterialTheme.colorScheme.onPrimary,
				contentColor = MaterialTheme.colorScheme.primary,
				disabledContainerColor = MaterialTheme.colorScheme.primary,
				disabledContentColor = MaterialTheme.colorScheme.onPrimary
			)
		) {
			Text(
				text = "Continue",
				style = MaterialTheme.typography.labelSmall
			)
		}

	}
}




/*
	Creates a standard post.

 */
@Composable
fun StandardPost(
	communityViewModel: CommunityViewModel,
	createPost : (Post) -> Unit,
	goBack : () -> Unit
) {

}



/*
	Creates a standard post.

 */
@Composable
fun RecipePost(
	communityViewModel: CommunityViewModel,
	createRecipe : (Recipe) -> Unit,
	goBack : () -> Unit
) {

}





@Composable
@Preview(showBackground = true)
fun CreatePostPreview() {
	CreatePost(viewModel(), {}, {}, {})
}