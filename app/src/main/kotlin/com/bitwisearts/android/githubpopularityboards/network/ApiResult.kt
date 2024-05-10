package com.bitwisearts.android.githubpopularityboards.network

import io.ktor.client.statement.HttpResponse

/**
 * Abstract class that represents the different results of an API request.
 *
 * @param T
 *   The type of the expected successful response from the API request.
 */
sealed class ApiResult<T> {
	/**
	 * The [ApiResult] that represents an API request that has completed
	 * successfully.
	 *
	 * @param T
	 *   The type of the expected successful response from the API request.
	 * @param data
	 *   The Successful response from the API request.
	 */
	data class Success<T>(val data: T) : ApiResult<T>()

	/**
	 * The [ApiResult] that represents an API request that has failed.
	 *
	 * @param T
	 *   The type of the expected successful response from the API request.
	 * @param response
	 *   The response from the API request or `null` if the request did not
	 *   complete.
	 * @param error
	 *   The [Throwable] that caused the request to fail or `null` if there was
	 *   no caught exception.
	 */
	data class Failure<T>(
		val response: HttpResponse?,
		val responseBody: String?,
		val error: Throwable?
	) : ApiResult<T>() {
		/**
		 * The [FailureResponse] that represents the failure response from the
		 * API request [responseBody] or `null` if the response body is `null`.
		 */
		val failureResponse: FailureResponse? by lazy {
			responseBody?.let { FailureResponse.from(it) }
		}
	}
}