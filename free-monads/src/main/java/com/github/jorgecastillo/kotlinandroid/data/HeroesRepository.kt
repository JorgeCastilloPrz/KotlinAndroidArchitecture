package com.github.jorgecastillo.kotlinandroid.data

import com.github.jorgecastillo.kotlinandroid.data.CachePolicy.*
import com.github.jorgecastillo.kotlinandroid.free.algebra.FreeHeroesAlgebra
import com.github.jorgecastillo.kotlinandroid.free.algebra.HeroesAlgebra.Companion.getAllHeroes
import com.github.jorgecastillo.kotlinandroid.free.algebra.HeroesAlgebra.Companion.getSingleHero
import com.karumi.marvelapiclient.model.CharacterDto

sealed class CachePolicy {
  object NetworkOnly : CachePolicy()
  object NetworkFirst : CachePolicy()
  object LocalOnly : CachePolicy()
  object LocalFirst : CachePolicy()
}

fun getHeroesWithCachePolicy(policy: CachePolicy): FreeHeroesAlgebra<List<CharacterDto>> =
    when (policy) {
      is NetworkOnly -> getAllHeroes()
      is NetworkFirst -> getAllHeroes()
      is LocalOnly -> getAllHeroes()
      is LocalFirst -> getAllHeroes()
    }

fun getHeroDetails(policy: CachePolicy, heroId: String): FreeHeroesAlgebra<CharacterDto> =
    when (policy) {
      is NetworkOnly -> getSingleHero(heroId)
      is NetworkFirst -> getSingleHero(heroId)
      is LocalOnly -> getSingleHero(heroId)
      is LocalFirst -> getSingleHero(heroId)
    }

fun getHeroesFromAvengerComicsWithCachePolicy(policy: CachePolicy): FreeHeroesAlgebra<List<CharacterDto>> = TODO()
