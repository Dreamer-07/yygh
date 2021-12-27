package pers.prover07.yygh.vo.hosp;

import lombok.Data;

import java.util.List;

/**
 * @Classname DepartmentVo
 * @Description TODO
 * @Date 2021/11/29 15:06
 * @Created by Prover07
 */
@Data
public class DepartmentVo {

    private String depcode;

    private String depname;

    private List<DepartmentVo> children;

}
