package com.assemble.crm.report.service;

import com.assemble.crm.common.security.SecurityUtils;
import com.assemble.crm.customer.entity.CustomerStatus;
import com.assemble.crm.customer.repository.CustomerRepository;
import com.assemble.crm.lead.entity.LeadStatus;
import com.assemble.crm.lead.repository.LeadRepository;
import com.assemble.crm.opportunity.entity.OpportunityStage;
import com.assemble.crm.opportunity.entity.OpportunityStatus;
import com.assemble.crm.opportunity.repository.OpportunityRepository;
import com.assemble.crm.report.dto.*;
import com.assemble.crm.task.entity.TaskStatus;
import com.assemble.crm.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final CustomerRepository customerRepository;
    private final LeadRepository leadRepository;
    private final OpportunityRepository opportunityRepository;
    private final TaskRepository taskRepository;

    public ReportService(CustomerRepository customerRepository, LeadRepository leadRepository,
                         OpportunityRepository opportunityRepository, TaskRepository taskRepository) {
        this.customerRepository = customerRepository;
        this.leadRepository = leadRepository;
        this.opportunityRepository = opportunityRepository;
        this.taskRepository = taskRepository;
    }

    public DashboardResponse dashboard() {
        Long companyId = SecurityUtils.currentCompanyId();
        return new DashboardResponse(
                customerRepository.countByCompanyId(companyId),
                customerRepository.countByCompanyIdAndStatus(companyId, CustomerStatus.ACTIVE),
                leadCount(companyId),
                leadRepository.countByCompanyIdAndStatus(companyId, LeadStatus.NEW),
                leadRepository.countByCompanyIdAndStatus(companyId, LeadStatus.QUALIFIED),
                leadRepository.countByCompanyIdAndStatus(companyId, LeadStatus.CONVERTED),
                opportunityRepository.countByCompanyIdAndStatus(companyId, OpportunityStatus.OPEN),
                opportunityRepository.countByCompanyIdAndStatus(companyId, OpportunityStatus.WON),
                opportunityRepository.sumValueByStatus(companyId, OpportunityStatus.OPEN),
                opportunityRepository.sumValueByStatus(companyId, OpportunityStatus.WON),
                taskRepository.countByCompanyIdAndStatus(companyId, TaskStatus.PENDING),
                taskRepository.countByCompanyIdAndStatus(companyId, TaskStatus.OVERDUE)
        );
    }

    private long leadCount(Long companyId) {
        long total = 0;
        for (LeadStatus s : LeadStatus.values()) {
            total += leadRepository.countByCompanyIdAndStatus(companyId, s);
        }
        return total;
    }

    public List<PipelineStageDto> pipeline() {
        Long companyId = SecurityUtils.currentCompanyId();
        return opportunityRepository.pipelineByStage(companyId).stream()
                .map(row -> new PipelineStageDto(
                        (OpportunityStage) row[0],
                        ((Number) row[1]).longValue(),
                        toBigDecimal(row[2])))
                .toList();
    }

    private static BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd) return bd;
        return new BigDecimal(value.toString());
    }

    public List<SourceCountDto> leadsBySource() {
        Long companyId = SecurityUtils.currentCompanyId();
        return leadRepository.countBySource(companyId).stream()
                .map(row -> new SourceCountDto(
                        row[0] != null ? row[0].toString() : "UNKNOWN",
                        ((Number) row[1]).longValue()))
                .toList();
    }

    public SalesPerformanceDto salesPerformance() {
        Long companyId = SecurityUtils.currentCompanyId();
        long won = opportunityRepository.countByCompanyIdAndStatus(companyId, OpportunityStatus.WON);
        long lost = opportunityRepository.countByCompanyIdAndStatus(companyId, OpportunityStatus.LOST);
        BigDecimal wonValue = opportunityRepository.sumValueByStatus(companyId, OpportunityStatus.WON);
        double winRate = (won + lost) == 0 ? 0.0 : (double) won / (won + lost) * 100.0;
        return new SalesPerformanceDto(won, lost, wonValue, Math.round(winRate * 100.0) / 100.0);
    }

    public TasksSummaryDto tasksSummary() {
        Long companyId = SecurityUtils.currentCompanyId();
        return new TasksSummaryDto(
                taskRepository.countByCompanyIdAndStatus(companyId, TaskStatus.PENDING),
                taskRepository.countByCompanyIdAndStatus(companyId, TaskStatus.IN_PROGRESS),
                taskRepository.countByCompanyIdAndStatus(companyId, TaskStatus.COMPLETED),
                taskRepository.countByCompanyIdAndStatus(companyId, TaskStatus.OVERDUE)
        );
    }

    /** Builds a simple CSV of the pipeline for export. */
    public String pipelineCsv() {
        StringBuilder sb = new StringBuilder("stage,count,total_value\n");
        for (PipelineStageDto dto : pipeline()) {
            sb.append(dto.stage()).append(',')
              .append(dto.count()).append(',')
              .append(dto.totalValue()).append('\n');
        }
        return sb.toString();
    }
}
