package com.glyphpass.test

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.glyphpass.test.ui.theme.LearnFirestoreAuthTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


private const val TAG = "DebugFirestoreTest"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "Initializing Firebase Firestore")
        val db = Firebase.firestore

        Log.d(TAG, "Initializing Firebase Auth")
        val auth = Firebase.auth

        // logUserOut(auth)

        /* Calling addSimpleUser() here works as expected IF NO AUTH IS REQUIRED.
        // If the database requires an authenticated user e.g.
        // "allow read, write: if request.auth != null"
        // then calling this method from here will properly fail
        */
        // addSimpleRecordToFirestore (db,"Alex")

        /* I can properly add new users to the project whenever needed
        */
        // addFireBaseUserToProject(auth, "jeff3@yahoo.com", "Foo123")

        /*  I CANNOT  write to Firestore if I am authenticated! If no authentication is required,
            trying to insert into Firestore will fail silently. If auth is required e.g.
            "allow read, write: if request.auth != null" then it either fails due to inadequate permissions
            or fails silently
        */
        authenticateFireBaseUserAndWriteToFirestore(db, auth, "jeff@yahoo.com", "Foo123")


        setContent {
            LearnFirestoreAuthTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Firebase Auth!")
                }
            }
        }
    }

    private fun logUserOut(auth: FirebaseAuth) {
        Log.d(TAG, "Logging the user out!")
        auth.signOut()
    }


    private fun addFireBaseUserToProject(auth: FirebaseAuth, email: String, password : String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                }
            }
    } // end fun addFireBaseUserToProject()

    private fun addSimpleRecordToFirestore (db: FirebaseFirestore, firstName : String) {

        // Create a new user with a first and last name
        val user = hashMapOf(
            "first" to firstName,
            "last" to "Lovelace",
            "born" to 1815
        )

        // Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Document added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }    // end fun addSimpleUserToFirestore()


    private fun authUserAddSimpleRecordToFirestore (db: FirebaseFirestore, auth: FirebaseAuth, firstName : String) {

        // Create a new user with a first and last name
        val user = hashMapOf(
            "first" to firstName,
            "last" to "Lovelace",
            "born" to 1815
        )

        // Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Authenticated user added Document with ID: ${documentReference.id}")
                this.logUserOut(auth)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }    // end fun addSimpleUserToFirestore()


    fun authenticateFireBaseUserAndWriteToFirestore(db: FirebaseFirestore, auth: FirebaseAuth, email: String, password : String) {

        Log.d(TAG, "Attempting to login the user")

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "Sign In With Email: Success!")
                    val user = auth.currentUser
                    val uid = auth.uid
                    Log.d(TAG, "User ID is $uid")
                    Log.d(TAG, "Signed in User  is: $uid")
                    // Attempt to have the authenticated used write to Firestore
                    authUserAddSimpleRecordToFirestore(db, auth, "Zillow9")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    // queryForAllUsers(db)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

    }

}   // end class

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LearnFirestoreAuthTheme {
        Greeting("Android")
    }
}