package kz.ncanode.ioc;

public class TestServiceProvider2 implements ServiceProvider {

    public TestServiceProvider3 c;

    public TestServiceProvider2(TestServiceProvider3 c) {
        this.c = c;
    }
}
