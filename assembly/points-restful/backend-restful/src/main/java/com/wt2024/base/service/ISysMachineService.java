package com.wt2024.base.service;

import com.wt2024.base.entity.SysMachine;

/**
 * @ClassName ISysMachineService
 * @Description: TODO
 * @Author shade.yang
 * @Date 2020/6/15
 * @Version V1.0
 **/
public interface ISysMachineService {

    int updateOrInsertMachine(SysMachine sysMachine);

    SysMachine queryAndSaveSysMachine(SysMachine sysMachine);

    SysMachine queryAndSaveServerSysMachine();

    int stopServerSysMachine();

    int heartbeatServerSysMachine();
}
