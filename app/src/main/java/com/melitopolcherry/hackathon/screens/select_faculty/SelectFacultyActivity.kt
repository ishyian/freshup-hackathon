package com.melitopolcherry.hackathon.screens.select_faculty

import android.content.DialogInterface
import android.content.Intent
import androidx.core.view.WindowCompat
import com.melitopolcherry.hackathon.core.extensions.showSelectorDialog
import com.melitopolcherry.hackathon.core.presentation.BaseActivity
import com.melitopolcherry.hackathon.data.model.FACULTIES
import com.melitopolcherry.hackathon.data.model.Faculty
import com.melitopolcherry.hackathon.databinding.ActivitySelectFacultyBinding
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.melitopolcherry.hackathon.screens.select_faculty.adapter.FacultyListAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectFacultyActivity : BaseActivity<ActivitySelectFacultyBinding>(),
    FacultyListAdapter.OnListItemSelectedListener {

    @Inject
    lateinit var accountManager: IAccountManager

    private val adapter = FacultyListAdapter(this)

    override fun initBinding(): ActivitySelectFacultyBinding {
        return ActivitySelectFacultyBinding.inflate(layoutInflater)
    }

    override fun onCreate() = with(binding) {
        super.onCreate()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        rvFaculties.adapter = adapter
        val universityId = accountManager.fetchUniversity()
        universityId?.universityId?.let { id ->
            adapter.setData(FACULTIES.filter { it.universityId == id })
        }
        Unit
    }

    override fun itemSelected(faculty: Faculty) {
        if (faculty.groups.isEmpty()) {
            return
        }
        showSelectorDialog("Оберіть свою групу", faculty.groups) { dialogInterface: DialogInterface, i: Int ->
            accountManager.saveFaculty(faculty.id, faculty.groups[i])
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}