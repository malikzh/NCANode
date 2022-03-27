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

    private final HashMap<String, ServiceProvider> providers;
    private final ArrayList<String> dependenciesStack;

    public ServiceContainer() {
        providers = new HashMap<>();
        dependenciesStack = new ArrayList<>();
    }

    /**
     * Регистрирует ServiceProvider для этого контейнера.
     *
     * @param className Имя класса, который надо зарегистрировать
     * @throws ServiceProviderAlreadyExistsException Service Provider Already ExistsException
     * @throws ClassNotFoundException Class Not Found Exception
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
     * @return ServiceProvider
     */
    public ServiceProvider instance(String className) {
        return providers.get(className);
    }

    /**
     * Производит инициализацию всех сервис-провйдеров
     *
     * @throws ClassNotFoundException Class Not Found Exception
     * @throws NoSuchMethodException No Such Method Exception
     * @throws ProviderNotFoundException Provider Not Found Exception
     * @throws AmbiguousConstructorException Ambiguous Constructor Exception
     * @throws IllegalAccessException Illegal Access Exception
     * @throws InvocationTargetException Invocation Target Exception
     * @throws InstantiationException Instantiation Exception
     * @throws CircularDependencyException Circular Dependency Exception
     */
    public void boot() throws ClassNotFoundException, NoSuchMethodException, ProviderNotFoundException, AmbiguousConstructorException, IllegalAccessException, InvocationTargetException, InstantiationException, CircularDependencyException {
        for (Map.Entry<String, ServiceProvider> entry : providers.entrySet()) {
            createInstance(entry.getKey());
        }
    }

    private void createInstance(String className) throws ClassNotFoundException, AmbiguousConstructorException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, ProviderNotFoundException, CircularDependencyException {

        if (providers.get(className) != null) return;

        if (dependenciesStack.contains(className)) {
            throw new CircularDependencyException("Circular dependencies detected. Duplicating \"" + className + "\". Dependencies stack: " + dependenciesStack);
        }

        dependenciesStack.add(className);
        resolveDependencies(className);

        Class<?> provider = Class.forName(className);


        Constructor<?>[] constructors = provider.getDeclaredConstructors();

        if (constructors.length > 1) {
            throw new AmbiguousConstructorException("Provider \"" + className + "\" has more than one constructor");
        }

        if (constructors.length > 0) {
            Constructor<?> ctor = constructors[0];

            ArrayList<ServiceProvider> args = new ArrayList<>();

            for (Class<?> arg : ctor.getParameterTypes()) {
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

    private ArrayList<String> getProviderDependencies(String className) throws ProviderNotFoundException, ClassNotFoundException, AmbiguousConstructorException {

        if (!providers.containsKey(className)) {
            throw new ProviderNotFoundException("Provider \"" + className + "\" not found");
        }

        Class<?> providerClass = Class.forName(className);

        Constructor<?>[] constructors = providerClass.getDeclaredConstructors();

        if (constructors.length > 1) {
            throw new AmbiguousConstructorException("Provider \"" + className + "\" has more than one constructor");
        }

        ArrayList<String> deps = new ArrayList<>();

        if (constructors.length > 0) {
            Constructor<?> ctor = constructors[0];

            for (Class<?> dep : ctor.getParameterTypes()) {
                deps.add(dep.getName());
            }
        }

        return deps;
    }
}
