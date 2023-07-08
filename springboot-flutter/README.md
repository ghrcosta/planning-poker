# planning-poker using Spring Boot + Flutter

It was made to run in GAE Standard environment (free tier), which doesn't
support websocket. Clients receive updates from Firestore directly using
the Firebase SDK for Flutter.



## Environment configuration

1. Install [Java 17](https://adoptium.net/?variant=openjdk17)
2. Install [Git Bash](https://git-scm.com/download)
    1. During installation, select option `Checkout as-is, commit
       Unix-style`.
        * Deploy will fail if the files have line separators with
          Windows style (CRLF)!
3. Install [Google Cloud SDK](https://cloud.google.com/sdk/docs/install)
    1. Use the Git Bash command line to install the required components:
        ```
        $ gcloud components install app-engine-java
        ```
    2. Create an environment variable called "GOOGLE_CLOUD_SDK_HOME" with the full
       path to the Cloud SDK /bin directory.
4. Install [Flutter SDK](https://docs.flutter.dev/get-started/install)
    1. Follow the instructions to install the SDK (don't forget the "additional
       requirements" section).
    2. If it shows a warning that the Android toolchain is missing, ignore it.
5. Install an IDE such as [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)
   or [VSCode](https://code.visualstudio.com/)
    1. Configure whatever is needed for Kotlin/Java and Flutter support.
6. Install NPM
    * For Windows:
        1. Install https://github.com/coreybutler/nvm-windows
        2. Open Windows command prompt (or Git Bash) \***as admin**\*
        3. Execute:
            ```
            $ nvm install latest
            $ nvm use latest
            ```
           *Note*: at the time this readme was written, latest=20.3.1
7. Install Firebase CLI + Emulators
    ```
    $ npm install -g firebase-tools
    ```
    * Using CMD (Win+R > cmd) -- doesn't work on Git Bash (at least, not on 2.38.1):
        ```
        $ cd <project_directory>/springboot
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
8. Install the Flutter [Firebase plugin](https://firebase.google.com/docs/flutter/setup?hl=pt-br&platform=web)
9. Optional: Set up application-default credentials
    * This is only required to execute the application locally.
      See https://firebase.google.com/docs/admin/setup#testing_with_gcloud_end_user_credentials
    1. In your GCP project, go to APIs & Services > OAuth consent screen
        * Configure it for external usage
        * Add scopes "/auth/userinfo.email" and "openid"
        * Add yourself as a test user
    2. Go to APIs & Services > Credentials
        * Create a "Desktop" credential, download the json file and put
          it in the project root directory
    3. Execute:
        ```
        $ gcloud auth application-default login --client-id-file=client_secret_<a-random-id>.json
            (will open browser to complete login; check all boxes)
        ```



## Executing locally

Firestore emulator is required, otherwise the requests will be sent to the real
instance on GCP.



1. Start emulators
    * ```
      $ cd <project_directory>/springboot
      $ firebase emulators:start
      ```
    * Access the emulators UI via `http://localhost:8090`
2. Run backend server
    1. In `<project_directory>/springboot/src/main/resources/`, change the 
       properties values for "local" and "prod" as necessary -- you will need to
       change at least the GCP project-id one.
    2. Execute `./gradlew run` or, if using IntelliJ IDEA, execute the saved
       configuration "PlanningPokerApplicationKt". The server will be waiting
       for requests at address `http://localhost:8080`. The Swagger API
       documentation will be available at `http://localhost:8080/swagger-ui`.
3. Run frontend server
    1. Execute `flutter run` or, if using an IDE, make sure the "target device"
       is configured to be one of your browsers, then use the corresponding button
       to execute the debug server. The browser will open automatically. Despite
       running in a different localhost port, the backend will accept frontend
       requests due to `allow-cross-origin=true` in `application-local.properties`.



## Deploy

1. Make sure AppEngine and Cloud Build API are both enabled on you GCP project.
2. If the backend is running locally, stop it, otherwise GAE deploy will fail
3. Make sure Google Cloud SDK is set up correctly:
    * If you have other projects (or just installed Google Cloud SDK):
        ```
        $ gcloud config set project <YOUR_GCP_PROJECT>
        ```
    * If you have multiple accounts (or just installed Google Cloud SDK):
        ```
        $ gcloud config set account <YOUR_GCP_USER_ACCOUNT_EMAIL>
        ```
    * If it's been a while since the last time you used Google Cloud SDK
      (or just installed it):
        ```
        $ gcloud auth login
        ```
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
7. (OPTIONAL but RECOMMENDED) Clean up artifacts from AppEngine deploy

   This is not required, but will help the GCP project stay in the free tier.
   See [link](https://stackoverflow.com/q/42947918),
   [link](https://stackoverflow.com/q/63578581).
    1. Access `https://console.cloud.google.com/gcr`, delete all files from this
       project
    2. Access `https://console.cloud.google.com/storage`, delete all files in
       "staging" and "artifacts" buckets



## Improvement ideas

Maybe someday...

* Frontend
    * Tests
* Backend
    * GraalVM?
    * Improve logging
        * Group log messages according to the request that generated them
    * Execute all deployment steps with a single command
