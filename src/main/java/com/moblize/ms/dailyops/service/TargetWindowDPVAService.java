package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.mongo.TargetWindowDPVA;
import com.moblize.ms.dailyops.repository.mongo.client.TargetWindowDPVARepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TargetWindowDPVAService {

    @Autowired
    private TargetWindowDPVARepository targetWindowDPVARepository;

    @Autowired
    private NotifyDPVAService notifyDPVAService;



    public TargetWindowDPVA getTargetWindowDetail(String wellUID) {
        return targetWindowDPVARepository.findFirstByUid(wellUID);
    }

    public TargetWindowDPVA saveTargetWindowDetail(TargetWindowDPVA targetWindow) {
        TargetWindowDPVA Obj = null;
        try {
            TargetWindowDPVA targetWindowDB = getTargetWindowDetail(targetWindow.getUid());
            if (null != targetWindowDB) {
                targetWindowDB.setIsEnable(targetWindow.getIsEnable());
                targetWindowDB.setSelectedMode(targetWindow.getSelectedMode());
                targetWindowDB.setAdvance(targetWindow.getAdvance());
                targetWindowDB.setBasic(targetWindow.getBasic());
                Obj = targetWindowDPVARepository.save(targetWindowDB);
            } else {
                Obj = targetWindowDPVARepository.save(targetWindow);
            }
            notifyDPVAService.notifyDPVAJob(targetWindow);
        } catch (Exception e) {
            log.error("Error occure in saveTarget window service",e);
        }
        return Obj;
    }
}
