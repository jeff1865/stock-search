package com.yg.horus.document.stock.data;

import lombok.Data;

/**
 * Created by 1002000 on 2019. 3. 19..
 */
@Data
public class DailyInvestorValues {
    private String dateStamp ;
    private int individual ;
    private int foreigner ;
    private int institutional ;
    private int finance ;
    private int insurance ;
    private int trust ;
    private int bank ;
    private int etcFinance ;
    private int pension ;

    // ...
}
