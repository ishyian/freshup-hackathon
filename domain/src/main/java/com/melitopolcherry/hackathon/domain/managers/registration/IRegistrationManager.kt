package com.melitopolcherry.hackathon.domain.managers.registration

import com.melitopolcherry.hackathon.data.models.login.AccessToken
import com.melitopolcherry.hackathon.data.models.login.LoginResponse
import com.melitopolcherry.hackathon.data.models.responces.GoogleAccessTokenResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Singleton

@Singleton
interface IRegistrationManager {

    fun googleLogin(userId: String, fcmToken: String): Single<LoginResponse>
    fun facebookLogin(userId: String, fcmToken: String): Single<LoginResponse>
    fun getGoogleAccessToken(
        grantType: String, clientId: String, clientSecret: String,
        redirectUri: String, code: String
    ): Single<GoogleAccessTokenResponse>

    fun refreshToken(refreshToken: String): Single<AccessToken>
    fun emailSignUp(emailSignUp: String): Completable
    fun emailRegistration(emailRegistration: HashMap<String, String>): Single<LoginResponse>

    fun emailLogin(email: String, password: String, token: String): Single<LoginResponse>
    fun changePassword(email: String, newPassword: String, code: String): Single<LoginResponse>
    fun forgotPassword(email: String): Completable
}
