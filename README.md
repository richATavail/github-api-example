Github Popularity Boards
--------------------------------------------------------------------------------
This is a simple Android app that displays up to the 100 most starred Github 
repositories. This app uses the Github REST API to fetch the repositories and
their top contributors.

This app is an assignment described by the following instructions:

> Build an Android app that uses the GitHub REST API to display a list of the 100 
> most starred Github repositories. For each of the repositories, also display its 
> top contributor. To get the list of repositories, you can use the following 
> request:
> 
> GET https://api.github.com/search/repositories?q=stars:>0
>
> To get the list of contributors for a given repository, you can use the 
> following request:
>
> GET https://api.github.com/repos/{owner}/{repo}/contributors

## About
The app is built using Jetpack Compose and its Material 3 components supporting
Android SDK 31 and up (Android 12). It also uses a very light-weight Ktor client 
implementation. Given this is a simple app, the following features were omitted:

- **Session Data Cache**: The app does not cache the data fetched from the Github 
  API. This means that the app will fetch the data every time it is opened or 
  the data is refreshed. Hence there is no usage of Repositories, Room, or any 
  other Android data storage mechanism.
- **Handle Different Screen Sizes**: The app is designed for a single screen size 
  and does not handle different screen sizes.
- **Data Persistence on Configuration Changes**: The app does not persist the 
  data when the configuration changes (e.g., screen rotation). This means that 
  the app will fetch the data again when the configuration changes. 
  _Note: screen rotation is disabled to simplify development_
- **Theme Customization**: The app uses the default Material 3 theme and does not 
  provide any customization options.
- **Error Handling**: The app does not have extensive error handling which may
  result in crashes in exceptional situations.
- **Testing**: The app does not have any tests as that would have taken more time
  to implement than the assignment allows.

Due to the time restraints on the assignment no extensive work was done to 
create a well-polished Jetpack Compose app. The app was built to be functional
using very basic Jetpack Compose components to simply get the data on screen
without much consideration for UI/UX. For example, the app does not have any
clear indication on how to refresh the data, which is done by tapping the top
app bar on the "Github Popular Repositories" title on the second screen.

Additionally, the app was started using the `Basic Compose Activity` template
provided by Android Studio. This template is a very basic Jetpack Compose app
that provides a starting point for a Compose app. It requires additional setup
and configuration to be a fully functional app, specifically in the area of
Gradle configuration. The work in this area was done quickly to get the app
running and may not be optimal. No work was done to extensively clean up the
template-created resources that were not used in the app.

### Running in Emulator
The app supports tapping the repository card to open the repository in the
device's browser. In the emulator the browser was seen to issue a warning that
the action was not safe. This did not happen on a physical device. Due to the
time constraints of the assignment, this issue was not investigated further.

## Setup
To prevent rate limiting, please add your Github API token to the 
`local.properties` file in the root of the project. The file should contain 
this exact variable with your Github API token as the value:

```properties
# The Github API token to use for the Github API that prevents rate limiting
GITHUB_API_TOKEN=<ADD_YOUR_GITHUB_API_TOKEN>
```

**NOTE:** If you do not have a Github API token, the app will still work but
you may encounter rate limiting issues. This was the case for me as the API key
provided was not valid. Additional in-app configuration was added to handle 
this case.

Gradle will print a warning if the Github API token is not provided:

```
***********************************************************************
*                             SETUP ISSUE                             *
* GITHUB_API_TOKEN is missing in local.properties. App will run       *
* without the token and may be rate limited by GitHub. See the        *
* README for instructions on how to add the token.                    *
***********************************************************************
```

If the Github API token is not provided, the app will still work but will be
rate limited by Github. If the token set is invalid, the app will display an
error message on the first data fetch attempt. Subsequent fetches will omit the
token and the app will be rate limited.

## Github API
The Github API is used to fetch the most starred repositories and their top
contributors. This app was built using API version 
[2022-11-28](https://docs.github.com/en/rest/search/search?apiVersion=2022-11-28).

### Repository Search API
The search API is described [here](https://docs.github.com/en/rest/search/search?apiVersion=2022-11-28#search-repositories).

Here is an example request to get the 100 most starred repositories:

```
https://api.github.com/search/repositories?q=stars:%3E1&sort=stars&order=desc&per_page=100
```

The response payload is built according to the API's [response schema](etc/repo-api-schema.json).

### Contributors API
The contributors API is described [here](https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#list-repository-contributors).

Here is an example request to get the contributors for a repository:

```
https://api.github.com/repos/facebook/react-native/contributors?per_page=1
```

The response payload is built according to the API's [response schema](etc/contributor-api-schema.json).