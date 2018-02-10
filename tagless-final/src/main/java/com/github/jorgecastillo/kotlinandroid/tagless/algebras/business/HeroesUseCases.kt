package com.github.jorgecastillo.kotlinandroid.tagless.algebras.business

import arrow.Kind
import com.github.jorgecastillo.kotlinandroid.tagless.algebras.persistence.HeroesRepository
import com.karumi.marvelapiclient.model.CharacterDto
import javax.inject.Inject

class HeroesUseCases<F> @Inject constructor(val repository: HeroesRepository<F>) {

    fun getHeroes(): Kind<F, List<CharacterDto>> =
            repository.getHeroesWithCachePolicy(HeroesRepository.CachePolicy.NetworkOnly)

    fun getHeroDetails(heroId: String): Kind<F, CharacterDto> =
            repository.getHeroDetails(HeroesRepository.CachePolicy.NetworkOnly, heroId)


}
