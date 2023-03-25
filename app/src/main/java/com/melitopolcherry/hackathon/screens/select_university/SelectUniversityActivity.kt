package com.melitopolcherry.hackathon.screens.select_university

import android.content.Intent
import androidx.core.view.WindowCompat
import com.melitopolcherry.hackathon.core.presentation.BaseActivity
import com.melitopolcherry.hackathon.data.model.UNIVERSITIES
import com.melitopolcherry.hackathon.data.model.University
import com.melitopolcherry.hackathon.databinding.ActivitySelectUniversityBinding
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.screens.select_faculty.SelectFacultyActivity
import com.melitopolcherry.hackathon.screens.select_university.adapter.UniversityListAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectUniversityActivity : BaseActivity<ActivitySelectUniversityBinding>(),
    UniversityListAdapter.OnListItemSelectedListener {

    @Inject
    lateinit var accountManager: IAccountManager

    private val adapter = UniversityListAdapter(this)

    override fun initBinding(): ActivitySelectUniversityBinding {
        return ActivitySelectUniversityBinding.inflate(layoutInflater)
    }

    override fun onCreate() = with(binding) {
        super.onCreate()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        rvUniversities.adapter = adapter
        adapter.setData(UNIVERSITIES)
    }

    override fun itemSelected(university: University) {
        accountManager.saveUniversity(university.id)
        startActivity(Intent(this, SelectFacultyActivity::class.java))
    }
}