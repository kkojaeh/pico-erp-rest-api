package pico.erp.parent;

import java.util.Set;
import javax.sql.DataSource;
import kkojaeh.spring.boot.component.SpringBootComponent;
import kkojaeh.spring.boot.component.SpringBootComponentParentReadyEvent;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.jdbc.datasource.DelegatingDataSource;

@SpringBootComponent("web-parent")
@SpringBootApplication(exclude = {
  DataSourceTransactionManagerAutoConfiguration.class,
  JpaRepositoriesAutoConfiguration.class,
  HibernateJpaAutoConfiguration.class,
  ActiveMQAutoConfiguration.class,
  JmsAutoConfiguration.class
})
@ComponentScan(useDefaultFilters = false)
public class WebParentApplication implements
  ApplicationListener<SpringBootComponentParentReadyEvent>, ApplicationContextInitializer {

  @Autowired
  private DataSource dataSource;

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    applicationContext.getBeanFactory()
      .registerSingleton("dataSource", new DelegatingDataSource(dataSource));
  }

  private void messageSource(ConfigurableApplicationContext parent,
    Set<ConfigurableApplicationContext> components) {
    val basenames = components.stream()
      .filter(
        component -> component.getBeanNamesForType(AbstractResourceBasedMessageSource.class).length
          > 0)
      .map(component -> component.getBean(AbstractResourceBasedMessageSource.class))
      .flatMap(messageSource -> messageSource.getBasenameSet().stream())
      .toArray(size -> new String[size]);

    val messageSource = parent.getBean(AbstractResourceBasedMessageSource.class);
    messageSource.addBasenames(basenames);
  }

  @Override
  public void onApplicationEvent(SpringBootComponentParentReadyEvent event) {
    messageSource(event.getParent(), event.getComponents());
  }

}
