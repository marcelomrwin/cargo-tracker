package org.eclipse.cargotracker.interfaces.handling.file;

import jakarta.batch.api.listener.JobListener;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;

import java.util.Date;

@Dependent
@Named("FileProcessorJobListener")
public class FileProcessorJobListener implements JobListener {

  @Inject private Logger logger;

  @Override
  public void beforeJob() throws Exception {
    logger.info("Handling event file processor batch job starting at {}", new Date());
  }

  @Override
  public void afterJob() throws Exception {
    logger.info("Handling event file processor batch job completed at {}", new Date());
  }
}
