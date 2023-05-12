package com.sample.service;

import com.sample.model.Company;
import com.sample.model.Dividend;
import com.sample.model.ScrapedResult;
import com.sample.persist.CompanyRepository;
import com.sample.persist.DividendRepository;
import com.sample.persist.entity.CompanyEntity;
import com.sample.persist.entity.DividendEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public ScrapedResult getDividendByCompanyName(String companyName) {

        CompanyEntity company = companyRepository.findByName(companyName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회사명입니다."));

        List<DividendEntity> dividendEntities = dividendRepository.findAllByCompanyId(company.getId());

        return new ScrapedResult(Company.builder()
                .ticker(company.getTicker())
                .name(company.getName())
                .build(),
                dividendEntities.stream()
                        .map(dividendEntity -> Dividend.builder()
                                .date(dividendEntity.getDate())
                                .dividend(dividendEntity.getDividend())
                                .build())
                        .collect(Collectors.toList()));
    }

}
