package com.bitwisearts.android.githubpopularityboards

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bitwisearts.android.githubpopularityboards.ui.NavScreens
import com.bitwisearts.android.githubpopularityboards.ui.ReposView
import com.bitwisearts.android.githubpopularityboards.ui.StartView
import com.bitwisearts.android.githubpopularityboards.ui.theme.GithubPopularityBoardsTheme

/**
 * The single activity that hosts the entire app.
 */
class MainActivity : ComponentActivity() {
	@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
	@SuppressLint("SourceLockedOrientationActivity")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		enableEdgeToEdge()
		setContent {
			DisposableEffect(Unit) {
				// Disable landscape orientation. We do this as this is a simple
				// exercise to demonstrate a very simple app. Orientation change
				// triggers a reconfiguration in Compose. Allowing it for this
				// app would require extra support for managing state on
				// reconfiguration. In the interest of completing the work in
				// the suggested time range, we prevent this to reduce the
				// volume of work to fit in the time allotted.
				val originalOrientation = requestedOrientation
				requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
				onDispose {
					requestedOrientation = originalOrientation
				}
			}
			GithubPopularityBoardsTheme {
				// This is a simple example of how to get the window size class
				// in your app. WindowSizeClass.widthSizeClass would be used to
				// determine the likely device size/configuration defined in
				// WindowWidthSizeClass companion object. This is not used for
				// the exercise, but is included here to demonstrate what API
				// would be used to dispatch to different layouts based on the
				// window size.
				@Suppress("UNUSED_VARIABLE")
				val windowSize: WindowSizeClass = calculateWindowSizeClass(this)

				val navController = rememberNavController()
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					NavHost(
						modifier = Modifier.padding(innerPadding),
						navController = navController,
						startDestination = NavScreens.START.route,
						enterTransition = {
							fadeIn(animationSpec = tween(FADE_IN_DURATION))
						},
						exitTransition = {
							fadeOut(animationSpec = tween(FADE_IN_DURATION))
						}
					) {
						composable(NavScreens.START.route) {
							StartView(
								navController = navController
							)
						}
						composable(NavScreens.REPOS.route) {
							ReposView()
						}
					}
				}
			}
		}
	}

	companion object {
		/**
		 * The duration in milliseconds of the fade in animation.
		 */
		private const val FADE_IN_DURATION = 300
	}
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
	Text(
		text = "Hello $name!",
		modifier = modifier
	)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
	GithubPopularityBoardsTheme {
		Greeting("Android")
	}
}