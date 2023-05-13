package com.sample.scheduler;

import com.sample.model.Company;
import com.sample.model.ScrapedResult;
import com.sample.model.constants.CacheKey;
import com.sample.persist.CompanyRepository;
import com.sample.persist.DividendRepository;
import com.sample.persist.entity.CompanyEntity;
import com.sample.persist.entity.DividendEntity;
import com.sample.scaper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@EnableCaching
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;

    private DividendRepository dividendRepository;
    private final Scraper yahooFinanceScraper;

    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        List<CompanyEntity> companies = companyRepository.findAll();

        for (CompanyEntity company : companies) {
            log.info("scraping scheduler is started -> " + company.getName());

            ScrapedResult scrapedResult = yahooFinanceScraper.scrap(
                    new Company(company.getName(), company.getTicker()));

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
