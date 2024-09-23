package com.task.books.request;

import lombok.Data;

@Data
public class BookRateRequest {
    Integer clientId;
    Integer rating;
}
