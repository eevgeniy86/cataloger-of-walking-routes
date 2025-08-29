package ru.otus;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.model.service.DBServiceRoute;
import ru.otus.webclient.OsrmHttpClient;

@Slf4j
@AllArgsConstructor
@Component("SuppliersLauncher")
public class SuppliersLauncher implements CommandLineRunner {
    private final DBServiceRoute dbServiceRoute;
    private final OsrmHttpClient httpClient;

    @Override
    public void run(String... args) {
        //
        //        Thread distancesThread = new Thread(new DistancesSupplier(dbServiceRoute, httpClient),
        // "DistancesSupplier");
        //        distancesThread.start();
    }
}
