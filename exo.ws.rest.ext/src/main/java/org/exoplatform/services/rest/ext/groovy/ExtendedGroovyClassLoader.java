/*
 * Copyright (C) 2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.services.rest.ext.groovy;

import groovy.lang.GroovyClassLoader;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.control.SourceUnit;
import org.exoplatform.commons.utils.SecurityHelper;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: ExtendedGroovyClassLoader.java 3731 2010-12-27 13:35:46Z
 *          aparfonov $
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ExtendedGroovyClassLoader extends GroovyClassLoader
{
   public static final String CODE_BASE = "/groovy/script/jaxrs";

   public static class SingleClassCollector extends GroovyClassLoader.ClassCollector
   {
      protected final CompilationUnit cunit;
      protected final SourceUnit sunit;
      protected Class target;

      protected SingleClassCollector(ExtendedInnerLoader cl, CompilationUnit cunit, SourceUnit sunit)
      {
         super(cl, cunit, sunit);
         this.cunit = cunit;
         this.sunit = sunit;
      }

      @Override
      protected Class createClass(byte[] code, ClassNode classNode)
      {
         ExtendedInnerLoader cl = (ExtendedInnerLoader)getDefiningClassLoader();
         /*String classname = classNode.getName();
         int i = classname.lastIndexOf('.');
         if (i != -1)
         {
            String pkgname = classname.substring(0, i);
            cl.definePackage(pkgname);
         }*/
         Class clazz = cl.defineClass(classNode.getName(), code, cunit.getAST().getCodeSource());
         getLoadedClasses().add(clazz);
         if (target == null)
         {
            ClassNode targetClassNode = null;
            SourceUnit targetSunit = null;
            ModuleNode module = classNode.getModule();

            if (module != null)
            {
               targetClassNode = (ClassNode)module.getClasses().get(0);
               targetSunit = module.getContext();
            }

            if (targetSunit == sunit && targetClassNode == classNode) //NOSONAR
            {
               target = clazz;
            }
         }
         return clazz;
      }

      public Class getTarget()
      {
         return target;
      }
   }

   public static class MultipleClassCollector extends GroovyClassLoader.ClassCollector
   {
      protected final CompilationUnit cunit;
      protected final Set<SourceUnit> sunitSet;
      private final List<Class> compiledClasses;

      protected MultipleClassCollector(ExtendedInnerLoader cl, CompilationUnit cunit, Set<SourceUnit> sunitSet)
      {
         super(cl, cunit, null);
         this.cunit = cunit;
         this.sunitSet = sunitSet;
         this.compiledClasses = new ArrayList<Class>();
      }

      @Override
      protected Class createClass(byte[] code, ClassNode classNode)
      {
         ExtendedInnerLoader cl = (ExtendedInnerLoader)getDefiningClassLoader();
         /*String classname = classNode.getName();
         int i = classname.lastIndexOf('.');
         if (i != -1)
         {
            String pkgname = classname.substring(0, i);
            cl.definePackage(pkgname);
         }*/
         Class clazz = cl.defineClass(classNode.getName(), code, cunit.getAST().getCodeSource());
         getLoadedClasses().add(clazz);
         ModuleNode module = classNode.getModule();
         if (module != null)
         {
            SourceUnit currentSunit = module.getContext();
            if (sunitSet.contains(currentSunit))
               compiledClasses.add(clazz);
         }
         return clazz;
      }

      public List<Class> getCompiledClasses()
      {
         return compiledClasses;
      }
   }

   public static class ExtendedInnerLoader extends GroovyClassLoader.InnerLoader
   {
      public ExtendedInnerLoader(ExtendedGroovyClassLoader parent)
      {
         super(parent);
      }

      protected Class defineClass(String name, byte[] code, CodeSource cs)
      {
         return super.defineClass(name, code, 0, code.length, cs);
      }

      protected void definePackage(String name) throws IllegalArgumentException
      {
         Package pkg = getPackage(name);
         if (pkg == null)
            super.definePackage(name, null, null, null, null, null, null, null);
      }
   }

   public ExtendedGroovyClassLoader(ClassLoader classLoader)
   {
      super(classLoader);
   }

   public ExtendedGroovyClassLoader(GroovyClassLoader parent)
   {
      super(parent);
   }

   public Class parseClass(InputStream in, String fileName, SourceFile[] files) throws CompilationFailedException
   {
      return doParseClass(in, fileName, files, Phases.CLASS_GENERATION, null, false);
   }

   protected Class doParseClass(InputStream in, String fileName, SourceFile[] files, int phase,
      CompilerConfiguration config, boolean shouldCacheSource) throws CompilationFailedException
   {
      synchronized (sourceCache)
      {
         Class target = (Class)sourceCache.get(fileName);
         if (target == null)
         {
            CodeSource cs = new CodeSource(getCodeSource(), (java.security.cert.Certificate[])null);
            CompilationUnit cunit = createCompilationUnit(config, cs);
            SourceUnit targetSunit = cunit.addSource(fileName, in);
            if (files != null)
            {
               for (int i = 0; i < files.length; i++)
                  cunit.addSource(files[i].getPath());
            }
            SingleClassCollector collector = createSingleCollector(cunit, targetSunit);
            cunit.setClassgenCallback(collector);
            cunit.compile(phase);

            for (Iterator iter = collector.getLoadedClasses().iterator(); iter.hasNext();)
            {
               Class clazz = (Class)iter.next();
               String classname = clazz.getName();
               int i = classname.lastIndexOf('.');
               if (i != -1)
               {
                  String pkgname = classname.substring(0, i);
                  Package pkg = getPackage(pkgname);
                  if (pkg == null)
                     definePackage(pkgname, null, null, null, null, null, null, null);
               }
               setClassCacheEntry(clazz);
            }

            target = collector.getTarget();

            if (shouldCacheSource)
               sourceCache.put(fileName, target);
         }

         return target;
      }
   }

   public Class[] parseClasses(SourceFile[] files)
   {
      return doParseClasses(files, Phases.CLASS_GENERATION, null);
   }

   protected Class[] doParseClasses(SourceFile[] sources, int phase, CompilerConfiguration config)
   {
      synchronized (classCache)
      {
         CodeSource cs = new CodeSource(getCodeSource(), (java.security.cert.Certificate[])null);
         CompilationUnit cunit = createCompilationUnit(config, cs);
         Set<SourceUnit> setSunit = new HashSet<SourceUnit>();
         for (int i = 0; i < sources.length; i++)
            setSunit.add(cunit.addSource(sources[i].getPath()));
         MultipleClassCollector collector = createMultipleCollector(cunit, setSunit);
         cunit.setClassgenCallback(collector);
         cunit.compile(phase);

         for (Iterator iter = collector.getLoadedClasses().iterator(); iter.hasNext();)
         {
            Class clazz = (Class)iter.next();
            String classname = clazz.getName();
            int i = classname.lastIndexOf('.');
            if (i != -1)
            {
               String pkgname = classname.substring(0, i);
               Package pkg = getPackage(pkgname);
               if (pkg == null)
                  definePackage(pkgname, null, null, null, null, null, null, null);
            }
            setClassCacheEntry(clazz);
         }
         List<Class> compiledClasses = collector.getCompiledClasses();
         return compiledClasses.toArray(new Class[compiledClasses.size()]);
      }
   }

   /**
    * @see groovy.lang.GroovyClassLoader#createCompilationUnit(org.codehaus.groovy.control.CompilerConfiguration,
    *      java.security.CodeSource)
    */
   @Override
   protected CompilationUnit createCompilationUnit(CompilerConfiguration config, CodeSource cs)
   {
      return new CompilationUnit(config, cs, this);
   }

   protected SingleClassCollector createSingleCollector(CompilationUnit unit, SourceUnit sunit)
   {
      ExtendedInnerLoader loader = SecurityHelper.doPrivilegedAction(new PrivilegedAction<ExtendedInnerLoader>() {
         public ExtendedInnerLoader run()
         {
            return new ExtendedInnerLoader(ExtendedGroovyClassLoader.this);
         }
      });
      return new SingleClassCollector(loader, unit, sunit);
   }

   protected MultipleClassCollector createMultipleCollector(CompilationUnit unit, Set<SourceUnit> setSunit)
   {
      ExtendedInnerLoader loader = SecurityHelper.doPrivilegedAction(new PrivilegedAction<ExtendedInnerLoader>() {
         public ExtendedInnerLoader run()
         {
            return new ExtendedInnerLoader(ExtendedGroovyClassLoader.this);
         }
      });
      return new MultipleClassCollector(loader, unit, setSunit);
   }

   protected URL getCodeSource()
   {
      return getCodeSource(CODE_BASE);
   }

   private URL getCodeSource(String codeBase)
   {
      try
      {
         return new URL("file", "", codeBase);
      }
      catch (MalformedURLException e)
      {
         throw new IllegalArgumentException("Unable create code source URL from: " + codeBase + ". " + e.getMessage());
      }
   }
}