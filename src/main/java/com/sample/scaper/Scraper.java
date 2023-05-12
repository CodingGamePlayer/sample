package com.sample.scaper;

import com.sample.model.Company;
import com.sample.model.ScrapedResult;

public interface Scraper {
    Company scarpCompanyByTicker(String ticker);

    ScrapedResult scrap(Company company);

}
