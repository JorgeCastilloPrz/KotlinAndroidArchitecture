package com.github.jorgecastillo.kotlinandroid.io.algebras.ui

import android.content.Context
import arrow.Kind
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.getNewsItemDetails
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.getNews
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.DomainError
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.NewsItem
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model.NewsItemViewState
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.Runtime

interface NewsView {

    suspend fun showLoading(): Unit

    suspend fun hideLoading(): Unit

    suspend fun showNotFoundError(): Unit

    suspend fun showGenericError(): Unit

    suspend fun showAuthenticationError(): Unit
}

interface NewsListView : NewsView {

    suspend fun drawNews(news: List<NewsItemViewState>): Unit
}

interface NewsItemDetailView : NewsView {

    suspend fun drawNewsItem(newsItem: NewsItemViewState)
}

/**
 * On tagless-final module we built this operations over abstract behaviors defined on top of an F
 * type. We'll end up running these methods using a valid F type that support Concurrent behaviors,
 * like IO.
 */
fun <F> Runtime<F>.onNewsItemClick(
    ctx: Context,
    title: String
): Kind<F, Unit> =
    goToNewsItemDetail(ctx, title)

private suspend fun displayErrors(
    view: NewsView,
    t: Throwable
): Unit {
    when (DomainError.fromThrowable(t)) {
        is DomainError.NotFoundError -> view.showNotFoundError()
        is DomainError.UnknownServerError -> view.showGenericError()
        is DomainError.AuthenticationError -> view.showAuthenticationError()
    }
}

fun <F> Runtime<F>.getAllNews(view: NewsListView): Kind<F, Unit> = fx.concurrent {
    !effect { view.showLoading() }
    val maybeNews = !getNews().attempt()
    !effect { view.hideLoading() }
    !effect {
        maybeNews.fold(
            ifLeft = { displayErrors(view, it) },
            ifRight = { view.drawNews(it.map { newsItem -> newsItem.toViewState() }) }
        )
    }
}

fun <F> Runtime<F>.getNewsItemDetails(
    title: String,
    view: NewsItemDetailView
): Kind<F, Unit> = fx.concurrent {
    !effect { view.showLoading() }
    val maybeNewsItem = !getNewsItemDetails(title).attempt()
    !effect { view.hideLoading() }
    !effect {
        maybeNewsItem.fold(
            ifLeft = { displayErrors(view, it) },
            ifRight = { newsItem -> view.drawNewsItem(newsItem.toViewState()) }
        )
    }
}

fun NewsItem.toViewState() = NewsItemViewState(
    title = title,
    author = author,
    photoUrl = urlToImage,
    publishedAt = publishedAt,
    description = description,
    content = content
)
