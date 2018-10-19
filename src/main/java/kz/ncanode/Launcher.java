package kz.ncanode;

import kz.ncanode.cmd.CmdServiceProvider;
import kz.ncanode.config.ConfigServiceProvider;
import kz.ncanode.ioc.ServiceContainer;
import kz.ncanode.log.ErrorLogServiceProvider;
import kz.ncanode.log.OutLogServiceProvider;
import kz.ncanode.log.RequestLogServiceProvider;

/**
 * Hello world!
 *
 */
public class Launcher
{
    public static String[] arguments = null;

    public static void main( String[] args )
    {
        arguments = args;

        ServiceContainer sc = new ServiceContainer();

        try {

            // Регистрация обработчика аргументов коммандной строки
            sc.register(CmdServiceProvider.class.getName());

            // Регистрация обработчиков логов
            sc.register(OutLogServiceProvider.class.getName()); // выводит в стандартный вывод
            sc.register(ErrorLogServiceProvider.class.getName()); // выводит в лог ошибок
            sc.register(RequestLogServiceProvider.class.getName()); // выводит в лог запросов

            // Регистрация обработчика конфигурации
            sc.register(ConfigServiceProvider.class.getName());

            // Загрузка всех сервис-провайдеров
            sc.boot();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
