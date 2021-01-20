package com.my.solr.utils;

import java.util.function.Consumer;

public class SolrQueryGenerator {

	private enum SolrQueryOperator {
		AND(generator -> generator.and()),
		OR(generator -> generator.or())
		
		;
		
		private Consumer<SolrQueryGenerator> consumer;
		
		SolrQueryOperator(Consumer<SolrQueryGenerator> consumer) {
			this.consumer = consumer;
		}
	}
	
	private StringBuilder queryBuilder = new StringBuilder();
	private String lastParamString;
	
	public SolrQueryGenerator param(String name, Object value) {
		
		lastParamString = new StringBuilder()
				.append(name)
				.append(':')
				.append(hasSpace(value) ? '(' : "")
				.append(value)
				.append(hasSpace(value) ? ')' : "")
				.toString();
		
		queryBuilder
			.append(lastParamString);
		return this;
	}
	
	public SolrQueryGenerator range(String name, Object start, Object end) {
		queryBuilder
			.append('(')
			.append(name)
			.append(':')
			.append('[')
			.append(start)
			.append(" TO ")
			.append(end)
			.append(']')
			.append(')');
		return this;
	}
	
	public SolrQueryGenerator and() {
		queryBuilder
			.append(" AND ");
		return this;
	}
	
	public SolrQueryGenerator or() {
		queryBuilder
			.append(" OR ");
		return this;
	}
	
	public SolrQueryGenerator not() {
		queryBuilder
			.append('-');
		return this;
	}
	
	public SolrQueryGenerator startAnd() {
		queryBuilder
			.append(" AND ")
			.append(" (");
		return this;
	}
	
	public SolrQueryGenerator endEnd() {
		return this.close();
	}
	
	public SolrQueryGenerator startOr() {
		queryBuilder
			.append(" OR ")
			.append(" (");
		return this;
	}
	
	public SolrQueryGenerator endOr() {
		return this.close();
	}
	
	public SolrQueryGenerator andParams(Object ...params) {
		return this.params(SolrQueryOperator.AND, params);
	}
	
	public SolrQueryGenerator orParams(Object ...params) {
		return this.params(SolrQueryOperator.OR, params);
	}
	
	public SolrQueryGenerator open() {
		queryBuilder
			.append('(');
		return this;
	}
	
	public SolrQueryGenerator close() {
		queryBuilder
			.append(')');
		return this;
	}
	
	public SolrQueryGenerator score(int score) {
		StringBuilder sb = new StringBuilder()
			.append('(')
			.append(lastParamString)
			.append(')')
			.append('^')
			.append(score);
		queryBuilder.replace(queryBuilder.lastIndexOf(lastParamString), queryBuilder.length(), sb.toString());
		return this;
	}

	public SolrQueryGenerator append(String str) {
		queryBuilder
			.append(str);
		return this;
	}
	
	public String toQueryString() {
		return queryBuilder.toString();
	}
	
	private boolean hasSpace(Object value) {
		return String.valueOf(value).contains(" ");
	}
	
	private SolrQueryGenerator params(SolrQueryOperator operator, Object ...params) {
		if (params.length % 2 != 0) 
			throw new IllegalArgumentException("params' length must be even number");
		
		queryBuilder.append('(');
		
		for (int i = 0; i < params.length - 1; i += 2) {
			String name = Optional.ofNullable(String.valueOf(params[i]))
				.orElseThrow(() -> new IllegalArgumentException("name must be string."));
			Object value = params[i + 1];
			this.param(name, value);
			
			if (i != params.length - 2) operator.consumer.accept(this);
		}
		
		queryBuilder.append(')');
		
		return this;
	}
}
