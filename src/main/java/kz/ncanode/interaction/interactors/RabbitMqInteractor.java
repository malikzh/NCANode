package kz.ncanode.interaction.interactors;

import com.rabbitmq.client.ConnectionFactory;
import kz.ncanode.interaction.InteractionServiceProvider;
import kz.ncanode.interaction.Interactor;

public class RabbitMqInteractor implements Interactor {
    InteractionServiceProvider provider = null;

    public RabbitMqInteractor(InteractionServiceProvider provider) {
        this.provider = provider;
    }

    @Override
    public void interact() {
        // todo
    }
}
