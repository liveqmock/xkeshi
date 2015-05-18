package com.xkeshi.common.listener;

import com.xkeshi.common.globality.Initializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;


@SuppressWarnings("rawtypes")
@Component  
public class Initor implements ApplicationListener<ContextRefreshedEvent> {   
       
    @Autowired(required=false)   
    List<Initializable> initors;
       
    @Override  
    public void onApplicationEvent(ContextRefreshedEvent event) {   
  
        if(null==initors)   
        {   
            return;   
        }   
        if(event instanceof ContextRefreshedEvent)   
        {
            if(event.getApplicationContext().getParent() == null){
                for(Initializable initor:initors)
                {
                    initor.init();
                }
            }
        }
    }   
  
}  