package et.nate.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class ApplicationEventConfig {
    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster eventMulticasterProvider() {
        try(var taskExecutor = new SimpleAsyncTaskExecutor()) {
            SimpleApplicationEventMulticaster eventMulticaster =
                    new SimpleApplicationEventMulticaster();
            taskExecutor.setVirtualThreads(true);
            eventMulticaster.setTaskExecutor(taskExecutor);
            return eventMulticaster;
        }
    }
}
