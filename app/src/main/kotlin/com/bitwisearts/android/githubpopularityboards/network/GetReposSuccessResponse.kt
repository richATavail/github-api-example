package com.bitwisearts.android.githubpopularityboards.network

import com.bitwisearts.android.githubpopularityboards.data.GitHubRepo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The response to a [Get Repos][GithubApi.getRepos] request.
 *
 * [See documentation](https://docs.github.com/en/rest/search/search?apiVersion=2022-11-28#search-repositories)
 *
 * @property items
 *   The list of [GitHubRepo]s that match the search criteria.
 */
@Serializable
data class GetReposSuccessResponse(
	val items: List<GitHubRepoApiBody>
) {
	companion object {
		/**
		 * Attempts to create a [GetReposSuccessResponse] from the given raw
		 * JSON string.
		 */
		fun from(raw: String): GetReposSuccessResponse =
			decodeApiJson(raw)
	}
}

/**
 * Represents a GitHub repository.
 *
 * @author Richard Arriaga
 *
 * @property id
 *   The unique identifier of the repository in the Github database.
 * @property name
 *   The name of the repository.
 * @property owner
 *   The [Owner] of the repository or `null` if the owner is not provided.
 * @property fullName
 *   The full name of the repository.
 * @property description
 *   The description of the repository.
 * @property contributorsUrl
 *   The URL to the contributors of the repository.
 * @property stargazersCount
 *   The number of stars the repository has.
 * @property htmlUrl
 *   The URL to the repository on GitHub.
 * @property language
 *   The primary programming language the repository is written in or `null`
 *   if the language is not provided.
 */
@Serializable
data class GitHubRepoApiBody(
	val id: Int,
	val name: String,
	val owner: Owner?,
	@SerialName("full_name")
	val fullName: String,
	val description: String?,
	@SerialName("contributors_url")
	val contributorsUrl: String,
	@SerialName("stargazers_count")
	val stargazersCount: Int,
	@SerialName("html_url")
	val htmlUrl: String,
	val language: String?
)

/**
 * Represents the owner of a GitHub repository.
 *
 * @property id
 *   The unique identifier of the owner in the Github database.
 * @property login
 *   The effective name of the owner.
 * @property htmlUrl
 *   The URL to the owner's profile on GitHub.
 */
@Serializable
data class Owner(
	val id: Int,
	val login: String,
	@SerialName("html_url")
	val htmlUrl: String
)