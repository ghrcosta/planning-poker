# planning-poker

Planning poker application, made as a personal project to learn more about GCP,
Kotlin and other different technologies.

It was made to run in GAE Standard environment (free tier), which doesn't
support websocket, so long polling is used instead. Data is stored in Firestore
Native mode, and the backend is notified of database changes by a Cloud Function
that listens to Firestore events.

This application is not scalable. In order to continue using the GCP free tier
we can only deploy using automatic or basic scaling, and neither support sending
requests to specific instances (or, in this case, to all of them). Due to these
limitations the application must always have only one instance, to make sure all
clients will receive the new data when Cloud Function notifies that the database
has been modified. If more than one instance is created, the application will
send the new data to the clients on the instance that received the notification
and the others would have to wait until their next sync request.

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



## Executing locally

Firestore and Cloud Functions emulators are required, otherwise the
requests will be sent to the real instances on GCP.



### Starting emulators

Using Git Bash:
```
$ cd <project_directory>/functions
$ npm run build
$ cd ..
$ firebase emulators:start
```
Access the emulators UI via `http://localhost:8090`



## Executing the server

Execute `./gradlew run` passing the following environment variables:
```
GOOGLE_CLOUD_PROJECT=<YOUR_GCP_PROJECT>;FIRESTORE_EMULATOR_HOST=localhost:8081
```
Both variables are required in order for the server to communicate with the
Firestore emulator.

After the server start running you can access the frontend via
`http://localhost:8080` (as defined in `jvmMain/resources/application.conf`).



## Deploy

1. Make sure AppEngine and Cloud Functions are both enabled on you GCP project.
2. Search for `YOUR_GCP_PROJECT` and replace with your own GCP project name.
3. Deploy the backend
   ```
   ./gradlew appengineDeploy
   ```
4. Deploy the Cloud function
   ```
   $ cd <project_directory>/functions
   $ npm install
   $ npm run build
   $ firebase deploy --only functions
       (if ESLint complains, make the necessary changes and try deploying again)
   ```
5. Deploy Cron job
   ```
   $ cd <project_directory>
   $ gcloud app deploy cron.yaml
   ```