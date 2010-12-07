package org.exoplatform.services.rest.ext.filter;

import junit.framework.TestCase;

import org.exoplatform.common.util.HierarchicalProperty;
import org.exoplatform.services.rest.ext.provider.HierarchicalPropertyEntityProvider;

import java.io.ByteArrayInputStream;

public class HierarchicalPropertyEntityProviderTest extends TestCase
{
   public void testRequestBodyXMLParsing() throws Exception
   {
      String s = "\n<root>\n   <l1>\n\t<l2>hel\nlo</l2>\n  </l1>  \n</root>\n";
      System.out.println(s);
      HierarchicalProperty hp =
         new HierarchicalPropertyEntityProvider().readFrom(HierarchicalProperty.class, null, null, null, null,
            new ByteArrayInputStream(s.getBytes()));
      assertTrue(hp.getChild(0).getChild(0).getValue().equals("hel\nlo"));
   }
}