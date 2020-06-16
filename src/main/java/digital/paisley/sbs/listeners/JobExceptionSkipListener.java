package digital.paisley.sbs.listeners;

import digital.paisley.sbs.entities.Orders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.OnProcessError;
import org.springframework.batch.core.annotation.OnReadError;
import org.springframework.batch.core.annotation.OnSkipInWrite;

@Slf4j
public class JobExceptionSkipListener {

    @OnReadError
    public void problemOnRead(Exception ex) {
        log.error("Error on Reading CSV is:" + ex.getMessage());
    }

    @OnProcessError
    public void problemOnProcess(Orders orders, Exception ex) {
        log.info("Error on Processing  is:" + ex.getMessage() + " Input is:" + orders.toString());
    }

    @OnSkipInWrite
    public void problemOnWrite(Orders orders, Throwable ex) {
        log.info("Error on Writing  is:" + ex.getMessage());
    }

}
