package com.wt2024.points.core.handler.trans;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.core.handler.trans.vo.AbstractTransVo;
import com.wt2024.points.repository.api.cache.repository.CacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName TransHandlerBase
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/7/1
 * @Version V1.0
 **/
@Slf4j
public abstract class AbstractTransHandler<T extends AbstractTransVo> implements TransHandlerInterface<T> {

    protected ApplicationContext applicationContext;

    private CacheRepository cacheRepository;

    protected AbstractTransHandler(ApplicationContext applicationContext, CacheRepository cacheRepository) {
        log.info("init handler {}", this.getClass());
        this.applicationContext = applicationContext;
        this.cacheRepository = cacheRepository;
    }

    @Override
    public T execute(T vo) {
        try {
            initLock(vo);
            if (check(vo)) {
                pointsAccount(vo);
                businessAccount(vo);
                log.debug("success call");
                successCallBack(vo);
                event(vo);
            }
            if (vo.isFailCalled()) {
                log.debug("fail call, code[{}] message=[{}]", vo.getCode(), vo.getMessage());
                failCallBack(vo);
            }
            if (!vo.success()) {
                throw new PointsException(vo.getCode(), vo.getMessage());
            }
        } catch (PointsException e) {
            throw e;
        } catch (Exception e) {
            log.error("execute error", e);
            throw new PointsException(PointsCode.TRANS_0005);
        } finally {
            unLock(vo);
        }
        return vo;
    }

    protected abstract void event(T vo);

    /**
     * 失败回调方法
     */
    protected abstract void failCallBack(T vo) throws PointsException;

    /**
     * 成功回调方法
     */
    protected abstract void successCallBack(T vo) throws PointsException;

    /**
     * 加锁key
     */
    protected abstract void initLock(T vo);

    /**
     * 数据检查阶段
     *
     * @return
     */
    protected abstract boolean check(T vo);

    /**
     * 客户积分处理
     */
    protected abstract void pointsAccount(T vo);

    /**
     * 商户积分处理
     */
    protected abstract void businessAccount(T vo);

    /**
     * 并不是所有锁一开始就知道加什么，因此需要在基于一些场景下，进行手动加锁
     * @param vo
     * @param key
     */
    protected void lock(T vo, String key) {
        log.debug("lock {}", key);
        if (!cacheRepository.lock(key, 10, TimeUnit.MINUTES)) {
            throw new PointsException(PointsCode.TRANS_2009);
        }
        vo.getLockSuccessKeys().add(key);
    }

    private void unLock(T vo) {
        List<String> cacheLockKeys = vo.getLockSuccessKeys();
        for (String key : cacheLockKeys) {
            log.debug("unlock {}", key);
            cacheRepository.unLock(key);
        }
    }

    public void failCalled(T vo) {
        log.info("set fail call function flag");
        vo.setFailCalled(true);
    }
}
