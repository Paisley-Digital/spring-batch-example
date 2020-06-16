package digital.paisley.sbs.processors;

import digital.paisley.sbs.entities.Orders;
import org.springframework.batch.item.ItemProcessor;

public class DBLogProcessor implements ItemProcessor<Orders, Orders> {
    public Orders process(Orders orders) throws Exception
    {
        System.out.println("Inserting store order : " + orders);
        return orders;
    }
}
