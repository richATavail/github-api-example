package com.bitwisearts.android.githubpopularityboards.network

import kotlinx.serialization.SerialName

/**
 * A response that represents a failure in a [GithubApi] request.
 *
 * **NOTE:** This payload was derived through trial and error, not from the
 * Github API documentation.
 *
 * @property message
 *   The message that describes the failure.
 * @property documentationUrl
 *   The URL to the documentation that describes the failure.
 */
data class FailureResponse(
	val message: String,
	@SerialName("documentation_url")
	val documentationUrl: String
) {
	companion object {
		/**
		 * Attempts to create a [FailureResponse] from the given raw JSON string.
		 *
		 * @param raw
		 *   The raw JSON string to attempt to parse.
		 * @return
		 *   The [FailureResponse] if the parsing was successful; `null`
		 *   otherwise.
		 */
		fun from (raw: String): FailureResponse? =
			try {
				decodeApiJson(raw)
			} catch (e: Exception) {
				null
			}
	}
}