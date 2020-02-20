package com.knoldus

import twitter4j._
import twitter4j.auth.AccessToken

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

trait TwitterInstance {

  def getTwitterInstance: Twitter = {
    val twitter = new TwitterFactory().getInstance

    val consumerKey = "e6uS4phTxImI68qlA6h4V3zwR"
    val consumerSecret = "M8b4Q3sudgU9mNZgJx1onUlqQYi5h5YCK1GVacjAc8yHDAohFc"
    val token = "160922224-AKOoOasbqi3huqT7uyq4Og0Oqlucn8rKeD9IcUvU"
    val tokenSecret = "7HgIJUmjOX2AZThvVp7RPWsZwOrW1ffpvkEpjeBSQynnH"

    twitter.setOAuthConsumer(consumerKey, consumerSecret)
    twitter.setOAuthAccessToken(new AccessToken(token, tokenSecret))

    twitter
  }
}

class TwitterFeed extends TwitterInstance {

  val twitter = getTwitterInstance

  def getFeeds(hashTag: String): Future[List[MyTweets]] = {
    Future {
      val query = new Query(hashTag)
      val list = twitter.search(query)
      println(list.getTweets.size())

      val tweets = list.getTweets.asScala.toList
      val allTweets = tweets.map {
        x => MyTweets(x.getText, x.getUser)
      }
      allTweets.sortBy(_.name)
    }
  }

  def getAverageLikes(hashtag: String): Future[Int] = {
    Future {
      val query = new Query(hashtag)
      val list = twitter.search(query)
      val tweets = list.getTweets.asScala.toList
      val likesCount = tweets.map(_.getFavoriteCount)
      likesCount.length / tweets.length
    }
  }

  def getAverageReTweets(hashtag: String): Future[Int] = {
    Future {
      val query = new Query(hashtag)
      val list = twitter.search(query)
      val tweets = list.getTweets.asScala.toList
      val reTweets = tweets.map(_.getRetweetCount)
      reTweets.size / tweets.size
    }
  }
}

object TwitterFeed extends App {

  val twitterFeed = new TwitterFeed


  val query = "#CAA"

  val status = twitterFeed.getFeeds(query)

  val avgLikes = twitterFeed.getAverageLikes(query)
  val avgRetweets = twitterFeed.getAverageReTweets(query)
  val res = Await.result(status, 10.second)
  val result = Await.result(avgLikes, 12.seconds)
  val result1 = Await.result(avgRetweets, 14.seconds)
  println("average likes: " + result)
  println("avg retweets :" + result1)


  println("number of tweets : " + res)


  //  res.map(r => println(r.name))
}