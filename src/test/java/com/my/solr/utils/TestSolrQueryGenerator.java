package com.my.solr.utils;

import org.junit.jupiter.api.Test;

public class TestSolrQueryGenerator {
	
	@Test
	public void testSolrQueryGenerator() {
		SolrQueryGenerator queryGenerator = new SolrQueryGenerator()
				.param("writer", "hong gildong")
			.and()
				.param("content", "written by heo gyun")
			.and()
				.andParams(
					"auth", "user",
					"phone", "010-1111-1111"
				)
			.or()
				.range("date", "2020-01-11", "2020-01-31")
			.and()
				.not()
				.param("age", 30)
			.and()
				.param("idx_writer", "hong gildong honggildong")
				.score(100);
		
		System.out.println(queryGenerator.toQueryString());
	}

}
