package com.melitopolcherry.hackathon.domain.managers.account

import com.google.android.gms.maps.model.LatLng
import com.melitopolcherry.hackathon.data.models.Address
import com.melitopolcherry.hackathon.data.models.City
import com.melitopolcherry.hackathon.data.models.checkout.CreditCardResponse
import com.melitopolcherry.hackathon.data.models.checkout.PaymentMethod
import com.melitopolcherry.hackathon.data.models.login.AccessToken
import com.melitopolcherry.hackathon.data.models.login.LoginResponse
import com.melitopolcherry.hackathon.data.models.login.User
import com.melitopolcherry.hackathon.data.models.realmModels.RealmFaculty
import com.melitopolcherry.hackathon.data.models.realmModels.RealmUniversity
import com.melitopolcherry.hackathon.data.models.responces.AddAddressResponse
import com.melitopolcherry.hackathon.data.models.responces.AddPhoneResponse
import com.melitopolcherry.hackathon.data.models.responces.ChangeEmailResponse
import com.melitopolcherry.hackathon.data.models.responces.UpdateUserNameResponse
import com.melitopolcherry.hackathon.data.utils.Enums
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface IAccountManager {

    val isLoggedIn: Boolean?
    val isOnBoardingPassed: Boolean?

    fun deleteFully()

    fun saveLocation(loc: LatLng)
    fun getLocation(): LatLng?
    fun saveUser(user: User)
    fun saveToken(token: AccessToken)
    fun getToken(): AccessToken?
    fun deleteUser()
    fun fetchUser(): User?
    fun deleteAccount(authorization: String): Completable
    fun saveLoginMethod(loginMethod: Enums.LoginMethods)
    fun getLoginMethod(): Enums.LoginMethods?
    fun deleteToken()
    fun deleteLocation()
    fun deleteRecentlyCities()
    fun updateFCM(authorization: String, appid: String): Completable

    fun getProfile(authorization: String): Single<User>

    fun sendPhoneCode(authorization: String): Completable
    fun confirmPhoneCode(authorization: String, code: String): Completable
    fun updatePassword(
        authorization: String,
        parameters: HashMap<String, Any?>
    ): Single<LoginResponse>

    fun addAddress(auth: String, address: Address): Single<AddAddressResponse>
    fun updateUserName(
        authorization: String,
        parameters: HashMap<String, Any?>
    ): Single<UpdateUserNameResponse>

    fun updateUserEmail(authorization: String, email: String): Single<ChangeEmailResponse>
    fun addPhoneNumber(
        authorization: String,
        parameters: HashMap<String, Any?>
    ): Single<AddPhoneResponse>

    fun sendCreditCard(
        authorization: String,
        paymentMethod: PaymentMethod
    ): Single<CreditCardResponse>

    fun saveOnBoardingFlag(flag: Boolean)
    fun getOnBoardingFlag(): Boolean?
    fun deleteOnBoardingFlag()

    fun saveRecentlyCities(city: City)
    fun getRecentlyCities(): List<City>?

    fun saveRecentlySearch(search: String)
    fun getRecentlySearch(): List<String>
    fun deleteRecentlySearch()

    fun closeRealm()
    fun isDataFilled(): Boolean
    fun saveUniversity(universityId: String)
    fun fetchUniversity(): RealmUniversity?
    fun saveFaculty(facultyId: String, group: String)
    fun fetchFaculty(): RealmFaculty?
}