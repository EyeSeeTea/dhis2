package org.hisp.dhis.interceptor;

import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.system.database.DatabaseInfoProvider;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class ContextInterceptor
    implements Interceptor
{
    private static final String KEY_IN_MEMORY_DATABASE = "inMemoryDatabase";
    
    @Autowired
    private DatabaseInfoProvider databaseInfoProvider;

    @Override
    public void destroy()
    {
    }

    @Override
    public void init()
    {
    }

    @Override
    public String intercept( ActionInvocation invocation )
        throws Exception
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put( KEY_IN_MEMORY_DATABASE, databaseInfoProvider.isInMemory() );
        invocation.getStack().push( map );
        return invocation.invoke();
    }
}
