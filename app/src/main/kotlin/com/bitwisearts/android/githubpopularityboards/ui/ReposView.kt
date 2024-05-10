package com.bitwisearts.android.githubpopularityboards.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bitwisearts.android.githubpopularityboards.BuildConfig
import com.bitwisearts.android.githubpopularityboards.R
import com.bitwisearts.android.githubpopularityboards.data.GitHubRepo
import com.bitwisearts.android.githubpopularityboards.network.ApiResult
import com.bitwisearts.android.githubpopularityboards.network.GitHubRepoApiBody
import com.bitwisearts.android.githubpopularityboards.network.GithubApi
import com.bitwisearts.android.githubpopularityboards.ui.theme.GithubPopularityBoardsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

/**
 * The [ViewModel] used to manage the state for the [ReposView].
 */
class ReposViewModel: ViewModel() {
	/**
	 * The state that indicates whether there is an active API request in
	 * progress to retrieve data from the Github server.
	 */
	val isRequesting = mutableStateOf(false)

	/**
	 * The [ApiResult] that indicates the current state of the request to
	 * the Github API.
	 */
	val repos = mutableStateOf<List<GitHubRepo>>(emptyList())

	/**
	 * The [ApiResult.Failure] that indicates the reason for the failure.
	 */
	val failure = mutableStateOf<ApiResult.Failure<*>?>(null)

	/**
	 * The [GithubApi] used to retrieve data from the Github server.
	 *
	 * Note: In a production app, this would be managed elsewhere; it is managed
	 * here for simplicity.
	 */
	private val api: GithubApi = GithubApi()

	/**
	 * Indicates whether the request to the Github API should include the
	 * security token. This is toggled when a request is made and the response
	 * indicates that the token is not authorized.
	 */
	private var useSecurity: Boolean = BuildConfig.GITHUB_API_TOKEN.isNotBlank()

	/**
	 * The number of pages to retrieve from the Github API.
	 */
	val resultPageCount = mutableIntStateOf(10)

	/**
	 * Retrieves repositories that are popular on GitHub.
	 */
	fun retrievePopularRepos() {
		failure.value = null
		repos.value = emptyList()
		viewModelScope.launch(Dispatchers.IO) {
			isRequesting.value = true
			when (val repoRsp = api.getRepos(resultPageCount.intValue, useSecurity)) {
				is ApiResult.Failure -> {
					if (repoRsp.response?.status?.value == 401) {
						useSecurity = false
					}
					failure.value = repoRsp
				}
				is ApiResult.Success -> {
					val myRepos: List<GitHubRepo> =
						repoRsp.data.items.map { repoApi ->
							async { gitRepo(repoApi) }
						}.awaitAll().filterNotNull()
					repos.value = myRepos
				}
			}
			isRequesting.value = false

		}
	}

	/**
	 * Answers the top contributor to the given repository or `null` if the
	 * network request fails.
	 */
	private suspend fun gitRepo(repoApi: GitHubRepoApiBody): GitHubRepo? =
		api.getTopRepoContributor(repoApi, useSecurity).let { result ->
			when (result) {
				is ApiResult.Failure -> {
					when(result.response?.status?.value) {
						401 -> useSecurity = false
						403 -> {
							// Rate limit exceeded
							failure.value = result
						}
					}
					null
				}
				is ApiResult.Success -> {
					val contributor = result.data.contributors.firstOrNull()
					GitHubRepo.from(repoApi, contributor)
				}
			}
		}
}

/**
 * The view that displays the list of repositories that are popular on GitHub.
 *
 * @param viewModel
 *   The [ReposViewModel] that manages the state for this view.
 */
@Composable
fun ReposView(
	viewModel: ReposViewModel = viewModel()
) {
	LaunchedEffect(Unit) {
		// We immediately want to retrieve the popular repositories when the
		// view is first created.
		viewModel.retrievePopularRepos()
	}
	Column(
		modifier = Modifier.padding(16.dp).fillMaxSize()
	) {
		val isRequesting by remember {
			viewModel.isRequesting
		}
		Row(
			modifier = Modifier.clickable {
				if(!isRequesting) {
					// We only want to retrieve the popular repositories if
					// there is no active request in progress.
					viewModel.retrievePopularRepos()
				}
			},
			verticalAlignment = Alignment.CenterVertically,
		){
			Text(
				text = stringResource(id = R.string.repos_title),
				style = MaterialTheme.typography.titleLarge,
				color = MaterialTheme.colorScheme.onSurface,
				modifier = Modifier.padding(vertical = 20.dp)
			)

			Box(
				modifier = Modifier
					.weight(1f)
					.padding(end = 15.dp),
				contentAlignment = Alignment.CenterEnd,
			) {
				val infiniteTransition =
					rememberInfiniteTransition(label = "infinite")
				val rotate by infiniteTransition.animateFloat(
					initialValue = 0F,
					targetValue = if(isRequesting) 360F else 0F,
					animationSpec = infiniteRepeatable(
						animation =
							tween(1000, easing = LinearEasing)
					),
					label = "rotation"
				)
				Icon(
					imageVector = Icons.Default.Refresh,
					contentDescription =
						stringResource(id = R.string.refresh_icon),
					tint = MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier
						.size(30.dp)
						.graphicsLayer {
							rotationZ = rotate
						}
				)
			}
		}
		RadioButtonOptions(viewModel)
		Spacer(modifier = Modifier.height(8.dp))
		val failureState by remember {
			viewModel.failure
		}
		val repos by remember {
			viewModel.repos
		}
		failureState?.let { failure ->
			Row {
				Text(
					text = stringResource(id = R.string.error),
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.error,
					modifier = Modifier.padding(vertical = 8.dp)
				)
			}
			if(failure.response != null) {
				Row {
					Text(
						text = failure.response.status.toString(),
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.error,
						modifier = Modifier.padding(vertical = 8.dp)
					)
				}
			}
			failure.failureResponse?.let {
				Text(
					text = it.message,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.error,
					modifier = Modifier.padding(vertical = 8.dp)
				)
			}
		} ?: LazyColumn {
			items(repos) { repo ->
				RepoCard(repo = repo)
			}
		}
	}
}

/**
 * The view that displays a card for a repository that is popular on GitHub.
 *
 * @param repo
 *   The [GitHubRepo] that contains the information to display in the card.
 */
@Composable
private fun RepoCard(repo: GitHubRepo)
{
	val context = LocalContext.current
	Card(
		modifier = Modifier.padding(8.dp)
			.clickable {
				context.startActivity(
					Intent(Intent.ACTION_VIEW, Uri.parse(repo.htmlUrl))
				)
			}
			.border(
				width = 1.dp,
				color = MaterialTheme.colorScheme.outlineVariant,
				shape = RoundedCornerShape(size = 20.dp)
			)
	) {
		Column(
			modifier = Modifier.padding(16.dp)
		) {
			Text(
				text = repo.name,
				style = MaterialTheme.typography.headlineSmall,
				overflow = TextOverflow.Ellipsis,
				maxLines = 1
			)
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.End
			) {
				Text(
					text = "${repo.starsCount}",
					style = MaterialTheme.typography.titleMedium
				)
				Spacer(modifier = Modifier.width(4.dp))
				val isDarkTheme = isSystemInDarkTheme()
				Icon(
					imageVector = Icons.Default.Star,
					contentDescription =
						stringResource(id = R.string.star_icon),
					tint = if(isDarkTheme) Color.Yellow else Color.Blue,
				)
			}
			Spacer(modifier = Modifier.height(8.dp))
			if(repo.description != null) {
				Text(
					text = repo.description,
					style = MaterialTheme.typography.bodySmall,
					overflow = TextOverflow.Ellipsis,
					maxLines = 2
				)
				Spacer(modifier = Modifier.height(8.dp))
			}
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.End
			) {
				Text(
					text = stringResource(id = R.string.top_contributors_title),
					style = MaterialTheme.typography.bodySmall,
					fontWeight = FontWeight.Bold
				)
				Spacer(modifier = Modifier.width(4.dp))
				Text(
					text = repo.contributorLogin,
					style = MaterialTheme.typography.bodySmall
				)

			}
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.End
			) {
				Text(
					text = repo.contributions.toString(),
					style = MaterialTheme.typography.bodySmall
				)
				Spacer(modifier = Modifier.width(4.dp))
				Text(
					text = stringResource(id = R.string.contribution_count),
					style = MaterialTheme.typography.bodySmall
				)
			}
		}
	}
}

/**
 * The view that displays the radio button options for the number of results
 * to display per page. This was added as the provided GitHub API key was not
 * valid causing this to be developed as a workaround to allow the user to
 * toggle the number of results to display per page to avoid the rate limit
 * of the GitHub API.
 *
 * @param viewModel
 *   The [ReposViewModel] that manages the state for this view.
 */
@Composable
private fun RadioButtonOptions(viewModel: ReposViewModel) {
	val options = listOf(2, 10, 100)
	val selectedOption by remember { viewModel.resultPageCount }

	Row {
		Text(
			text = stringResource(id = R.string.results_per_page),
			style = MaterialTheme.typography.bodyMedium,
			modifier = Modifier.padding(top = 4.dp)
		)

	}
	Row(
		horizontalArrangement = Arrangement.SpaceEvenly
	) {
		options.forEach { option ->
			Row(
				Modifier
					.clickable { viewModel.resultPageCount.intValue = option }
					.padding(16.dp)
					.clickable { viewModel.resultPageCount.intValue = option },
				verticalAlignment = Alignment.CenterVertically
			) {
				RadioButton(
					selected = selectedOption == option,
					onClick = { viewModel.resultPageCount.intValue = option }
				)
				Text(
					text = option.toString(),
					style = MaterialTheme.typography.bodyMedium,
					modifier = Modifier.padding(start = 8.dp)
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun RepoCardPreview() {
	GithubPopularityBoardsTheme {
		RepoCard(
			repo = GitHubRepo(
				id = 1,
				name = "Repo Name",
				ownerName = "Owner Name",
				ownerUrl = "www.github.com",
				fullName = "My Repo",
				description = "This is a description",
				contributorsUrl = "www.github.com/contributors",
				starsCount = 100,
				htmlUrl = "www.github.com/repo",
				language = "Kotlin",
				contributorLogin = "Contributor",
				contributorUrl = "www.github.com/contributor",
				contributions = 100
			)
		)
	}
}