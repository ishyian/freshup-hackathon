package com.melitopolcherry.hackathon.domain.managers.registration

import com.melitopolcherry.hackathon.data.models.login.LoginResponse
import com.melitopolcherry.hackathon.data.models.responces.GoogleAccessTokenResponse
import com.melitopolcherry.hackathon.data.repo.EvenzRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistrationManager @Inject constructor(private val repository: EvenzRepository) :
    IRegistrationManager {

    override fun emailRegistration(emailRegistration: HashMap<String, String>): Single<LoginResponse> {
        return repository.emailRegistration(emailRegistration)
    }

    override fun emailSignUp(emailSignUp: String): Completable {
        val hash = HashMap<String, String>()
        hash["email"] = emailSignUp
        return repository.emailSignUp(hash)
    }

    override fun refreshToken(refreshToken: String): Single<com.melitopolcherry.hackathon.data.models.login.AccessToken> {
        return repository.refreshToken(refreshToken, "refresh_token")
    }

    override fun getGoogleAccessToken(
        grantType: String, clientId: String, clientSecret: String,
        redirectUri: String,
        code: String
    ): Single<GoogleAccessTokenResponse> {
        return repository.getGoogleAccessToken(grantType, clientId, clientSecret, redirectUri, code)
    }

    override fun emailLogin(email: String, password: String, token: String): Single<LoginResponse> {
        return repository.emailLogin(email, password, token)
    }

    override fun forgotPassword(email: String): Completable {
        val hash = HashMap<String, String>()
        hash["email"] = email
        return repository.forgotPassword(hash)
    }

    override fun changePassword(
        email: String,
        newPassword: String,
        code: String
    ): Single<LoginResponse> {
        val hash = HashMap<String, String>()
        hash["email"] = email
        hash["password"] = newPassword
        hash["confirmation_code"] = code
        hash["repeated_password"] = newPassword
        return repository.changePassword(hash)
    }

    override fun facebookLogin(userId: String, fcmToken: String): Single<LoginResponse> {
        return repository.facebookLogin(userId, fcmToken)
    }

    override fun googleLogin(userId: String, fcmToken: String): Single<LoginResponse> {
        return repository.googleLogin(userId, fcmToken)
    }
}