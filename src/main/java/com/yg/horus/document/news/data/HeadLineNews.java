package com.yg.horus.document.news.data;

import lombok.Data;

/**
 * Created by jeff on 19. 3. 30.
 */
@Data
public class HeadLineNews {
    private String id ;
    private String titleAnchor;
    private String summary ;
    private String timestamp ;
    private String url ;
    private String issuer ;

}
