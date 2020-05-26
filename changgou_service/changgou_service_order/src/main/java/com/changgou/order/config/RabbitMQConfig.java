package com.changgou.order.config;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhangHuan
 * @date: 2020/05/14/21:00
 * @Description: RabbitMQ配置类
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 定义广告队列的名称
     */
    public static final String AD_UPDATE_QUEUE = "ad_update_queue";

    /**
     * 定义上架后缓从商品详情页面的队列名称
     */
    public static final String PAGE_CACHE_QUEUE = "page_cache_queue";

    /**
     * 定义商品上架队列名称
     */
    public static final String SEARCH_ADD_QUEUE = "search_add_queue";

    /**
     * 定义商品下架队列名称
     */
    public static final String SEARCH_DOWN_QUEUE = "search_down_queue";

    /**
     * 定义异常队列的名称
     */
    public static final String ERROR_MESSAGE_QUEEN = "error_message";


    /**
     * 商品上架交互机
     */
    public static final String GOODS_UP_EXCHANGE = "goods_up_exchange";

    /**
     * 商品上架交换机名称
     */
    public static final String AD_UPDATE_EXCHANGE = "ad_update_exchange";

    /**
     * 商品下架交换机名称
     */
    public static final String GOODS_DOWN_EXCHANGE = "goods_down_exchange";

    /**
     * 支付成功队列的名称
     */
    public static final String ORDER_PAY="order_pay";


    @Bean(ORDER_PAY)
    public Queue queue(){
        return  new Queue(ORDER_PAY);
    }


    @Bean(AD_UPDATE_QUEUE)
    public Queue AD_UPDATE_QUEUE() {
        return new Queue(AD_UPDATE_QUEUE);
    }

    @Bean(ERROR_MESSAGE_QUEEN)
    public Queue ERROR_MESSAGE_QUEEN() {
        return new Queue(ERROR_MESSAGE_QUEEN);
    }

    @Bean(SEARCH_ADD_QUEUE)
    public Queue SEARCH_ADD_QUEUE() {
        return new Queue(SEARCH_ADD_QUEUE);
    }

    @Bean(SEARCH_DOWN_QUEUE)
    public Queue SEARCH_DOWN_QUEUE() {
        return new Queue(SEARCH_DOWN_QUEUE);
    }


    @Bean(PAGE_CACHE_QUEUE)
    public Queue PAGE_CACHE_QUEUE() {
        return new Queue(PAGE_CACHE_QUEUE);
    }

    /**
     * 定义广告更新的交互机
     *
     * @return 广告更新的交互机
     */
    @Bean(AD_UPDATE_EXCHANGE)
    public Exchange AD_UPDATE_EXCHANGE() {
        return ExchangeBuilder.directExchange(AD_UPDATE_EXCHANGE).durable(true).build();
    }

    /**
     * 定義商品上架交互機
     *
     * @return 商品上架交互機
     */
    @Bean(GOODS_UP_EXCHANGE)
    public Exchange GOODS_UP_EXCHANGE() {
        return ExchangeBuilder.fanoutExchange(GOODS_UP_EXCHANGE).durable(true).build();
    }

    /**
     * 定義商品下架交互機
     *
     * @return 商品下架交互機
     */
    @Bean(GOODS_DOWN_EXCHANGE)
    public Exchange GOODS_DOWN_EXCHANGE() {
        return ExchangeBuilder.fanoutExchange(GOODS_DOWN_EXCHANGE).durable(true).build();
    }

    /**
     * 绑定广告更新的交互机和队列关系
     *
     * @param queue
     * @param exchange
     * @return Binding
     */
    @Bean
    public Binding AD_UPDATE_QUEUE_BINDING(@Qualifier(AD_UPDATE_QUEUE) Queue queue, @Qualifier(AD_UPDATE_EXCHANGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("ad_update").noargs();
    }

    /**
     * 绑定商品上架的交互机和队列
     *
     * @param queue
     * @param exchange
     * @return Binding
     */
    @Bean
    public Binding SEARCH_ADD_QUEUE_BINDING(@Qualifier(SEARCH_ADD_QUEUE) Queue queue, @Qualifier(GOODS_UP_EXCHANGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }


    /**
     * 绑定商品下架的交互机和队列
     *
     * @param queue
     * @param exchange
     * @return Binding
     */
    @Bean
    public Binding SEARCH_DOWN_QUEUE_BINDING(@Qualifier(SEARCH_DOWN_QUEUE) Queue queue, @Qualifier(GOODS_DOWN_EXCHANGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }

    /**
     * 绑定商品上架后缓从商品详情页面的队列和交互机
     *
     * @param queue
     * @param exchange
     * @return Binding
     */
    @Bean
    public Binding PAGE_CACHE_QUEUE_BINDING(@Qualifier(PAGE_CACHE_QUEUE) Queue queue, @Qualifier(GOODS_UP_EXCHANGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }
}

