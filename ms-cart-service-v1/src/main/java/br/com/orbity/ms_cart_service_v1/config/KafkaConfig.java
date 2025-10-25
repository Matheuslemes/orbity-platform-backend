package br.com.orbity.ms_cart_service_v1.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;

@Configuration
@ConditionalOnProperty(prefix = "cart.messaging", name = "enabled", havingValue = "true")
public class KafkaConfig {

    @Bean
    public NewTopic cartUpdatedTopic(CartProperties props) {
        return new NewTopic(props.getMessaging().getTopicUpdated(), 1, (short)1);
    }

    @Bean
    public NewTopic cartMergedTopic(CartProperties props) {
        return new NewTopic(props.getMessaging().getTopicMerged(), 1, (short)1);
    }

    @Bean
    public NewTopic cartCheckedOutTopic(CartProperties props) {
        return new NewTopic(props.getMessaging().getTopicCheckedOut(), 1, (short)1);
    }

}
