package kz.ncanode;

import kz.ncanode.cmd.CmdServiceProvider;
import kz.ncanode.config.ConfigServiceProvider;
import kz.ncanode.ioc.ServiceContainer;
import kz.ncanode.log.ErrorLogServiceProvider;
import kz.ncanode.log.OutLogServiceProvider;
import kz.ncanode.log.RequestLogServiceProvider;
import kz.ncanode.kalkan.KalkanServiceProvider;
import kz.ncanode.pki.CAStoreServiceProvider;
import kz.ncanode.pki.X509Manager;

import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

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

            // Регистрация провайдера калкан
            sc.register(KalkanServiceProvider.class.getName());

            // Регистрация хранилища доверенных и корневых сертификатов
            sc.register(CAStoreServiceProvider.class.getName());

            // Загрузка всех сервис-провайдеров
            sc.boot();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }


        try {
            X509Certificate cert = X509Manager.load("ca/trusted/nca_rsa.crt");
            CAStoreServiceProvider cas = (CAStoreServiceProvider) sc.instance(CAStoreServiceProvider.class.getName());
            ArrayList<?> t = cas.chain(cert);
            t.size();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }
}
