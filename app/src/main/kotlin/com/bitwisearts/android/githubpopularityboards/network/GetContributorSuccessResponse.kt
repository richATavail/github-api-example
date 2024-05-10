package com.bitwisearts.android.githubpopularityboards.network

import kotlinx.serialization.Serializable

/**
 * The response to a [Get Contributors][GithubApi.getTopRepoContributor]
 * request.
 *
 * [See documentation](https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#list-repository-contributors)
 *
 * @property contributors
 *   The list of [ContributorAPIBody]s that represent the contributors to the
 *   repository.
 */
class GetContributorSuccessResponse(
	val contributors: List<ContributorAPIBody>
) {
	companion object {
		/**
		 * Attempts to create a [GetContributorSuccessResponse] from the given
		 * raw JSON string.
		 */
		fun from (raw: String): GetContributorSuccessResponse =
			GetContributorSuccessResponse(ContributorAPIBody.from(raw))
	}
}

/**
 * Represents a contributor to a GitHub repository.
 *
 * @property login
 *   The username of the contributor.
 * @property url
 *   The URL to the contributor's profile.
 * @property contributions
 *   The number of contributions the contributor has made to the repository.
 */
@Serializable
data class ContributorAPIBody(
	val login: String,
	val url: String,
	val contributions: Long
) {
	companion object {
		/**
		 * Attempts to create a list of [ContributorAPIBody]s from the given raw
		 * JSON string.
		 */
		fun from (raw: String): List<ContributorAPIBody> =
			try {
				decodeApiJson<List<ContributorAPIBody>>(raw)
			} catch (e: Exception) {
				emptyList()
			}
	}
}