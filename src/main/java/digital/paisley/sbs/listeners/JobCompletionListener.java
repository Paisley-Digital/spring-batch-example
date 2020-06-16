package digital.paisley.sbs.listeners;

import digital.paisley.sbs.entities.Orders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class JobCompletionListener extends JobExecutionListenerSupport {

    private final JdbcTemplate jdbcTemplate;

    public JobCompletionListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("Executing job id " + jobExecution.getId());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("============ JOB FINISHED ============ Verifying the results....\n");

            List<Orders> results = jdbcTemplate.query("SELECT * FROM PUBLIC.ORDERS",
                    (rs, row) -> Orders.builder()
                            .id(rs.getLong(1))
                            .orderId(rs.getString(2))
                            .build());

            for (Orders item : results) {
                log.info("Discovered Order Id<" + item.orderId + "> in the database.");
            }

        }
    }
}
