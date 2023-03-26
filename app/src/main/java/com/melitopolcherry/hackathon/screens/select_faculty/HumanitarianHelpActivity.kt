package com.melitopolcherry.hackathon.screens.select_faculty

import android.content.DialogInterface
import android.content.Intent
import androidx.core.view.WindowCompat
import com.melitopolcherry.hackathon.core.extensions.showSelectorDialog
import com.melitopolcherry.hackathon.core.presentation.BaseActivity
import com.melitopolcherry.hackathon.data.model.FACULTIES
import com.melitopolcherry.hackathon.data.model.Faculty
import com.melitopolcherry.hackathon.databinding.ActivityHumanitarianHelpBinding
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.melitopolcherry.hackathon.screens.events.CategoryType
import com.melitopolcherry.hackathon.screens.events.adapter.CategoriesAdapter
import com.melitopolcherry.hackathon.screens.select_faculty.adapter.HumanitarianHelpAdapter
import com.melitopolcherry.hackathon.screens.select_faculty.adapter.HumanitarianUiModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HumanitarianHelpActivity : BaseActivity<ActivityHumanitarianHelpBinding>() {

    @Inject
    lateinit var accountManager: IAccountManager

    private val adapter by lazy {
        HumanitarianHelpAdapter()
    }

    override fun initBinding(): ActivityHumanitarianHelpBinding {
        return ActivityHumanitarianHelpBinding.inflate(layoutInflater)
    }

    override fun onCreate() = with(binding) {
        super.onCreate()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        rvHelp.adapter = adapter
        adapter.items = listOf(
            HumanitarianUiModel("Доброго дня, ми родина вимушених переселенців з Луганської області. Переселенці вже вдруге. Маю двох донечок-8 та 15 років. Я є інвалідом з дитинства по зору. Не вистачає коштіа на найнеобхідніші речі.Дуже потребуємо вашої допомоги! Щиро вдячні!", 6, 0),
            HumanitarianUiModel("Я інвалід дитинства, хворію псоріазом. Маю сина, 11 років, одинока мама. В зв'язку з війною підприємство на якому працювала майже не працює, 25% від мін. заробітної платні. Проживаю разом з літньою мамою- пенсіонеркою.", 31, 0),
            HumanitarianUiModel("Я багатодітна мати, у зв'язку із військовим станом потребую допомоги. Моєй сім'ї, необхідні продукти харчування, засоби гігієни та ліки.Від держави отримую тільки 2.100, як багатодітна мати, роботу втратила із за війни у країні.", 2, 0)
        )
    }
}