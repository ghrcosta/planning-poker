# planning-poker

Planning poker tool made in about 5 days as a small, personal project to learn
more about GCP, Kotlin and other different technologies.

It was made to run in GAE Standard environment (free tier), which doesn't
support websocket. In the current version, clients receive updates from 
Firestore directly using the Firebase javascript SDK. Compared to long pooling,
this reduced the load on the backend and sped up response time slightly.

To simplify development and deploy this project uses Kotlin Multiplatform. Even
though it's still Alpha, I wanted to (1) try it out and (2) learn more about
Kotlin instead of learning a new language (e.g. Dart for Flutter).



## Environment configuration

1. Install [Java 11](https://adoptium.net/?variant=openjdk11)
2. Install [Git Bash](https://git-scm.com/download)
    1. During installation, select option `Checkout as-is, commit 
       Unix-style`
        * Deploy will fail if the files have line separators with
          Windows style (CRLF)!
3. Install [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)
    1. Go to settings and make sure Kotlin code style is set to be the
       one from the project
    2. To make push via IDE work, access 
       https://source.developers.google.com/new-password
       and follow the steps
4. Install [Google Cloud SDK](https://cloud.google.com/sdk/docs/install)
    1. Use the Git Bash command line to install the required components:
        ```
        $ gcloud components install app-engine-java
        ```
5. Clone project
6. Install NPM
    * For Windows:
        1. Install https://github.com/coreybutler/nvm-windows
        2. Open Windows command prompt (or Git Bash) \***as admin**\*
        3. Execute:
            ```
            $ nvm install latest
            $ nvm use latest
            ```
           *Note*: at the time this readme was written, latest=19.0.1
7. Install Firebase CLI + Emulators
    ```
    $ npm install -g firebase-tools
    ```
    * Using CMD (Win+R > cmd) -- doesn't work properly on Git Bash (at least, not on 2.38.1):
        ```
        $ cd <project_directory>
        $ firebase login
            (will open browser to complete login)
            - If you were already logged in, execute:
                $ firebase login --reauth
                    (should open browser to complete login)
        $ firebase init emulators
            - Select your GCP project or create a new one
            - If you are in the correct project directory the settings
              are already defined, see firebase.json
            - Select "Download emulators"
        ```
8. Set up application-default credentials
    * This is only required to execute the application locally.
      See https://firebase.google.com/docs/admin/setup#testing_with_gcloud_end_user_credentials
    * In your GCP project, go to APIs & Services > OAuth consent screen
        * Configure it for external usage
        * Add scopes "/auth/userinfo.email" and "openid"
        * Add yourself as a test user
    * Go to APIs & Services > Credentials
        * Create a "Desktop" credential, download the json file and put
          it in the project root directory
    * Execute:
        ```
        $ gcloud auth application-default login --client-id-file=test_oauth_client_id.json
            (will open browser to complete login; check all boxes)
        ```



## Executing locally

Firestore emulator is required, otherwise the requests will be sent to the real
instance on GCP.



1. Start emulators
    * ```
      $ cd <project_directory>
      $ firebase emulators:start
      ```
    * Access the emulators UI via `http://localhost:8090`

2. Run server
    1. In `src/jsMain/config/Firebase.kt`, setup Firebase parameters by
       replacing `YOUR_GCP_DATA_HERE` with real values from your Firebase 
       project (see Firebase console > Project settings). This is required even
       for local execution.
    2. Execute `./gradlew run` passing the following environment variables:
        ```
        GOOGLE_CLOUD_PROJECT=<YOUR_GCP_PROJECT>;FIRESTORE_EMULATOR_HOST=localhost:8081
        ```
        Both variables are required in order for the server to communicate with
        the Firestore emulator.

After the server is running you can access the frontend via
`http://localhost:8080` (as defined in `jvmMain/resources/application.conf`).



## Deploy

1. Make sure AppEngine and Cloud Functions are both enabled on you GCP project.
2. Search for `YOUR_GCP_PROJECT` and `YOUR_GCP_DATA_HERE` replace with your own
   GCP project name and data (see Firebase console > Project settings).
3. If the backend is running locally, stop it, otherwise GAE deploy will fail
4. Deploy the backend
   ```
   ./gradlew clean appengineDeploy
   ```
5. Deploy Cron job
   ```
   $ cd <project_directory>
   $ gcloud app deploy cron.yaml
   ```
6. Deploy Firestore security rules
   ```
   $ cd <project_directory>
   $ firebase deploy --only firestore:rules
   ```
7. (OPTIONAL) Clean up artifacts from AppEngine deploy

   This is not required, but will help the GCP project stay in the free tier.
   See [link](https://stackoverflow.com/q/42947918),
   [link](https://stackoverflow.com/q/63578581).
   1. Access `https://console.cloud.google.com/gcr`, delete any old builds



## Improvement ideas

Not sure if I'll ever do any of these, but...

* Frontend
  * Frontend makeover
    * Use more Material UI components; learn how to properly change their CSS
  * Set production/development links via environment variable
    * Gradle -> Webpack -> React
  * Reduce minified .js size
  * Improve error handling
  * Tests :)
* Backend
  * Try deploying as a free tier e2-micro Compute Engine VM
    * Websocket would work there...
  * Improve startup time
    * Quarkus? Something else?
  * Improve logging
    * Group log messages according to the request that generated them
  * Reduce RAM usage
  * Execute all deployment steps with a single command
  * Tests :)
