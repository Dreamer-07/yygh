package pers.prover07.yygh.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public enum DictEnum {

    /**
     * 医院等级
     */
    HOSTYPE("Hostype", "医院等级"),

    /**
     * 证件类型
     */
    CERTIFICATES_TYPE("CertificatesType", "证件类型"),
    ;

    private String dictCode;
    private String msg;

}
