package com.onestore.ds.preprocess;

import lombok.Data;

/**
 * Created by a1000074 on 16/01/2020.
 */
@Data
public class VoAppMeta {
    private String mainCategory ;
    private String subCategory ;
    private String prodId ;
    private String prodName ;
    private String regDate ;
    private long useSec ;
    private long cntUsers ;

    public String[] getCsvElementSet() {
        return new String[] {this.mainCategory, this.subCategory, this.prodId, this.prodName,
        this.regDate, String.valueOf(this.cntUsers), String.valueOf(this.useSec)};
    }
}
