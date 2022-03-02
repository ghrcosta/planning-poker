// Whenever a room is updated, notify the server so it can send the update to
// its clients.
// See: https://firebase.google.com/docs/functions/firestore-events.

// During development, after any change execute "npm run build" to recompile the
// function. After the emulators are started, check the logs at
// http://localhost:8090/logs.


import * as functions from "firebase-functions";

// Installed with "npm install node-fetch@2". The "@2" is because the normal
// version (v3) apparently does not work in Cloud Functions (v2 is "CommonJS",
// v3 is "ESM"). See "https://github.com/node-fetch/node-fetch".
// Also had to execute "npm install @types/node-fetch".
import fetch from "node-fetch";


const serverUrl =
  (process.env.FUNCTIONS_EMULATOR) ?
    "http://localhost:8080" :
    "https://YOUR_GCP_PROJECT.appspot.com";


export const notifyRoomChange =
  functions.firestore.document("room/{roomId}").onUpdate(async (_, context) => {
    functions.logger.info(`Room ${context.params.roomId} updated`);
    await fetch(
        `${serverUrl}/${context.params.roomId}/notifyUpdated`,
        {method: "POST"}
    );
  });
