package ecommerce;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrder {
    public static void main (String[] args) throws ExecutionException, InterruptedException {
        try(var dispatcher = new KafkaDispatcher())
        {
            for(var i = 0; i <  10; i++){
                var key = UUID.randomUUID().toString();

                var value = key + "31,37";
                dispatcher.send("ECOMMERCE_NEW_ORDER", key, value);

                var email = "Thank you for your order! We are processing your order!";
                dispatcher.send("ECOMMERCE_NEW_ORDER", key, email);
            }
        }
    }
}
