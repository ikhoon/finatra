package com.twitter.finatra.thrift.internal.routing

import com.twitter.finagle.Service
import com.twitter.scrooge.ThriftService

case class Services(service: Option[Service[Array[Byte], Array[Byte]]], serviceIface: ThriftService)
