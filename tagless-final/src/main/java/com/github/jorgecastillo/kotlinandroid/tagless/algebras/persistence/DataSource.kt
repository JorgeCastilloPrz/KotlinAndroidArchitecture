package com.github.jorgecastillo.kotlinandroid.tagless.algebras.persistence

import arrow.HK
import arrow.TC
import arrow.data.Try
import arrow.effects.AsyncContext
import arrow.syntax.either.right
import arrow.typeclass
import arrow.typeclasses.MonadError
import arrow.typeclasses.binding
import com.github.jorgecastillo.kotlinandroid.BuildConfig
import com.karumi.marvelapiclient.CharacterApiClient
import com.karumi.marvelapiclient.MarvelApiConfig
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

@typeclass
interface DataSource<F> : TC {

    fun ME(): MonadError<F, Throwable>

    fun AC(): AsyncContext<F>

    private val apiClient
        get() = CharacterApiClient(MarvelApiConfig.Builder(
                BuildConfig.MARVEL_PUBLIC_KEY,
                BuildConfig.MARVEL_PRIVATE_KEY).debug().build())

    private fun buildFetchHeroesQuery(): CharactersQuery =
            CharactersQuery.Builder.create().withOffset(0).withLimit(50).build()

    private fun fetchHero(heroId: String) =
            apiClient.getCharacter(heroId).response

    private fun fetchHeroes(query: CharactersQuery): List<CharacterDto> =
            apiClient.getAll(query).response.characters

    private fun <F, A, B> runInAsyncContext(
            f: () -> A,
            onError: (Throwable) -> B,
            onSuccess: (A) -> B, AC: AsyncContext<F>): HK<F, B> {
        return AC.runAsync { proc ->
            async(CommonPool) {
                val result = Try { f() }.fold(onError, onSuccess)
                proc(result.right())
            }
        }
    }

    fun fetchAllHeroes(): HK<F, List<CharacterDto>> =
        ME().binding {
            val query = buildFetchHeroesQuery()
            runInAsyncContext(
                    f = { fetchHeroes(query) },
                    onError = { ME().raiseError<List<CharacterDto>>(it) },
                    onSuccess = { ME().pure(it) },
                    AC = AC()
            ).bind()
        }


    fun fetchHeroDetails(heroId: String): HK<F, CharacterDto> =
            ME().binding {
                runInAsyncContext(
                        f = { fetchHero(heroId) },
                        onError = { ME().raiseError<CharacterDto>(it) },
                        onSuccess = { ME().pure(it) },
                        AC = AC()
                ).bind()
            }
}