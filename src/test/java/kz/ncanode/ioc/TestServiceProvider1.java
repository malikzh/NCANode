package kz.ncanode.ioc;

public class TestServiceProvider1 implements ServiceProvider {

    TestServiceProvider2 b;

    public TestServiceProvider1(TestServiceProvider2 b) {
        this.b = b;
    }
}
