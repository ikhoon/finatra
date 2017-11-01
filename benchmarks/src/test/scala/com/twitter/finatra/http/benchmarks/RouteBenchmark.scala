package com.twitter.finatra.http.benchmarks

import com.twitter.finagle.Filter
import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.finatra.http.internal.routing.Route
import com.twitter.util.Future
import org.openjdk.jmh.annotations._

@State(Scope.Thread)
class RouteBenchmark {

  def defaultCallback(request: Request) = {
    Future(Response())
  }

  val route = Route(
    name = "groups",
    method = Method.Post,
    uri = "/groups/",
    admin = false,
    index = None,
    callback = defaultCallback,
    annotations = Seq(),
    requestClass = classOf[Request],
    responseClass = classOf[Response],
    routeFilter = Filter.identity,
    filter = Filter.identity
  )

  val routeWithPathParams = Route(
    name = "groups",
    method = Method.Post,
    uri = "/groups/:id",
    admin = false,
    index = None,
    callback = defaultCallback,
    annotations = Seq(),
    requestClass = classOf[Request],
    responseClass = classOf[Response],
    routeFilter = Filter.identity,
    filter = Filter.identity
  )

  val postGroupsPath = "/groups/"
  val postGroupsRequest = Request(Method.Post, postGroupsPath)

  val postGroups123Path = postGroupsPath + "123"
  val postGroups123Request = Request(Method.Post, postGroups123Path)

  @Benchmark
  def testRoute() = {
    route.handle(postGroupsRequest, postGroupsPath, bypassFilters = false)
  }

  @Benchmark
  def testRouteWithPathParams() = {
    routeWithPathParams.handle(postGroups123Request, postGroups123Path, bypassFilters = false)
  }
}
