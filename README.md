# planning-poker

Simple planning poker application to learn more about Kotlin and
other different technologies.

Made to run in GAE Standard environment, which doesn't support websocket
so long polling is used instead. Data is stored in Firestore Native mode,
and the backend is notified of database changes by a Cloud Function that
listens to Firestore events.

In order to scale, the Cloud Function would have to notify all live
instances, so they can send the update to all ongoing polling requests.
Automatic scaling doesn't allow targeting of individual instances
therefore basic scaling is used instead.

To simplify development and deploy this project uses Kotlin. Even though
it's still Alpha, I wanted to (1) try it out and (2) learn more about
Kotlin instead of learning a new language (e.g. for Flutter).

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
        2. Open Windows command prompt (or Git Bash) as admin
        3. Execute:
            ```
            $ nvm install latest
            $ nvm use latest
            ```
7. Install Firebase CLI + Emulators
    ```
    $ npm install -g firebase-tools
    ```
    * Using CMD (Win+R > cmd) -- doesn't work on Git Bash (as of 9.34.0):
        ```
        $ cd <project_directory>
        $ firebase login
            (will open browser to complete login)
        $ firebase init emulators
            - Select your GCP project or create a new one
            - If you are in the correct project directory the settings
              are already defined:
                * Functions emulator on port 5050
                * Firestore emulator on port 8081
                * Emulator UI enabled on port 8090
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

## Starting emulators
Using Git Bash:
```
$ cd <project_directory>/functions
$ npm run build
$ cd ..
$ firebase emulators:start
```