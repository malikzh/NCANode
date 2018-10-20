package kz.ncanode;

import kz.ncanode.cmd.CmdServiceProvider;
import kz.ncanode.config.ConfigServiceProvider;
import kz.ncanode.ioc.ServiceContainer;
import kz.ncanode.log.ErrorLogServiceProvider;
import kz.ncanode.log.OutLogServiceProvider;
import kz.ncanode.log.RequestLogServiceProvider;
import kz.ncanode.kalkan.KalkanServiceProvider;
import kz.ncanode.pki.CAStoreServiceProvider;
import kz.ncanode.pki.OCSPStatus;
import kz.ncanode.pki.PkiServiceProvider;
import kz.ncanode.pki.X509Manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;

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

            // Регистрация провайдера для работы с PKI
            sc.register(PkiServiceProvider.class.getName());


            // Загрузка всех сервис-провайдеров
            sc.boot();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }


        try {
            PkiServiceProvider pki = ((PkiServiceProvider)sc.instance(PkiServiceProvider.class.getName()));
            X509Certificate cert = X509Manager.load("ca/trusted/nca_rsa.crt");
            X509Certificate cert2 = X509Manager.load("ca/root/root_rsa.crt");
            CAStoreServiceProvider cas = (CAStoreServiceProvider) sc.instance(CAStoreServiceProvider.class.getName());
            ArrayList<?> t = cas.chain(cert);
            t.size();

            KeyStore ks = pki.loadKey("src/test/resources/individual_valid/GOSTKNCA_f8a422238209b6e753116431b69b09178f14e9d7.p12", "Qwerty12");

            // test ocsp
            Enumeration<String> als = ks.aliases();
            String alias = null;
            while (als.hasMoreElements()) {
                alias = als.nextElement();
            }

            // cert2 в цепочке должны совпадать

            OCSPStatus st = pki.verifyOcsp((X509Certificate)ks.getCertificate(alias), cert);
            System.out.println(st);

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
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
