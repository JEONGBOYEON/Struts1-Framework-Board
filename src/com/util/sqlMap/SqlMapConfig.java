package com.util.sqlMap;

import java.io.Reader;



import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class SqlMapConfig {
	
	private static final SqlMapClient sqlMap;
	
		//결국 sqlMap 으로 삽입
		static{
			
			try {
				
				String resource ="com/util/sqlMap/sqlMapConfig.xml";
				Reader reader = Resources.getResourceAsReader(resource);
				
				sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Error initializing class:" + e);
			}
		}
	
		public static SqlMapClient getSqlMapInstance(){		//호출하면
			
			return sqlMap;									//String resource ="com/util/sqlMap/sqlMapConfig.xml"불러온다
			
		}
		
		
		
}
