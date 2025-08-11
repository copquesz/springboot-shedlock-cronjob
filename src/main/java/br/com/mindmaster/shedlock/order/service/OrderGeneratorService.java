package br.com.mindmaster.shedlock.order.service;

import br.com.mindmaster.shedlock.order.entity.Order;
import br.com.mindmaster.shedlock.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderGeneratorService {

    private final OrderRepository orderRepository;

    public void execute() {
        log.debug("Starting order generation...");
        Order order = new Order();
        orderRepository.save(order);
        log.info("New order generated and saved. orderId={}", order.getId());

    }

}
