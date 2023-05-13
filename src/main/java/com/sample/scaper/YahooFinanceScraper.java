package com.sample.scaper;

import com.sample.model.Company;
import com.sample.model.Dividend;
import com.sample.model.ScrapedResult;
import com.sample.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {
    private static final String STATICS_URL =
            "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";

    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400L;

    @Override
    public ScrapedResult scrap(Company company) {
        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);

        try {
            long now = System.currentTimeMillis() / 1000;

            String url = String.format(STATICS_URL, company.getTicker(), START_TIME, now);

            Connection connect = Jsoup.connect(url);
            Document document = connect.get();

            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableEle = parsingDivs.get(0);

            Element tbody = tableEle.children().get(1);
            List<Dividend> dividends = new ArrayList<>();

            for (Element child : tbody.children()) {
                String text = child.text();
                if (text.endsWith("Dividend")) {

                    String[] split = text.split(" ");
                    int month = Month.strToNumber(split[0]);
                    int day = Integer.valueOf(split[1].replace(",", ""));
                    int year = Integer.valueOf(split[2]);
                    String dividend = split[3];

                    if (month < 0) {
                        throw new RuntimeException("Unexpected Month enum value -> " + split[0]);
                    }

                    dividends.add(
                            new Dividend(LocalDateTime.of(year, month, day, 0, 0), dividend)
                    );


//                    System.out.println(year + "/" + month + "/" + day + " -> " + dividend);
                }
            }
            scrapResult.setDividendEntities(dividends);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return scrapResult;
    }

    @Override
    public Company scarpCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();

            Element titleEle = document.getElementsByTag("h1").get(0);

            String title = "";
            String[] split = titleEle.text().split(" ");
            for (int i = 0; i < 2; i++) {
                title += split[i];
                if (i == 0) {
                    title += " ";
                }
            }

            return new Company(ticker, title);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
