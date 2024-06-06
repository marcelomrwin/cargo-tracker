package org.eclipse.cargotracker.infrastructure.messaging.jms;


import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import org.slf4j.Logger;

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "jakarta.jms.Queue"),
                @ActivationConfigProperty(
                        propertyName = "destinationLookup",
                        propertyValue = "java:app/jms/RejectedRegistrationAttemptsQueue")
        })
public class RejectedRegistrationAttemptsConsumer implements MessageListener {

    @Inject
    private Logger logger;

    @Override
    public void onMessage(Message message) {
        try {
            logger.info(

                    "Rejected registration attempt of cargo with tracking ID {}.",
                    message.getBody(String.class));
        } catch (JMSException ex) {
            logger.warn("Error processing message.", ex);
        }
    }
}
