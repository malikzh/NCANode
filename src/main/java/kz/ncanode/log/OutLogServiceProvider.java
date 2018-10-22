package kz.ncanode.log;

import kz.ncanode.ioc.ServiceProvider;

/**
 * Простой класс лога, который выводит в стандартный вывод
 */
public class OutLogServiceProvider extends Log implements ServiceProvider {
    public OutLogServiceProvider() {
        super(System.out);
        getPrintStream().needPrintTime = false; // Выключаем добавление даты в стандартный вывод
    }
}
