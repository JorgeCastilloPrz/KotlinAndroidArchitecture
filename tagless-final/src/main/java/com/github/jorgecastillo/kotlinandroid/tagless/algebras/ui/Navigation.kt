package com.github.jorgecastillo.kotlinandroid.tagless.algebras.ui

import android.content.Context
import arrow.Kind
import arrow.effects.MonadSuspend
import com.github.jorgecastillo.kotlinandroid.tagless.runtime.ui.SuperHeroDetailActivity
import javax.inject.Inject

class Navigation<F> @Inject constructor(val monadSuspend: MonadSuspend<F>) {

    fun goToHeroDetailsPage(ctx: Context, heroId: String): Kind<F, Unit> =
            monadSuspend { SuperHeroDetailActivity.launch(ctx, heroId) }

}