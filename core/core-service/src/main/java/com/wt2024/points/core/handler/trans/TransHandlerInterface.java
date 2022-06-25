package com.wt2024.points.core.handler.trans;


import com.wt2024.points.core.handler.trans.vo.AbstractTransVo;

/**
 * @ClassName TransHandlerInterface
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/7/1
 * @Version V1.0
 **/
public interface TransHandlerInterface<T extends AbstractTransVo> {

    /**
     * 执行方法
     */
    T execute(T vo);
}
