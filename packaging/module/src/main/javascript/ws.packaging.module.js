eXo.require("eXo.projects.Module") ;
eXo.require("eXo.projects.Product") ;

function getModule(params) {
  var module = new Module();

  module.version = "${project.version}" ;//
  module.relativeMavenRepo =  "org/exoplatform/ws" ;
  module.relativeSRCRepo =  "ws/trunk" ;
  module.name =  "ws" ;

  module.commons = 
    new Project("org.exoplatform.ws", "exo.ws.commons", "jar", module.version);
    
	module.soap_cxf_jsr181 = 
    new Project("org.exoplatform.ws", "exo.ws.soap.cxf.jsr181", "jar", module.version).
    addDependency(new Project("org.apache.cxf", "cxf-rt-transports-http", "jar", "2.1.2")) ;
  
  module.frameworks = {};
  module.frameworks.servlet = 
    new Project("org.exoplatform.ws", "exo.ws.frameworks.servlet", "jar", module.version)
    //.addDependency(new Project("javax.servlet", "servlet-api", "jar", "2.4"));  

  module.frameworks.json = 
    new Project("org.exoplatform.ws", "exo.ws.frameworks.json", "jar", module.version);

  module.frameworks.cometd =
	new Project("org.exoplatform.ws", "exo.ws.frameworks.cometd.webapp", "war", module.version).
    addDependency(new Project("org.mortbay.jetty", "cometd-bayeux", "jar", "6.1.11")).
	addDependency(new Project("org.mortbay.jetty", "jetty-util", "jar", "6.1.11")).
	addDependency(new Project("org.mortbay.jetty", "cometd-api", "jar", "0.9.20080221")).
	addDependency(new Project("org.exoplatform.ws", "exo.ws.frameworks.cometd.service", "jar", module.version));  

  module.rest = 
    new Project("org.exoplatform.ws", "exo.ws.rest.core", "jar", module.version).      
    addDependency(module.commons).
    addDependency(module.frameworks.json).
    addDependency(new Project("org.exoplatform.ws", "exo.ws.rest.ext", "jar", module.version)).      
    addDependency(new Project("javax.annotation", "jsr250-api", "jar", "1.0")).
    addDependency(new Project("javax.ws.rs", "jsr311-api", "jar", "1.0")).
    addDependency(new Project("commons-chain", "commons-chain", "jar", "1.0")) .
//    addDependency(new Project("javax.xml.parsers", "jaxp-api", "jar", "1.4")) .
    addDependency(new Project("javax.xml.bind", "jaxb-api", "jar", "2.1"));
  //addDependency(new Project("com.sun.xml.bind", "jaxb-impl", "jar", "2.1.7")) .
//    addDependency(new Project("com.sun.xml.parsers", "jaxp-ri", "jar", "1.4")) .
  //addDependency(new Project("org.jvnet.jaxb2.maven2", "maven-jaxb2-plugin", "jar", "0.1"));
          
  module.soap = {};
  module.soap.jsr181 =
    new Project("org.exoplatform.ws", "exo.ws.soap.xfire.jsr181", "jar", module.version).
    addDependency(new Project("picocontainer", "picocontainer", "jar", "1.1")) .
    addDependency(new Project("org.codehaus.xfire", "xfire-jsr181-api", "jar", "1.0")) .
    addDependency(new Project("org.codehaus.xfire", "xfire-all", "jar", "1.2.6")) .
 // addDependency(new Project("stax", "stax-api", "jar", "1.0")) .
    addDependency(new Project("wsdl4j", "wsdl4j", "jar", "1.6.1")) .
    addDependency(new Project("jdom", "jdom", "jar", "1.0"));  
          
  return module;
}
