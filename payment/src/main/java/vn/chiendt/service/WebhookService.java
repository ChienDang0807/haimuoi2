package vn.chiendt.service;


import com.stripe.model.Event;

public interface WebhookService {
    void handlePaymentIntentCreated(Event event);

    void handlePaymentIntentCanceled(Event event);

    void handlePaymentIntentSucceeded(Event event);

    void handlePaymentIntentFailed(Event event);
}
