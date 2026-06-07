package com.ndd.digitallibrary.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AccessLogResponse {

    private int lastReadPage;
    private LocalDateTime lastReadAt;
    private boolean hasDownloaded;

    private DocumentSummaryResponse documentSummaryResponse;

}
