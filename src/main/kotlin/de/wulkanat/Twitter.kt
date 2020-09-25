package de.wulkanat

import blue.starry.penicillin.PenicillinClient
import blue.starry.penicillin.core.session.ApiClient
import blue.starry.penicillin.core.session.config.account
import blue.starry.penicillin.core.session.config.token
import blue.starry.penicillin.endpoints.accountActivity
import blue.starry.penicillin.endpoints.accountactivity.subscribe
import blue.starry.penicillin.extensions.queue
import kotlinx.serialization.json.Json

object Twitter {
    private val twitterFile: TwitterFile = Json { allowStructuredMapKeys = true }.decodeFromString(
        TwitterFile.serializer(),
        TWITTER_FILE.readText()
    )
    private val apiKey get() = twitterFile.apiKey
    private val apiSecretKey get() = twitterFile.apiSecretKey
    private val bearerToken get() = twitterFile.bearerToken
    private val accessToken get() = twitterFile.accessToken
    private val accessTokenSecret get() = twitterFile.accessTokenSecret
    private val env get() = twitterFile.env

    val api: ApiClient = PenicillinClient {
        account {
            token(Twitter.accessToken, Twitter.accessTokenSecret)
        }
    }

    init {
        api.accountActivity.subscribe(env).queue()
        api.
    }
}