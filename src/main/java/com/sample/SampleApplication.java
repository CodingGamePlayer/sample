package com.sample;

import com.sample.model.Company;
import com.sample.model.ScrapedResult;
import com.sample.scaper.Scraper;
import com.sample.scaper.YahooFinanceScraper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);

    }

}
