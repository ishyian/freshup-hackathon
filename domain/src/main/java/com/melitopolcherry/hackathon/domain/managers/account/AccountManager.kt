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
import com.melitopolcherry.hackathon.data.models.realmModels.RealmLocation
import com.melitopolcherry.hackathon.data.models.realmModels.RealmLoginMethod
import com.melitopolcherry.hackathon.data.models.realmModels.RealmOnBoardingFlag
import com.melitopolcherry.hackathon.data.models.realmModels.RealmRecentlyCities
import com.melitopolcherry.hackathon.data.models.realmModels.RealmRecentlySearch
import com.melitopolcherry.hackathon.data.models.realmModels.RealmUniversity
import com.melitopolcherry.hackathon.data.models.responces.AddAddressResponse
import com.melitopolcherry.hackathon.data.models.responces.AddPhoneResponse
import com.melitopolcherry.hackathon.data.models.responces.ChangeEmailResponse
import com.melitopolcherry.hackathon.data.models.responces.UpdateUserNameResponse
import com.melitopolcherry.hackathon.data.repo.EvenzRepository
import com.melitopolcherry.hackathon.data.utils.Enums
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.realm.Realm
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountManager @Inject constructor(private val repository: EvenzRepository) :
    IAccountManager {
    init {
        Realm.getApplicationContext()
    }

    private var realm: Realm = Realm.getDefaultInstance()

    override fun deleteToken() {
        realm.executeTransaction {
            val result = it.where(AccessToken::class.java).findFirst()
            result?.deleteFromRealm()
        }
    }

    override fun deleteLocation() {
        realm.executeTransaction {
            val result = it.where(RealmLocation::class.java).findFirst()
            result?.deleteFromRealm()
        }
    }

    override fun deleteRecentlyCities() {
        realm.executeTransaction {
            val result = it.where(RealmRecentlyCities::class.java).findFirst()
            result?.deleteFromRealm()
        }
    }

    override fun isDataFilled(): Boolean {
        return fetchUniversity() != null && fetchFaculty() != null
    }

    override val isLoggedIn: Boolean
        get() = fetchUser() != null

    override val isOnBoardingPassed: Boolean
        get() = getOnBoardingFlag() != null

    override fun deleteFully() {
        deleteLocation()
        deleteRecentlyCities()
        deleteToken()
        deleteUser()
        deleteLocation()
    }

    override fun saveOnBoardingFlag(flag: Boolean) {
        realm.executeTransaction {
            it.insertOrUpdate(RealmOnBoardingFlag(flag))
        }
    }

    override fun saveUniversity(universityId: String) {
        realm.executeTransaction {
            it.insertOrUpdate(RealmUniversity(universityId))
        }
    }

    override fun fetchUniversity(): RealmUniversity? {
        val copy = realm.where(RealmUniversity::class.java).findFirst()
        return if (copy != null) {
            realm.copyFromRealm(copy)
        } else {
            null
        }
    }

    override fun saveFaculty(facultyId: String, group: String) {
        realm.executeTransaction {
            it.insertOrUpdate(RealmFaculty(facultyId, group))
        }
    }

    override fun fetchFaculty(): RealmFaculty? {
        val copy = realm.where(RealmFaculty::class.java).findFirst()
        return if (copy != null) {
            realm.copyFromRealm(copy)
        } else {
            null
        }
    }

    override fun getOnBoardingFlag(): Boolean? {
        val copy = realm.where(RealmOnBoardingFlag::class.java).findFirst()
        return if (copy != null) {
            realm.copyFromRealm(copy)?.isPassed
        } else {
            null
        }
    }

    override fun deleteOnBoardingFlag() {
        realm.executeTransaction {
            val result = it.where(RealmOnBoardingFlag::class.java).findFirst()
            result?.deleteFromRealm()
        }
    }

    override fun saveUser(user: User) {
        realm.executeTransaction {
            it.insertOrUpdate(user)
        }
    }

    override fun saveToken(token: AccessToken) {
        realm.executeTransaction {
            it.insertOrUpdate(token)
        }
    }

    override fun getToken(): AccessToken? {
        val realmToken = realm.where(AccessToken::class.java).findFirst()
        return if (realmToken != null) {
            realm.copyFromRealm(realmToken)
        } else {
            null
        }
    }

    override fun saveLocation(loc: LatLng) {
        realm.executeTransaction {
            it.insertOrUpdate(RealmLocation(loc.latitude, loc.longitude))
        }
    }

    override fun getLocation(): LatLng? {
        val loc = realm.where(RealmLocation::class.java).findFirst()
        return if (loc != null) {
            val location = realm.copyFromRealm(loc)
            if (location?.latitude != null) {
                LatLng(location.latitude!!, location.longitude!!)
            } else {
                null
            }
        } else {
            null
        }
    }

    override fun saveRecentlyCities(city: City) {
        val cities = mutableListOf<City>()
        getRecentlyCities().let { cities.addAll(it) }
        var containsCity = false

        cities.forEach {
            val equals = it.name?.equals(city.name)
            if (equals != null && equals) {
                containsCity = true
            }
        }

        if (!containsCity) {
            if (cities.size < 5) {
                cities.add(0, city)
            } else {
                cities.removeAt(0)
                cities.add(0, city)
            }
            realm.executeTransactionAsync {
                it.insertOrUpdate(RealmRecentlyCities(cities))
            }
        }
    }

    override fun getRecentlyCities(): List<City> {
        var list = listOf<City>()
        realm.executeTransaction { r ->
            val s = r.where(RealmRecentlyCities::class.java).findFirst()
            s?.let {
                val copied = r.copyFromRealm(s)
                list = copied.listOfRecentlyCities!!
            }
        }
        return list
    }

    override fun saveRecentlySearch(search: String) {
        val cities = mutableListOf<String>()
        getRecentlySearch().let { cities.addAll(it) }
        var containsCity = false

        cities.forEach {
            val equals = it == search
            if (equals) {
                containsCity = true
            }
        }

        if (!containsCity) {
            if (cities.size < 5) {
                cities.add(0, search)
            } else {
                cities.removeAt(0)
                cities.add(0, search)
            }
            realm.executeTransactionAsync {
                it.insertOrUpdate(RealmRecentlySearch(cities))
            }
        }
    }

    override fun getRecentlySearch(): List<String> {
        var list = listOf<String>()
        realm.executeTransaction { r ->
            val s = r.where(RealmRecentlySearch::class.java).findFirst()
            s?.let {
                val copied = r.copyFromRealm(s)
                list = copied.listOfRecentlySearch!!
            }
        }
        return list
    }

    override fun deleteRecentlySearch() {
        realm.executeTransaction {
            val result = it.where(RealmRecentlySearch::class.java).findFirst()
            result?.deleteFromRealm()
        }
    }

    override fun saveLoginMethod(loginMethod: Enums.LoginMethods) {
        realm.executeTransaction {
            it.insertOrUpdate(RealmLoginMethod(loginMethod))
        }
    }

    override fun getLoginMethod(): Enums.LoginMethods? {
        val method = realm.where(RealmLoginMethod::class.java).findFirst()
        return if (method != null) {
            val loginMethod = realm.copyFromRealm(method)
            if (loginMethod?.loginMethod != null) {
                Enums.LoginMethods.valueOf(loginMethod.loginMethod!!)
            } else {
                null
            }
        } else {
            null
        }
    }

    override fun getProfile(authorization: String): Single<User> {
        return repository.getProfile(authorization)
    }

    override fun deleteAccount(authorization: String): Completable {
        return repository.deleteAccount(authorization)
    }

    override fun addAddress(
        auth: String,
        address: Address
    ): Single<AddAddressResponse> {
        return repository.addAddress(auth, address)
    }

    override fun updateUserName(
        authorization: String,
        parameters: HashMap<String, Any?>
    ): Single<UpdateUserNameResponse> {
        return repository.updateUserName(authorization, parameters)
    }

    override fun updateUserEmail(
        authorization: String,
        email: String
    ): Single<ChangeEmailResponse> {
        return repository.updateUserEmail(authorization, email)
    }

    override fun addPhoneNumber(
        authorization: String,
        parameters: HashMap<String, Any?>
    ): Single<AddPhoneResponse> {
        return repository.addPhoneNumber(authorization, parameters)
    }

    override fun sendCreditCard(
        authorization: String,
        paymentMethod: PaymentMethod
    ): Single<CreditCardResponse> {
        return repository.sendCreditCard(authorization, paymentMethod)
    }

    override fun updateFCM(authorization: String, appid: String): Completable {
        return repository.updateFCM(authorization, appid)
    }

    override fun sendPhoneCode(authorization: String): Completable {
        return repository.sendPhoneCode(authorization)
    }

    override fun confirmPhoneCode(authorization: String, code: String): Completable {
        return repository.confirmPhoneCode(authorization, code)
    }

    override fun updatePassword(
        authorization: String,
        parameters: HashMap<String, Any?>
    ): Single<LoginResponse> {
        return repository.updatePassword(authorization, parameters)
    }

    override fun deleteUser() {
        realm.executeTransaction {
            val result = it.where(User::class.java).findFirst()
            result?.deleteFromRealm()
        }
    }

    override fun fetchUser(): User? {
        val copy = realm.where(User::class.java).findFirst()
        return if (copy != null) {
            realm.copyFromRealm(copy)
        } else {
            null
        }
    }

    override fun closeRealm() {
        realm.close()
    }
}