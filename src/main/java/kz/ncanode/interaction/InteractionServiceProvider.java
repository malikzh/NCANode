package kz.ncanode.interaction;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.config.ConfigServiceProvider;
import kz.ncanode.info.InfoServiceProvider;
import kz.ncanode.interaction.interactors.HttpInteractor;
import kz.ncanode.interaction.interactors.RabbitMqInteractor;
import kz.ncanode.ioc.ServiceProvider;
import kz.ncanode.log.ErrorLogServiceProvider;
import kz.ncanode.log.OutLogServiceProvider;

import java.util.Hashtable;

/**
 * Класс-менеджер для управления методами взаимодействия с API (HTTP, RabbitMQ)
 */
public class InteractionServiceProvider implements ServiceProvider {

    public ConfigServiceProvider config;
    public OutLogServiceProvider out;
    public ApiServiceProvider    api;
    public InfoServiceProvider info;
    public ErrorLogServiceProvider err;

    Hashtable<String, Interactor> interactors;

    public InteractionServiceProvider(ConfigServiceProvider config, OutLogServiceProvider out, ApiServiceProvider api, InfoServiceProvider info, ErrorLogServiceProvider err) {
        this.config = config;
        this.out    = out;
        this.api    = api;
        this.info   = info;
        this.err    = err;

        interactors = new Hashtable<>();
        interactors.put("http", new HttpInteractor(this));
        interactors.put("rabbitmq", new RabbitMqInteractor(this));
    }

    public void start() {
        String interactorName = config.get("main", "mode");

        if (interactorName == null || interactorName.isEmpty()) {
            out.write("Interaction mode not specified... Aborting...");
            System.exit(-1);
        }

        out.write("Running in \"" + interactorName + "\" mode...");

        Interactor interactor = interactors.get(interactorName);

        if (interactor == null) {
            out.write("Unknwown interaction mode: '" + interactorName + "'. Aborting...");
        }

        interactor.interact();
    }
}
