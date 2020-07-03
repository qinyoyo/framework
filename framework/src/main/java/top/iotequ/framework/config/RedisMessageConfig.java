package top.iotequ.framework.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import top.iotequ.framework.event.SystemParameterChangedEvent;

@Configuration
@ConditionalOnProperty(value="spring.redis.url")
public class RedisMessageConfig {


    @Bean
    RedisMessageListenerContainer redisMessageContainer(RedisConnectionFactory connectionFactory,
                                                        MessageListenerAdapter redisMessageListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(redisMessageListenerAdapter, new PatternTopic(SystemParameterChangedEvent.SP_DATA_DICT));
        container.addMessageListener(redisMessageListenerAdapter, new PatternTopic(SystemParameterChangedEvent.SP_ORG_RELOAD));
        container.addMessageListener(redisMessageListenerAdapter, new PatternTopic(SystemParameterChangedEvent.SP_PERMISSION_INITIAL));
        return container;
    }

    @Bean
    MessageListenerAdapter redisMessageListenerAdapter(RedisMessageReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
    @Bean
    StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
    public static void setRedisMessage(String topic,Object message) {

    }
}
