package pico.erp.restapi;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import pico.erp.attachment.AttachmentRequests.ClearRequest;
import pico.erp.attachment.AttachmentService;
import pico.erp.item.lot.ItemLotRequests;
import pico.erp.item.lot.ItemLotService;
import pico.erp.quotation.QuotationRequests;
import pico.erp.quotation.QuotationService;
import pico.erp.shared.ApplicationIntegrator;
import pico.erp.shared.ApplicationStarter;
import pico.erp.shared.impl.ApplicationIntegratorImpl;

public class Jobs {

  private final ApplicationIntegrator integrator;

  public Jobs() {
    integrator = new ApplicationIntegratorImpl();
    ServiceLoader<ApplicationStarter> loader = ServiceLoader.load(ApplicationStarter.class);
    List<ApplicationStarter> starters = new LinkedList<>();
    loader.forEach(starters::add);
    Collections.sort(starters);
    starters.forEach(starter -> integrator.add(starter.start()));
    integrator.integrate()
      .integrateMessageSource()
      .complete();
  }

  /**
   * 삭제된지 3개월 이상된 첨부는 물리적으로 완전 삭제
   */
  public void clearAttachments() {
    integrator.getBean(AttachmentService.class).clear(
      new ClearRequest(
        OffsetDateTime.now().minusMonths(3)
      )
    );
  }

  /**
   * 만료일이 지난 품목 LOT 는 만료 처리
   */
  public void expireItemLots() {
    integrator.getBean(ItemLotService.class).expire(
      new ItemLotRequests.ExpireRequest(
        OffsetDateTime.now()
      )
    );
  }

  /**
   * 만료일이 지난 품목 LOT 는 만료 처리
   */
  public void expireQuotations() {
    integrator.getBean(QuotationService.class).expire(
      new QuotationRequests.ExpireRequest(
        OffsetDateTime.now()
      )
    );
  }

  public void atAfterMidnight() {
    expireItemLots();
    expireQuotations();
    clearAttachments();

  }

}
