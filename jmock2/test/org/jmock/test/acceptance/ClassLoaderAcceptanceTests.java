/*  Copyright (c) 2000-2004 jMock.org
 */
package org.jmock.test.acceptance;

import junit.framework.TestCase;

import org.jmock.InAnyOrder;
import org.jmock.Mockery;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Constants;


public class ClassLoaderAcceptanceTests extends TestCase {
    static class EmptyInterfaceCreator extends ClassLoader {
        protected Class<?> findClass( String name ) {
            ClassWriter writer = new ClassWriter(true);
            writer.visit(Constants.ACC_PUBLIC | Constants.ACC_INTERFACE,
                         name.replace('.', '/'),
                         "java/lang/Object",
                         null, /* interfaces */
                         null /* source file */);
            
            byte[] b = writer.toByteArray();

            return defineClass(name, b, 0, b.length);
        }
    }
    
    Mockery context = new Mockery();
    
    public void testMockingTypeFromOtherClassLoader() throws ClassNotFoundException {
        ClassLoader interfaceClassLoader = new EmptyInterfaceCreator();
        Class<?> interfaceClass = interfaceClassLoader.loadClass("$UniqueTypeName$");
        
        final Object o = context.mock(interfaceClass, "o");
        
        context.expects(new InAnyOrder() {{
            allow (o);
        }});
        
        o.toString();
        
        context.assertIsSatisfied();
    }
}