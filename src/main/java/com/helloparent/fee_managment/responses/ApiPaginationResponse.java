package com.helloparent.fee_managment.responses;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiPaginationResponse<T> {

	private int pageSize;
	private int pageNumber;
	private int totalPages;
	private boolean isLast;
	private boolean isFirst;
	private long totalRecords;
	private List<T> records;

}
