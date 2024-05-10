package com.bitwisearts.android.githubpopularityboards.network

import android.util.Log
import com.bitwisearts.android.githubpopularityboards.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders

/**
 * The Github API client that manages requests to the Github API.
 *
 * @author Richard Arriaga
 */
class GithubApi {
	/**
	 * The [HttpClient] used to make requests to the Github API.
	 */
	private val client = HttpClient(Android)

	/**
	 * Adds the necessary headers for making requests to the Github API.
	 *
	 * Taken from
	 * [cURL example](https://docs.github.com/en/rest/search/search?apiVersion=2022-11-28#search-repositories)
	 *
	 * @param withSecurity
	 *   Indicates whether the request should include the Github API token.
	 */
	private fun HttpRequestBuilder.githubHeaders(
		withSecurity: Boolean
	) {
		headers {
			append(HttpHeaders.Accept, "application/vnd.github+json")
			if(withSecurity && BuildConfig.GITHUB_API_TOKEN.isNotBlank())
				bearerAuth(BuildConfig.GITHUB_API_TOKEN)
			append("X-GitHub-Api-Version", "2022-11-28")
		}
	}

	/**
	 * Retrieves the top 100 starred repositories on Github.
	 *
	 * [See documentation](https://docs.github.com/en/rest/search/search?apiVersion=2022-11-28#search-repositories)
	 *
	 * @param page
	 *   The number of results to return per page.
	 * @param withSecurity
	 *   Indicates whether the request should include the Github API token.
	 * @return
	 *   The [ApiResult] of the [GetReposSuccessResponse] that represents the
	 *   response to the request.
	 */
	suspend fun getRepos(
		page: Int = 100,
		withSecurity: Boolean = false
	): ApiResult<GetReposSuccessResponse> =
		try {
			val response = client.get(
				"https://api.github.com/search/repositories?q=stars:%3E1&sort=stars&order=desc&per_page=$page"
			) {
				githubHeaders(withSecurity)
			}
			when (response.status.value) {
				200 -> {
					val body = response.bodyAsText()
					Log.d("GetRepos", body)
					ApiResult.Success(GetReposSuccessResponse.from(body))
				}
				else -> {
					Log.e("GetRepos", response.status.toString())
					val bodyAsText = try {
						response.bodyAsText().apply {
							Log.e("GetRepos", this)
						}
					} catch (e: Exception) {
						Log.e("GetRepos", "Failed to get failure body")
						null
					}
					ApiResult.Failure(response, bodyAsText, null)
				}
			}
		} catch (e: Exception) {
			Log.e("GetRepos", "Failed to get repos: ${e.message}", e)
			ApiResult.Failure(null, null, e)
		}

	/**
	 * Retrieves the top contributor to a repository.
	 *
	 * [See documentation](https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#list-repository-contributors)
	 *
	 * @param repoApiBody
	 *   The [GitHubRepoApiBody] that contains the information needed to
	 *   retrieve the top contributor to the repository.
	 * @param withSecurity
	 *   Indicates whether the request should include the Github API token.
	 * @return
	 *   The [ApiResult] of the [GetContributorSuccessResponse] that represents
	 *   the response to the request.
	 */
	suspend fun getTopRepoContributor(
		repoApiBody: GitHubRepoApiBody,
		withSecurity: Boolean = false
	): ApiResult<GetContributorSuccessResponse> =
		try {
			val response =
				client.get("${repoApiBody.contributorsUrl}?per_page=1") {
				githubHeaders(withSecurity)
			}
			when (response.status.value) {
				200 -> {
					val body = response.bodyAsText()
					Log.d(
						"GetContributor",
						"${repoApiBody.contributorsUrl}\n$body")
					ApiResult.Success(GetContributorSuccessResponse.from(body))
				}
				else -> {
					Log.e(
						"GetContributor",
						"${repoApiBody.contributorsUrl}: ${response.status}")
					val bodyAsText = try {
						response.bodyAsText().apply {
							Log.e("GetContributor", this)
						}
					} catch (e: Exception) {
						Log.e(
							"GetContributor",
							"Failed to get failure body")
						null
					}
					ApiResult.Failure(response, bodyAsText, null)
				}
			}
		} catch (e: Exception) {
			Log.e(
				"GetContributor",
				"Failed to get contributor, ${repoApiBody.contributorsUrl}," +
					" ${e.message}",
				e)
			ApiResult.Failure(null, null, e)
		}
}