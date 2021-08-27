package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.domain.mongo.DailyOpsLoadConfig;
import com.moblize.ms.dailyops.dto.TrueRopCache;
import com.moblize.ms.dailyops.dto.WellCoordinatesResponseV2;
import com.moblize.ms.dailyops.repository.mongo.client.WellPerformanceMetaDataRepository;
import com.moblize.ms.dailyops.repository.mongo.mob.MongoWellRepository;
import com.moblize.ms.dailyops.service.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.client.hotrod.DefaultTemplate;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class CacheService {
    @Autowired
    private RemoteCacheManager cacheManager;
    @Autowired
    @Lazy
    private WellsCoordinatesService wellsCoordinatesService;
    @Autowired
    private TrueRopCacheListener trueRopCacheListener;
    @Autowired
    private WellPerformanceMetaDataRepository metaDataRepository;
    @Autowired
    private RestClientService restClientService;
    @Autowired
    private MongoWellRepository mongoWellRepository;
    @Autowired
    private SurveyDataCacheListener surveyDataCacheListener;
    @Autowired
    private WellPlanDataCacheListener wellPlanDataCacheListener;
    @Autowired
    private NotifyDPVAService notifyDPVAService;

    @Value("${CODE}")
    String COMPANY_NAME;
    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void subscribe() {
        getWellCoordinatesCache().clear();
        log.info("Cache service start");
        DailyOpsLoadConfig dailyOpsLoadConfig =   notifyDPVAService.getDailyOpsLoadConfig(COMPANY_NAME);
        if(dailyOpsLoadConfig != null && !dailyOpsLoadConfig.getIsPerformanceMapCalculated()) {
            metaDataRepository.findAll().stream().forEach(metaData -> {
                    log.info("Cache service process well {}", metaData.getWellUid());
                    restClientService.processWell(mongoWellRepository.findByUid(metaData.getWellUid()));
            });
        }
        log.info("Cache service end");

       notifyDPVAService.loadDPVAData(COMPANY_NAME, dailyOpsLoadConfig);

        wellsCoordinatesService.getWellCoordinates(COMPANY_NAME);
        getTrueRopMetaCache().addClientListener(trueRopCacheListener);
        getSurveyDataCache().addClientListener(surveyDataCacheListener);
        getPlanDataCache().addClientListener(wellPlanDataCacheListener);
    }


    public RemoteCache<String, TrueRopCache> getTrueRopMetaCache() {
        RemoteCache<String, TrueRopCache> cache = cacheManager.administration()
            .getOrCreateCache(COMPANY_NAME + "_WellRopDepth", DefaultTemplate.DIST_ASYNC);
        return  cache;
    }
    public RemoteCache<String, MongoWell> getMongoWellCache() {
        RemoteCache<String, MongoWell> cache = cacheManager.administration()
            .getOrCreateCache(COMPANY_NAME + "_MongoWell", DefaultTemplate.DIST_ASYNC);
        return cache;
    }

    public RemoteCache<String, WellCoordinatesResponseV2> getWellCoordinatesCache() {
        RemoteCache<String, WellCoordinatesResponseV2> cache = cacheManager.administration()
            .getOrCreateCache(COMPANY_NAME + "_WellCoordinates", DefaultTemplate.DIST_ASYNC);
        return cache;
    }

    public RemoteCache<String, SurveyCacheDTO> getSurveyDataCache() {
        RemoteCache<String, SurveyCacheDTO> cache = cacheManager.administration()
            .getOrCreateCache(COMPANY_NAME + "_wellSurveyData", DefaultTemplate.DIST_ASYNC);
        return  cache;
    }
    public RemoteCache<String, WellPlanCacheDTO> getPlanDataCache() {
        RemoteCache<String, WellPlanCacheDTO> cache = cacheManager.administration()
            .getOrCreateCache(COMPANY_NAME + "_wellPlanData", DefaultTemplate.DIST_ASYNC);
        return  cache;
    }

    public RemoteCache<String, SurveyPerFeetDTO> getPerFeetSurveyDataCache() {
        RemoteCache<String, SurveyPerFeetDTO> cache = cacheManager.administration()
            .getOrCreateCache(COMPANY_NAME + "_wellPerFeetSurveyData", DefaultTemplate.DIST_ASYNC);
        return  cache;
    }
    public RemoteCache<String, PlannedPerFeetDTO> getPerFeetPlanDataCache() {
        RemoteCache<String, PlannedPerFeetDTO> cache = cacheManager.administration()
            .getOrCreateCache(COMPANY_NAME + "_wellPerFeetPlanData", DefaultTemplate.DIST_ASYNC);
        return  cache;
    }

    public RemoteCache<String, TargetWindowPerFootDTO> getPerFeetTargetWindowDataCache() {
        RemoteCache<String, TargetWindowPerFootDTO> cache = cacheManager.administration()
            .getOrCreateCache(COMPANY_NAME + "_wellPerFeetTargetWindowData", DefaultTemplate.DIST_ASYNC);
        return  cache;
    }
}
