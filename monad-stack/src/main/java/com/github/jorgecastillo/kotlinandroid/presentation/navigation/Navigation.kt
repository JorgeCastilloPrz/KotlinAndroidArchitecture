package com.github.jorgecastillo.kotlinandroid.presentation.navigation

import arrow.core.ForId
import arrow.data.Reader
import arrow.data.map
import com.github.jorgecastillo.kotlinandroid.di.context.SuperHeroesContext.GetHeroesContext
import com.github.jorgecastillo.kotlinandroid.view.SuperHeroDetailActivity

class HeroDetailsPage {
  fun go(heroId: String) = Reader.ask<ForId, GetHeroesContext>().map({ (ctx) ->
    SuperHeroDetailActivity.launch(ctx, heroId)
  })
}
