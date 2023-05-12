package com.sample.scheduler;

import com.sample.model.Company;
import com.sample.model.ScrapedResult;
import com.sample.persist.CompanyRepository;
import com.sample.persist.DividendRepository;
import com.sample.persist.entity.CompanyEntity;
import com.sample.persist.entity.DividendEntity;
import com.sample.scaper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;

    private DividendRepository dividendRepository;
    private final Scraper yahooFinanceScraper;

    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        List<CompanyEntity> companies = companyRepository.findAll();

        for (CompanyEntity company : companies) {
            log.info("scraping scheduler is started -> " + company.getName());

            ScrapedResult scrapedResult = yahooFinanceScraper.scrap(Company.builder()
                    .name(company.getName())
                    .ticker(company.getTicker())
                    .build());

            scrapedResult.getDividendEntities().stream()
                    .map(e -> new DividendEntity(company.getId(), e))
                    .forEach(e -> {
                                boolean exists = dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());

                                if (!exists) {
                                    dividendRepository.save(e);
                                }
                            }
                    );

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
