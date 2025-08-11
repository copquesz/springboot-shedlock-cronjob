package br.com.mindmaster.shedlock.order.service;

import br.com.mindmaster.shedlock.order.entity.Order;
import br.com.mindmaster.shedlock.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProcessorService {

    private final OrderRepository repository;

    @Transactional
    public void executePendingOrders() {
        long start = System.nanoTime();
        List<Order> pendingOrders = repository.findPendingOrders();

        if (pendingOrders.isEmpty()) {
            log.info("No pending orders to process.");
            return;
        }

        log.debug("Starting pending orders processing. count={}", pendingOrders.size());

        int success = 0;
        int failed = 0;

        for (Order order : pendingOrders) {
            long startOne = System.nanoTime();
            try {
                order.process();
                repository.save(order);
                long duration = (System.nanoTime() - startOne) / 1_000_000;
                log.info("Order processed successfully. orderId={} durationMs={}", order.getId(), duration);
                success++;
            } catch (Exception ex) {
                long tookOneMs = (System.nanoTime() - startOne) / 1_000_000;
                log.warn("Failed to process order. orderId={} durationMs={}", order.getId(), tookOneMs, ex);
                failed++;
            }
        }

        long totalTime = (System.nanoTime() - start) / 1_000_000;
        log.info("Processing finished. total={} success={} failed={} durationMs={}",
                pendingOrders.size(), success, failed, totalTime);

    }
}
