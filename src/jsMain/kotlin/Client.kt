import components.addParticipant.AddParticipantComponent
import components.index.IndexComponent
import components.room.RoomUI
import config.FirestoreHelper
import external.decodeURIComponent
import external.firebase.connectFirestoreEmulator
import kotlinx.browser.document
import kotlinx.browser.window
import react.create
import react.dom.render


val firestore = FirestoreHelper.initFirestore()

// Yes, totally a workaround... The first of many on this frontend :)
val HOST =
    if (window.location.hostname == "localhost") {
        // https://firebase.google.com/docs/emulator-suite/connect_firestore#web-version-9
        connectFirestoreEmulator(firestore, "localhost", 8081)
        "http://localhost:8080"
    } else {
        "https://YOUR_GCP_PROJECT.appspot.com"  // Edit to set your own GCP project
    }

object LocalData {
    var room: Room? = null
    var userName: String? = null
    val voteOptions = mapOf(
        "voteButton_0" to "0",
        "voteButton_one_half" to "½",
        "voteButton_1" to "1",
        "voteButton_2" to "2",
        "voteButton_3" to "3",
        "voteButton_5" to "5",
        "voteButton_8" to "8",
        "voteButton_13" to "13",
        "voteButton_question" to "?"
    )
}

fun main() {
    updateNavigation()
}

fun updateNavigation() {
    val root = document.getElementById("root") ?: error("Couldn't find root container")

    // For whatever reason, adding the "react-router-dom" module to build.gradle messes up the project in IntelliJ IDEA
    // and JS code stops being recognized, so we will navigate between pages without using it.

    val roomIdInUri = window.location.pathname.split("/")[1]

    val session = getSession()
    val roomIdInSession = session?.roomId ?: ""
    val userNameInSession = session?.participantName ?: ""

    val pageToRender =
        if (roomIdInUri.isBlank()) {
            IndexComponent.create()
        } else {
            if (roomIdInUri == roomIdInSession && userNameInSession.isNotBlank()) {
                if (LocalData.room == null) {
                    // On the first sync the browser will download the most recent server data and replace this
                    LocalData.room = Room(id = roomIdInSession, participants = listOf())
                    LocalData.userName = userNameInSession
                }
                RoomUI.create()
            } else {
                AddParticipantComponent.create()
            }
        }
    render(pageToRender, root)
}

fun goTo(newPath: String) {
    window.history.pushState("", "", "/${newPath}")
    updateNavigation()
}

private fun getSession(): ParticipantSession? {
    document.cookie
        .split(";")
        .find { it.trim().startsWith("session=") }
        ?.let { sessionRawEntry ->
            // rawValue: participantName%3D%2523stest%26roomId%3D%2523s5760b48b%2D1624%2D48ea%2Dbd06%2Dcd4f04b5c574
            // 1st pass: participantName=%23stest&roomId=%23s5760b48b-1624-48ea-bd06-cd4f04b5c574
            // 2nd pass: participantName=#stest&roomId=#s5760b48b-1624-48ea-bd06-cd4f04b5c574
            val sessionRawValue = sessionRawEntry.trim().split("=")[1]
            val session1stPass = decodeURIComponent(sessionRawValue)
            val session2ndPass = decodeURIComponent(session1stPass)
            val sessionRawItems = session2ndPass.split("&")

            val sessionRoomId = extractSessionItemValue(sessionRawItems, "roomId")
            val sessionParticipantName = extractSessionItemValue(sessionRawItems, "participantName")
            if (sessionRoomId != null && sessionParticipantName != null) {
                return ParticipantSession(
                    roomId = sessionRoomId,
                    participantName = sessionParticipantName
                )
            }
        }
    return null
}

private fun extractSessionItemValue(rawList: List<String>, key: String): String? {
    return rawList
        .find { it.startsWith("${key}=") }
        ?.split("=")?.get(1)
        ?.substringAfter("#s")  // Assuming that "#s" is just an indicator that the data is a String
        ?.replace("+", " ")
}