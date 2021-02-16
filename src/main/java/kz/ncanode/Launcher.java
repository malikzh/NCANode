package kz.ncanode;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.cmd.CmdServiceProvider;
import kz.ncanode.config.ConfigServiceProvider;
import kz.ncanode.info.InfoServiceProvider;
import kz.ncanode.interaction.InteractionServiceProvider;
import kz.ncanode.ioc.ServiceContainer;
import kz.ncanode.log.ErrorLogServiceProvider;
import kz.ncanode.log.OutLogServiceProvider;
import kz.ncanode.log.RequestLogServiceProvider;
import kz.ncanode.kalkan.KalkanServiceProvider;
import kz.ncanode.pki.*;

import java.util.Calendar;

public class Launcher
{
    public static String[] arguments = null;

    public static void main( String[] args )
    {
        // Shutdown handler
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                Launcher.shutdown();
            }
        }));

        // Intro
        System.out.print(Helper.awesomeBanner());
        System.out.printf("NCANode v%s\n", InfoServiceProvider.VERSION);
        System.out.printf("Official project page: %s\n", InfoServiceProvider.PROJECT_PAGE);
        System.out.printf("Please star ★ github repo: %s\n", InfoServiceProvider.GITHUB_PAGE);
        System.out.printf("Copyright (c) 2018-%d Malik Zharykov.\n", Calendar.getInstance().get(Calendar.YEAR));
        System.out.println("Memory info:");
        System.out.println(Helper.getMemoryInfo().toJSONString());
        System.out.println("------------------------------------");
        System.out.print("\n");

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

            // Регистрация провайдера калкан
            sc.register(KalkanServiceProvider.class.getName());

            // Регистрация хранилища доверенных и корневых сертификатов
            sc.register(CAStoreServiceProvider.class.getName());

            // Регистрация провайдера для работы с PKI
            sc.register(PkiServiceProvider.class.getName());

            // Регистрация провайдера для работы с TSP
            sc.register(TSPServiceProvider.class.getName());

            // Регистрация CRL провайдера
            sc.register(CrlServiceProvider.class.getName());

            // Регистрация подсистемы API
            sc.register(ApiServiceProvider.class.getName());

            // Регистрация класса информации о приложении
            sc.register(InfoServiceProvider.class.getName());

            // Регистрация interactor'а
            sc.register(InteractionServiceProvider.class.getName());

            // Загрузка всех сервис-провайдеров
            sc.boot();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // Поехали... :)
        ((InteractionServiceProvider)sc.instance(InteractionServiceProvider.class.getName())).start();
    }

    public static void shutdown() {
        System.out.print("\nNCANode stopped. Good bye :)\n");
    }
}
