import org.exoplatform.services.rest.ext.groovy.GroovyExoComponentTest.Component1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("a")
class CroovyResource2
{
   private Component1 component
   
   CroovyResource2(Component1 component)
   {
      this.component = component
   }
   
   @GET
   @Path("b")
   def m0()
   {
      return component.getName()  
   }
   
   
}