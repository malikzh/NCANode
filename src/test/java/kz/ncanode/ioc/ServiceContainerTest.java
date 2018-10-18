package kz.ncanode.ioc;

import org.junit.Test;

import static org.junit.Assert.*;

public class ServiceContainerTest {
    @Test
    public void boot() throws Exception {
        ServiceContainer sc = new ServiceContainer();
        sc.register(TestServiceProvider1.class.getName());
        sc.register(TestServiceProvider2.class.getName());
        sc.register(TestServiceProvider3.class.getName());
        sc.boot();

        TestServiceProvider1 a = (TestServiceProvider1)sc.instance(TestServiceProvider1.class.getName());
        TestServiceProvider2 b = (TestServiceProvider2)sc.instance(TestServiceProvider2.class.getName());
        TestServiceProvider3 c = (TestServiceProvider3)sc.instance(TestServiceProvider3.class.getName());

        // check for null
        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(c);

        // check DI
        assertTrue(a.b == b);
        assertTrue(b.c == c);
    }
}