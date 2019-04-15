package pico.erp.rest.config;

import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.val;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import pico.erp.project.ProjectService;
import pico.erp.project.charge.ProjectChargeId;
import pico.erp.project.charge.ProjectChargeRequests;
import pico.erp.project.charge.ProjectChargeService;
import pico.erp.project.sale.item.ProjectSaleItemId;
import pico.erp.project.sale.item.ProjectSaleItemRequests;
import pico.erp.project.sale.item.ProjectSaleItemService;
import pico.erp.quotation.QuotationEvents.CanceledEvent;
import pico.erp.quotation.QuotationEvents.CommittedEvent;
import pico.erp.quotation.QuotationService;
import pico.erp.quotation.addition.QuotationAdditionService;
import pico.erp.quotation.item.QuotationItemService;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
public class ProjectConfiguration {

  private static final String LISTENER_NAME = "listener.project-configuration";

  @ComponentAutowired
  private ProjectService projectService;

  @ComponentAutowired
  private ProjectChargeService projectChargeService;

  @ComponentAutowired
  private ProjectSaleItemService projectSaleItemService;

  @ComponentAutowired
  private QuotationService quotationService;

  @ComponentAutowired
  private QuotationItemService quotationItemService;

  @ComponentAutowired
  private QuotationAdditionService quotationAdditionService;

  /**
   * 견적이 취소 되면 해당 품목과 관련된 프로젝트 판매 품목을 제거
   */
  @EventListener
  @JmsListener(destination = LISTENER_NAME + "." + CanceledEvent.CHANNEL)
  public void onQuotationCanceled(CanceledEvent event) {
    val quotation = quotationService.get(event.getQuotationId());
    val project = projectService.get(quotation.getProjectId());

    quotationItemService.getAll(event.getQuotationId()).forEach(item -> {
      val exists = projectSaleItemService.exists(project.getId(), item.getItemId());
      if (exists) {
        val saleItem = projectSaleItemService.get(project.getId(), item.getItemId());
        projectSaleItemService.delete(
          new ProjectSaleItemRequests.DeleteRequest(saleItem.getId())
        );
      }
    });
    quotationAdditionService.getAll(event.getQuotationId()).forEach(addition -> {
      val id = ProjectChargeId.from(addition.getId().getValue());
      val exists = projectChargeService.exists(id);
      if (exists) {
        val charge = projectChargeService.get(id);
        if (!charge.isPaid()) {
          projectChargeService.delete(
            new ProjectChargeRequests.DeleteRequest(charge.getId())
          );
        }
      }
    });
  }

  /**
   * 견적이 제출 되면 해당 품목을 프로젝트 판매 품목으로 등록
   */
  @EventListener
  @JmsListener(destination = LISTENER_NAME + "." + CommittedEvent.CHANNEL)
  public void onQuotationCommitted(CommittedEvent event) {
    val quotation = quotationService.get(event.getQuotationId());
    val project = projectService.get(quotation.getProjectId());

    quotationItemService.getAll(event.getQuotationId()).forEach(item -> {
      val exists = projectSaleItemService.exists(project.getId(), item.getItemId());
      if (!exists) {
        projectSaleItemService.create(
          ProjectSaleItemRequests.CreateRequest.builder()
            .id(ProjectSaleItemId.from(item.getId().getValue()))
            .projectId(quotation.getProjectId())
            .itemId(item.getItemId())
            .expirationDate(quotation.getExpirationDate())
            .unitPrice(item.getFinalizedUnitPrice())
            .build()
        );
      }
    });
    quotationAdditionService.getAll(event.getQuotationId()).forEach(addition -> {
      val id = ProjectChargeId.from(addition.getId().getValue());
      val exists = projectChargeService.exists(id);
      if (!exists) {
        projectChargeService.create(
          ProjectChargeRequests.CreateRequest.builder()
            .id(id)
            .name(addition.getName())
            .projectId(quotation.getProjectId())
            .quantity(addition.getQuantity())
            .unitPrice(addition.getUnitPrice())
            .build()
        );
      }
    });
  }

}
