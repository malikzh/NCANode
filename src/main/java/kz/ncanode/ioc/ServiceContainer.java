package kz.ncanode.ioc;

import kz.ncanode.ioc.exceptions.AmbiguousConstructorException;
import kz.ncanode.ioc.exceptions.CircularDependencyException;
import kz.ncanode.ioc.exceptions.ProviderNotFoundException;
import kz.ncanode.ioc.exceptions.ServiceProviderAlreadyExistsException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс реализует DI-контейнер.
 *
 * @author Malik Zharykov
 */
public class ServiceContainer {

    private HashMap<String, ServiceProvider> providers = null;
    private ArrayList<String> dependenciesStack;

    public ServiceContainer() {
        providers = new HashMap<>();
        dependenciesStack = new ArrayList<>();
    }

    /**
     * Регистрирует ServiceProvider для этого контейнера.
     *
     * @param className Имя класса, который надо зарегистрировать
     * @throws ServiceProviderAlreadyExistsException
     * @throws ClassNotFoundException
     */
    public void register(String className) throws ServiceProviderAlreadyExistsException, ClassNotFoundException {
        if (providers.containsKey(className)) {
            throw new ServiceProviderAlreadyExistsException("Service provider already exists");
        }

        Class.forName(className);

        providers.put(className, null);
    }

    /**
     * Возвращает экземпляр ServiceProvider
     *
     * @param className имя класса
     * @return
     */
    public ServiceProvider instance(String className) {
        return providers.get(className);
    }

    /**
     * Производит инициализацию всех сервис-провйдеров
     *
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws ProviderNotFoundException
     * @throws AmbiguousConstructorException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws CircularDependencyException
     */
    public void boot() throws ClassNotFoundException, NoSuchMethodException, ProviderNotFoundException, AmbiguousConstructorException, IllegalAccessException, InvocationTargetException, InstantiationException, CircularDependencyException {
        for (Map.Entry<String, ServiceProvider> entry : providers.entrySet()) {
            createInstance(entry.getKey());
        }
    }

    private void createInstance(String className) throws ClassNotFoundException, AmbiguousConstructorException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, ProviderNotFoundException, CircularDependencyException {

        if (providers.get(className) != null) return;

        if (dependenciesStack.contains(className)) {
            throw new CircularDependencyException("Circular dependencies detected. Duplicating \"" + className + "\". Dependencies stack: " + dependenciesStack.toString());
        }

        dependenciesStack.add(className);
        resolveDependencies(className);

        Class provider = Class.forName(className);


        Constructor[] ctors = provider.getDeclaredConstructors();

        if (ctors.length > 1) {
            throw new AmbiguousConstructorException("Provider \"" + className + "\" has more than one constructor");
        }

        if (ctors.length > 0) {
            Constructor ctor = ctors[0];

            ArrayList<ServiceProvider> args = new ArrayList<>();

            for (Class arg : ctor.getParameterTypes()) {
                ServiceProvider p = providers.get(arg.getName());
                args.add(p);
            }

            providers.put(className, (ServiceProvider)ctor.newInstance(args.toArray()));
        }

        dependenciesStack.remove(className);
    }

    private void resolveDependencies(String className) throws ClassNotFoundException, NoSuchMethodException, AmbiguousConstructorException, ProviderNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, CircularDependencyException {
        for (String dep : getProviderDependencies(className)) {
            if (providers.get(dep) != null) continue;
            createInstance(dep);
        }
    }

    private ArrayList<String> getProviderDependencies(String className) throws ProviderNotFoundException, ClassNotFoundException, NoSuchMethodException, AmbiguousConstructorException {

        if (!providers.containsKey(className)) {
            throw new ProviderNotFoundException("Provider \"" + className + "\" not found");
        }

        Class providerClass = Class.forName(className);

        Constructor[] ctors = providerClass.getDeclaredConstructors();

        if (ctors.length > 1) {
            throw new AmbiguousConstructorException("Provider \"" + className + "\" has more than one constructor");
        }

        ArrayList<String> deps = new ArrayList<>();

        if (ctors.length > 0) {
            Constructor ctor = ctors[0];

            for (Class dep : ctor.getParameterTypes()) {
                deps.add(dep.getName());
            }
        }

        return deps;
    }
}
