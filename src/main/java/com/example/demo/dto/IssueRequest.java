package com.example.demo.dto;

public class IssueRequest {
    private Long bookId;
    private Long userId;
    private Integer days;

    public IssueRequest() {}

    public IssueRequest(Long bookId, Long userId, Integer days) {
        this.bookId = bookId;
        this.userId = userId;
        this.days = days;
    }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getDays() { return days; }
    public void setDays(Integer days) { this.days = days; }
}
