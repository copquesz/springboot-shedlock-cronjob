package br.com.mindmaster.shedlock.order.job;

import br.com.mindmaster.shedlock.order.service.OrderGeneratorService;
import br.com.mindmaster.shedlock.order.service.OrderProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderJob {

    private final OrderGeneratorService generator;
    private final OrderProcessorService processor;

    @Scheduled(fixedRate = 1000)
    @SchedulerLock(name = "generateOrders", lockAtLeastFor = "1s", lockAtMostFor = "1m")
    public void generateOrders() {
        generator.execute();
    }

    @Scheduled(fixedRate = 3000)
    @SchedulerLock(name = "processPendingOrders", lockAtLeastFor = "10s", lockAtMostFor = "1m")
    public void processOrders() {
        processor.executePendingOrders();
    }
}
