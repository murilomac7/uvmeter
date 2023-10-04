package org.jobrunr.examples.services;

import com.google.gson.Gson;
import org.jobrunr.examples.domain.OpenUvResult;
import org.jobrunr.examples.domain.Result;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardProgressBar;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.spring.annotations.Recurring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class MyService implements MyServiceInterface {

    private static final Logger LOGGER = new JobRunrDashboardLogger(LoggerFactory.getLogger(MyService.class));

    @Recurring(id = "buscarDadosOpenUv", cron = "0 * * * *")
    @Job(name = "Buscar indice de radiação no OpenUV")
    public void buscarDadosOpenUv(JobContext jobContext) {
        try {
            LOGGER.warn("Buscando dados na OpenUV API...");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.openuv.io/api/v1/uv?lat=37.28336227397032&lng=-121.87291519356863"))
                    .headers("x-access-token", "openuv-32315harlnbszy0l-io")
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient
                    .newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                Gson g = new Gson();
                OpenUvResult openUvResult = g.fromJson(response.body(), OpenUvResult.class);

                int uvCalculado = (int)openUvResult.result.uv;
                LOGGER.warn("Indice UV atual: " + uvCalculado);
            }
            else
                LOGGER.warn(String.valueOf(response.statusCode()));

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
