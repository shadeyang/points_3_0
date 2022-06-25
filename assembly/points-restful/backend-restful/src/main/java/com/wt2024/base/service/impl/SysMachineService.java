package com.wt2024.base.service.impl;

import com.wt2024.base.entity.SysMachine;
import com.wt2024.base.mapper.SysMachineMapper;
import com.wt2024.base.service.ISysMachineService;
import com.wt2024.base.utils.CacheMachineUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName SysMachineService
 * @Description: TODO
 * @Author shade.yang
 * @Date 2020/6/15
 * @Version V1.0
 **/
@Component
public class SysMachineService implements ISysMachineService {

    @Autowired
    public SysMachineMapper sysMachineMapper;

    @Override
    public int updateOrInsertMachine(SysMachine sysMachine) {
        return sysMachineMapper.updateOrInsert(sysMachine);
    }

    @Override
    public SysMachine queryAndSaveSysMachine(SysMachine sysMachine) {
        this.updateOrInsertMachine(sysMachine);
        return sysMachineMapper.querySysMachine(sysMachine);
    }

    @Override
    public SysMachine queryAndSaveServerSysMachine() {
        return this.queryAndSaveSysMachine(CacheMachineUtils.genMachine());
    }

    @Override
    public int stopServerSysMachine() {
        SysMachine machine = CacheMachineUtils.genMachine();
        machine.setDescribe("服务下线");
        return this.updateOrInsertMachine(machine);
    }

    @Override
    public int heartbeatServerSysMachine() {
        SysMachine machine = CacheMachineUtils.genMachine();
        machine.setDescribe("存活心跳");
        return this.updateOrInsertMachine(machine);
    }
}
