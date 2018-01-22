package com.github.jorgecastillo.kotlinandroid.io.algebras.services

import arrow.effects.IO
import com.github.jorgecastillo.kotlinandroid.io.algebras.persistence.HeroesRepository
import com.karumi.marvelapiclient.model.CharacterDto

object HeroesService {

  fun getHeroes(): IO<List<CharacterDto>> =
      HeroesRepository.getHeroesWithCachePolicy(
          HeroesRepository.CachePolicy.NetworkOnly)

  fun getHeroDetails(heroId: String): IO<CharacterDto> =
      HeroesRepository.getHeroDetails(HeroesRepository.CachePolicy.NetworkOnly, heroId)
}
