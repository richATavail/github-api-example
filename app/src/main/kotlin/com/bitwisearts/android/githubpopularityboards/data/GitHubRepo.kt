package com.bitwisearts.android.githubpopularityboards.data

import com.bitwisearts.android.githubpopularityboards.network.ContributorAPIBody
import com.bitwisearts.android.githubpopularityboards.network.GitHubRepoApiBody

/**
 * Represents a GitHub repository.
 *
 * @author Richard Arriaga
 *
 * @property id
 *   The unique identifier of the repository in the Github database.
 * @property name
 *   The name of the repository.
 * @property ownerName
 *   The effective name of the repository owner be it user login or organization
 *   name or `null` if the owner is not provided.
 * @property ownerUrl
 *   The URL to the owner's profile on GitHub or `null` if the owner is not
 *   provided.
 * @property fullName
 *   The full name of the repository.
 * @property description
 *   The description of the repository.
 * @property contributorsUrl
 *   The URL to the contributors of the repository.
 * @property starsCount
 *   The number of stars the repository has.
 * @property htmlUrl
 *   The URL to the repository on GitHub.
 * @property language
 *   The primary programming language the repository is written in or `null` if
 *    the language is not provided.
 * @property contributorLogin
 *   The username of the top repo contributor.
 * @property contributorUrl
 *   The URL to the top repo contributor's profile.
 * @property contributions
 *   The number of contributions the top repo contributor has made to the
 *   repository.
 */
data class GitHubRepo(
    val id: Int,
    val name: String,
    val ownerName: String?,
    val ownerUrl: String?,
    val fullName: String,
    val description: String?,
    val contributorsUrl: String,
    val starsCount: Int,
    val htmlUrl: String,
    val language: String?,
    val contributorLogin: String,
    val contributorUrl: String,
    val contributions: Long
) {
    companion object {
        /**
         * Attempts to create a [GitHubRepo] from the given [GitHubRepoApiBody]
         * and [ContributorAPIBody].
         */
        fun from(
            gitHubRepoApiBody: GitHubRepoApiBody,
            contributor: ContributorAPIBody?
        ): GitHubRepo =
            GitHubRepo(
                gitHubRepoApiBody.id,
                gitHubRepoApiBody.name,
                gitHubRepoApiBody.owner?.login,
                gitHubRepoApiBody.owner?.htmlUrl,
                gitHubRepoApiBody.fullName,
                gitHubRepoApiBody.description,
                gitHubRepoApiBody.contributorsUrl,
                gitHubRepoApiBody.stargazersCount,
                gitHubRepoApiBody.htmlUrl,
                gitHubRepoApiBody.language,
                contributor?.login ?: "Unavailable",
                contributor?.url ?: "Unavailable",
                contributor?.contributions ?: 0
            )
    }
}