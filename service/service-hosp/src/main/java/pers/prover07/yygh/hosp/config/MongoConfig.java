package pers.prover07.yygh.hosp.config;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import pers.prover07.yygh.model.hosp.Hospital;

/**
 * @Classname MongoConfig
 * @Description MongoDB 配置
 * @Date 2021/11/25 9:22
 * @Created by Prover07
 */
@Configuration
@AllArgsConstructor
public class MongoConfig {

    private MongoTemplate mongoTemplate;

    /**
     * 配置事件监听，创建索引
     */
    @EventListener(ApplicationEvent.class)
    public void initIndicesAfterStartup() {
        // 创建唯一索引
        mongoTemplate.indexOps(Hospital.class).ensureIndex(new Index("hoscode", Sort.Direction.DESC).unique());
        // 创建索引
        mongoTemplate.indexOps(Hospital.class).ensureIndex(new Index("hosname", Sort.Direction.DESC));
    }

}
