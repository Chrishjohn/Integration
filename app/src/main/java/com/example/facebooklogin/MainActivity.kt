package com.example.facebooklogin

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : AppCompatActivity() {
    var TAG: String = "MainActivity"

    /*For Google*/
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    /*For Facebook*/
    private lateinit var callbackManager: CallbackManager
    var mutableList: MutableList<Int> = mutableListOf(1, 2, 3)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mutableList.also {
            it.add(12)
        }
        Log.e(TAG, "mutable list:" + mutableList)
        try {
            val info = packageManager.getPackageInfo(
                "com.example.facebooklogin",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
/*
        * For Facebook signup implementation
        * */
        callbackManager = CallbackManager.Factory.create()

        button.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("public_profile", "email"))
        }

        // Callback for facebook registration
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    getUserProfile(loginResult?.accessToken, loginResult?.accessToken?.userId)
                    //          Toast.makeText(this@MainActivity, "Login success", Toast.LENGTH_LONG).show()

                }

                override fun onCancel() {
                    //  Log.e(TAG,""+logi)
                    Toast.makeText(this@MainActivity, "Login Cancelled", Toast.LENGTH_LONG).show()
                }

                override fun onError(exception: FacebookException) {
                    Toast.makeText(this@MainActivity, exception.message, Toast.LENGTH_LONG).show()
                }
            })
/*For Google Signin Code*/
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        button2.setOnClickListener {
            signIn()
        }
    }

    /* For Google Signin Code */
    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
            signInIntent, RC_SIGN_IN
        )
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )
            // Signed in successfully
            val googleId = account?.id ?: ""

            val googleFirstName = account?.givenName ?: ""

            val googleLastName = account?.familyName ?: ""

            val googleEmail = account?.email ?: ""

            val googleProfilePicURL = account?.photoUrl.toString()

            val googleIdToken = account?.idToken ?: ""

            //    socialSignupAPI(googleFirstName ,googleLastName, googleProfilePicURL, googleEmail, googleId, "Google")
        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e("-->failed code=", e.statusCode.toString())
        }
    }

    /* For Facebook Signin Code */
    @SuppressLint("LongLogTag")
    fun getUserProfile(token: AccessToken?, userId: String?) {
        token?.let {
            Log.e(TAG, "accesstoken=" + token.token + "userid=" + userId)
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
//gmail
        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

    }
}