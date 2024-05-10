package com.bitwisearts.android.githubpopularityboards.ui

/**
 * A basic enumeration of all the screens in the app that can be navigated to.
 *
 * @property route
 *   The route that the screen is associated with.
 */
enum class NavScreens(val route: String) {
	/** The start screen of the app. */
	START("start"),

	/**
	 * The screen that displays the list of repositories that are popular on
	 * GitHub.
	 */
	REPOS("repos")
}