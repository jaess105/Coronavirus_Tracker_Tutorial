package de.jannik.coronavirustracker.services;

import de.jannik.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service //Marked as Spring Service
public class CoronaVirusDataService {
    //The Source that is used in the tutorial passes RAW .csv Data from the whole World. Today its only the USA cases that pass raw Data.
    private final static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_US.csv";

    private List<LocationStats> allStats = new ArrayList<>();

    public static String getVirusDataUrl() {
        return VIRUS_DATA_URL;
    }

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    @PostConstruct //When you Construct the instance of this service than start this
    @Scheduled(cron="* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        List<LocationStats> newStats = new ArrayList<>();

        HttpClient client=HttpClient.newHttpClient();
        HttpRequest request=HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();
        HttpResponse<String> httpResponse= client.send(request, HttpResponse.BodyHandlers.ofString());

        StringReader csvBodyReader=new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

        records.forEach(record->{
            LocationStats locationStat = new LocationStats();
            locationStat.setState(record.get("Province_State"));
            locationStat.setCountry(record.get("Country_Region"));

            Integer latestCases=Integer.parseInt(record.get(record.size() -1));
            Integer previousDateCases=Integer.parseInt(record.get(record.size() -2));

            locationStat.setLatestTotalCases(latestCases);
            locationStat.setDivFromPreviousDate(latestCases-previousDateCases);
            newStats.add(locationStat);
        });
        this.allStats=newStats;
    }
}
