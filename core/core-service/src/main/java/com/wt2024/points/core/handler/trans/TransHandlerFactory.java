package com.wt2024.points.core.handler.trans;

import com.wt2024.points.core.handler.trans.vo.AbstractTransVo;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName TransHandlerFactory
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/8/25
 * @Version V1.0
 **/
@Setter
@Getter
public class TransHandlerFactory<T extends AbstractTransVo> {
    private T result;
    private AbstractTransHandler handler;
    private static Map<AbstractTransHandler, TransHandlerFactory> transHandlerFactoryMap = new ConcurrentHashMap();

    public <C extends AbstractTransHandler<T>> TransHandlerFactory(C transHandler) {
        this.handler = transHandler;
    }

    public <C extends AbstractTransHandler<T>> TransHandlerFactory(C transHandler, T vo) {
        this.handler = transHandler;
        this.result = transHandler.execute(vo);
    }

    public static <C extends AbstractTransHandler> TransHandlerFactory factory(C transHandler) {
        if (transHandlerFactoryMap.containsKey(transHandler)) {
            return transHandlerFactoryMap.get(transHandler);
        }
        TransHandlerFactory factory = new TransHandlerFactory(transHandler);
        transHandlerFactoryMap.put(transHandler, factory);
        return factory;
    }

    public T execute(T vo) {
        return (T)this.handler.execute(vo);
    }
}
