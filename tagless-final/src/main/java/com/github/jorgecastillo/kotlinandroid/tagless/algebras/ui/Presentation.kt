package com.github.jorgecastillo.kotlinandroid.tagless.algebras.ui

import android.content.Context
import arrow.Kind
import arrow.TC
import arrow.effects.MonadSuspend
import arrow.typeclass
import arrow.typeclasses.MonadError
import arrow.typeclasses.binding
import arrow.typeclasses.bindingCatch
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.business.HeroesUseCases
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.business.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.ui.model.SuperHeroViewModel
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.MarvelImage
import javax.inject.Inject

interface SuperHeroesView {

    fun showNotFoundError(): Unit

    fun showGenericError(): Unit

    fun showAuthenticationError(): Unit
}

interface SuperHeroesListView : SuperHeroesView {

    fun drawHeroes(heroes: List<SuperHeroViewModel>): Unit

}

interface SuperHeroDetailView : SuperHeroesView {

    fun drawHero(hero: SuperHeroViewModel)

}

class Presentation<F> @Inject constructor(
        val navigation: Navigation<F>,
        val heroesService: HeroesUseCases<F>,
        val monadSuspend: MonadSuspend<F>) {

    fun onHeroListItemClick(ctx: Context, heroId: String): Kind<F, Unit> =
            navigation.goToHeroDetailsPage(ctx, heroId)

    fun displayErrors(view: SuperHeroesView, t: Throwable): Kind<F, Unit> =
            monadSuspend {
                when (CharacterError.fromThrowable(t)) {
                    is CharacterError.NotFoundError -> view.showNotFoundError()
                    is CharacterError.UnknownServerError -> view.showGenericError()
                    is CharacterError.AuthenticationError -> view.showAuthenticationError()
                }
            }

    fun drawSuperHeroes(view: SuperHeroesListView): Kind<F, Unit> =
            monadSuspend.bindingCatch {
                val result = monadSuspend.handleError(heroesService.getHeroes(), { displayErrors(view, it); emptyList() }).bind()
                monadSuspend.pure(view.drawHeroes(result.map {
                    SuperHeroViewModel(
                            it.id,
                            it.name,
                            it.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY),
                            it.description)
                })).bind()
            }


    fun drawSuperHeroDetails(heroId: String, view: SuperHeroDetailView): Kind<F, Unit> =
            monadSuspend.bindingCatch {
                val result = monadSuspend.handleError(heroesService.getHeroDetails(heroId), { displayErrors(view, it); CharacterDto() }).bind()
                monadSuspend.pure(view.drawHero(SuperHeroViewModel(
                        result.id,
                        result.name,
                        result.thumbnail.getImageUrl(MarvelImage.Size.PORTRAIT_UNCANNY),
                        result.description))).bind()
            }

}
