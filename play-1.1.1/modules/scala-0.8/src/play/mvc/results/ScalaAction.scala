package play.mvc.results

import play.mvc.ControllerDelegate
import play.mvc.Http
import play.mvc.Http

class ScalaAction(action: => Any) extends Result {

  val actionDefinition = ControllerDelegate.reverseForScala 
  
  action 
  
  val delegate = new Redirect(actionDefinition.url) 

  def apply(request: Http.Request , response:Http.Response) {
    delegate.apply(request, response)
  }
  
}
