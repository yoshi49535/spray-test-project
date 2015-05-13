package jp.co.o3.core.task

import scala._
import scala.concurrent._
import scala.collection.mutable.LinkedHashMap
import akka.actor.{Actor, Props}

import java.util.UUID

import jp.co.o3.core.task.TaskManager._

object TaskManager {

  case class CreateTask() 
  case class DeleteTask(id:UUID) 
  case class GetTask(id:UUID) 
  case class CompleteTask(id:UUID, result:Any) 
  case class FailTask(id:UUID, message:String) 
  
  case class Task(id:UUID, status:Option[TaskStatus] = None) 
  trait TaskStatus
  case class TaskSucceed(result:Any) extends TaskStatus 
  case class TaskFailed(message:String) extends TaskStatus 
}

class TaskManager extends Actor {
  this:Actor => 

  val maxTaskSize: Int = 1000
  val tasks:LinkedHashMap[UUID, Task] = new LinkedHashMap[UUID, Task]()

  // abstract method of generate TaskId 
  def generateId:UUID = {
    UUID.randomUUID
  }

  def tryPut(id:UUID , value:Option[TaskStatus]) {
    tasks.getOrElseUpdate(id, Task(id, value))
  }

  def receive = {
    case CreateTask => {
      //
      val id = generateId
      // create task
      val task = tryPut(id, None) 

      sender ! task
    }
    case CompleteTask(id, result) => {
      val task = tryPut(id, Some(TaskSucceed(result)))
    } 
    case FailTask(id, t) => {
      val task = tryPut(id, Some(TaskFailed(t)))

      sender ! task 
    }
  }
}
