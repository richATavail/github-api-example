package com.bitwisearts.android.githubpopularityboards.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bitwisearts.android.githubpopularityboards.R

/**
 * The start view of the app. This is the first view that is shown when the app
 * is launched.
 *
 * @param navController
 *   The navigation controller that is used to navigate between screens.
 */
@Composable
fun StartView(
	navController: NavController
) {
	val context = LocalContext.current
	BackHandler {
		// We don't want anything to happen, so we disable back button to
		// prevent app from minimizing or closing.
		Toast.makeText(
			context,
			context.getText(R.string.back_button_disabled),
			Toast.LENGTH_SHORT
		).show()
	}

	Column(
		modifier = Modifier.padding(16.dp).fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Row(
			Modifier.padding(bottom = 16.dp),
		) {
			Text(
				text = stringResource(id = R.string.app_name),
				style = MaterialTheme.typography.titleLarge
			)
		}

		Row {
			Button(
				onClick = {
					navController.navigate(NavScreens.REPOS.route)
				}
			) {
				Text(text = stringResource(id = R.string.start))
			}
		}
	}
}